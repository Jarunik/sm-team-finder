package org.slos.domain;

import org.slos.services.RealizedCollectionAdapter;
import org.slos.services.TeamInformationService;
import org.slos.splinterlands.TeamInfoResponse;
import org.slos.splinterlands.TeamInfoService;
import org.slos.splinterlands.collection.Collection;
import org.slos.splinterlands.collection.CollectionService;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.LevelLimit;
import org.slos.util.WithMDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeamRequestContextFactory {
    @Autowired
    RealizedCollectionAdapter realizedCollectionAdapter;
    @Autowired
    CollectionService collectionService;
    @Autowired
    TeamInformationService teamInformationService;
    @Value("${defaults.maxAllotedTimeForRequest}") Integer maxAllotedTimeForRequest;
    @Value("${defaults.battlesPerMatchup}") Integer battlesPerMatchup;
    @Autowired private TeamInfoService teamInfoService;
    private static final Integer RANK = 4000;

    private final Logger logger = LoggerFactory.getLogger(TeamRequestContextFactory.class);

    public TeamRequestContext getTeamRequestContext(TeamRequest teamRequest) {
        MDC.put("REQUESTOR", teamRequest.getPlayerTop());
        System.out.println("Incoming request: " + teamRequest.toJson());
        logger.info("Incoming Request: " + teamRequest.toJson());

        Long teamRequestTimeout = System.currentTimeMillis() + (maxAllotedTimeForRequest*1000);
        TeamRequestContext teamRequestContext = new TeamRequestContext(teamRequest, realizedCollectionAdapter, teamRequestTimeout, battlesPerMatchup, collectionService);
        init(teamRequestContext);

        return teamRequestContext;
    }

    private void init(TeamRequestContext teamRequestContext) {
        try {
            logger.info("Start init.");

            ExecutorService executor = Executors.newFixedThreadPool(6);

            TeamRequest teamRequest = teamRequestContext.getTeamRequest();
            List<CompletableFuture<? extends Object>> futures = new ArrayList<>();

            CompletableFuture<RealizedCollection> topCollectionFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getRealizedCollectionFor(teamRequest.getPlayerTop())), executor);
            CompletableFuture<RealizedCollection> bottomCollectionFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getRealizedCollectionFor(teamRequest.getPlayerBottom())), executor);
            CompletableFuture<TeamInfoResponse> teamInfoResponseFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getTeams(teamRequest)), executor);
            CompletableFuture<List<TeamRank>> backupTeamRanksFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(teamRequestContext)), executor);

            futures.add(topCollectionFuture);
            futures.add(bottomCollectionFuture);
            futures.add(teamInfoResponseFuture);
            futures.add(backupTeamRanksFuture);

            CompletableFuture combinedFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
            logger.info("Prepared futures.");
            combinedFuture.get();
            executor.shutdown();

            RealizedCollection playerTopCollection = topCollectionFuture.get();
            RealizedCollection playerBottomCollection = bottomCollectionFuture.get();

            teamRequestContext.setPlayerCollection(playerTopCollection);
            if (playerBottomCollection != null) {
                teamRequestContext.setOpponentCollection(playerBottomCollection);
            }
            else {
                teamRequestContext.setOpponentCollection(playerTopCollection);
            }

            TeamInfoResponse teamInfoResponse = teamInfoResponseFuture.get();
            List<TeamRank> backupTeamRanks = backupTeamRanksFuture.get();
            populateTeamRequestContext(teamRequestContext, teamInfoResponse);
            addOpponentHistoryWithAddedWeight(teamInfoResponse.getOpponentHistory());
            teamRequestContext.setPlayerBackupSuggestedTeams(backupTeamRanks);

            logger.info("Completed init.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RealizedCollection getRealizedCollectionFor(String player) {
        RealizedCollection realizedCollection = null;

        try {
            logger.info("Start realized collection for: " + player);
            Collection collection = collectionService.getCollection(player);
            realizedCollection = realizedCollectionAdapter.convertToRealizedCollection(collection);
            logger.info("Finished realized collection for: " + player);
        } catch (Exception e) {
            logger.error("Failed getting collection for: " + player);
            e.printStackTrace();
        }
        return realizedCollection;
    }


    //TODO: Add in opponent history with added weight to be ran against in simulator
    private void addOpponentHistoryWithAddedWeight(List<TeamRank> opponentHistory) {
//        List<TeamRank> teamRanks = new LinkedList<>();
//
//        for (String teamId : opponentHistory) {
//            new TeamRank()
//        }
    }

    private void populateTeamRequestContext(TeamRequestContext teamRequestContext, TeamInfoResponse teamInfoResponse) {
        setTeamSuggestions(teamRequestContext, teamInfoResponse);

        Map<String, Integer> colorStats = teamInfoResponse.getColorStats();
        teamRequestContext.setColorStats(colorStats);

        List<TeamRank> opponentHistory = teamInfoResponse.getOpponentHistory();
        teamRequestContext.setOpponentHistory(opponentHistory);
    }

    private void setTeamSuggestions(TeamRequestContext teamRequestContext, TeamInfoResponse teamInfoResponse) {
        Set<TeamRank> teamRanks = new HashSet<>(teamInfoResponse.getTeams());
        logger.info("Team suggestions returned: " + teamRanks.size());
        teamRequestContext.setPlayerSuggestedTeams(new ArrayList<>(teamRanks));
        teamRequestContext.setOpponentSuggestedTeams(new ArrayList<>(teamRanks));
    }

    private TeamInfoResponse getTeams(TeamRequest teamRequest) {
        logger.info("Start get teams.");
        int mana = teamRequest.getMana();
        String ruleset = teamRequest.getGameRules();

        TeamInfoResponse teamInfoResponse = teamInfoService.getTeamsInfo(mana, ruleset, RANK, teamRequest.getLevelLimit(), teamRequest.getPlayerTop(), teamRequest.getPlayerBottom());

        logger.info("Team info service stats[" + ruleset + "]: " + teamInfoResponse.getServiceStats());
        logger.info("Finished get teams.");
        return teamInfoResponse;
    }

    private List<TeamRank> getBackupTeams(TeamRequestContext teamRequestContext) {
        List<TeamRank> backups = new ArrayList<>();
        TeamRequest teamRequest = teamRequestContext.getTeamRequest();
        int mana = teamRequest.getMana();
        LevelLimit levelLimit = teamRequest.getLevelLimit();
        String playerTop = teamRequest.getPlayerTop();
        String playerBottom = teamRequest.getPlayerBottom();

        String[] rules;
        String ruleset = teamRequest.getGameRules();

        if (ruleset.contains("|")) {
            rules = ruleset.split("\\|");
        }
        else if (!GameRuleType.forGameRuleType(ruleset).isRestrictive()) {
            rules = new String[1];
            rules[0] = "Standard";
        }
        else {
            rules = new String[1];
            rules[0] = ruleset;
        }
//        else {
//            logger.info("Backup team not possible with restrictive rulest: " + ruleset);
//            return new ArrayList<>();
//        }

        //CompletableFuture<TeamInfoResponse> teamInfoResponseFuture = CompletableFuture.supplyAsync(() -> getTeams(teamRequest));
        //        CompletableFuture<List<TeamRank>> backupTeamRanksFuture = CompletableFuture.supplyAsync(() -> getBackupTeams(teamRequestContext));
        //
        //        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(teamInfoResponseFuture, backupTeamRanksFuture);
        //        try {
        //            combinedFuture.get();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Set<CompletableFuture<List<TeamRank>>> ruleRequests = new HashSet<>();

        if ((rules.length > 1) && (GameRuleType.forGameRuleType(rules[0]).isRestrictive() && !GameRuleType.forGameRuleType(rules[1]).isRestrictive())) {
            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType(rules[0]).getId(), levelLimit, playerTop, playerBottom)), executor);
            ruleRequests.add(backupTeamsCompletableFuture);
        }
        if ((rules.length > 1) && (GameRuleType.forGameRuleType(rules[1]).isRestrictive() && !GameRuleType.forGameRuleType(rules[0]).isRestrictive())) {
            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType(rules[1]).getId(), levelLimit, playerTop, playerBottom)), executor);
            ruleRequests.add(backupTeamsCompletableFuture);
        }
        if ((rules.length > 1) && (!GameRuleType.forGameRuleType(rules[1]).isRestrictive() && !GameRuleType.forGameRuleType(rules[0]).isRestrictive())) {
            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture1 = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType(rules[0]).getId(), levelLimit, playerTop, playerBottom)), executor);
            ruleRequests.add(backupTeamsCompletableFuture1);
            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture2 = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType(rules[1]).getId(), levelLimit, playerTop, playerBottom)), executor);
            ruleRequests.add(backupTeamsCompletableFuture2);
//            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture3 = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType("Standard").getId(), levelLimit, playerBottom)));
//            ruleRequests.add(backupTeamsCompletableFuture3);
        }
        if ((rules.length == 1) && (!rules[0].equals(ruleset))) {
            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType(rules[0]).getId(), levelLimit, playerTop, playerBottom)), executor);
            ruleRequests.add(backupTeamsCompletableFuture);
        }
        else if ((rules.length == 1) && GameRuleType.forGameRuleType(rules[0]).isRestrictive()) {
            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType(rules[0]).getId() + "|Aim True", levelLimit, playerTop, playerBottom)), executor);
            ruleRequests.add(backupTeamsCompletableFuture);
        }

        CompletableFuture<List<TeamRank>>[] arr = ruleRequests.stream().toArray(CompletableFuture[]::new);
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(arr);
        try {
            combinedFuture.get();
            executor.shutdown();

            for (CompletableFuture<List<TeamRank>> future : ruleRequests) {
                List<TeamRank> backupTeams = future.get();
                for (TeamRank teamRank : backupTeams) {
                    teamRank.setSource(TeamInfoService.TEAM_INFO_SERVICE_SOURCE + "|BACKUP");
                }
                backups.addAll(backupTeams);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return backups;
    }

    private List<TeamRank> getBackupTeams(int mana, String ruleset, LevelLimit levelLimit, String playerTop, String playerBottom) {
        logger.info("Getting backup team with ruleset: " + ruleset);
        TeamInfoResponse teamInfoResponse = teamInfoService.getTeamsInfo(mana, ruleset, RANK, levelLimit, playerTop, playerBottom);
        logger.info("Team info service stats[" + ruleset + "]: " + teamInfoResponse.getServiceStats());
        return teamInfoResponse.getTeams();
    }
}
