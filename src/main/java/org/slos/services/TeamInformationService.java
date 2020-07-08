package org.slos.services;

import org.slos.TeamRequestProcessStep;
import org.slos.domain.TeamRank;
import org.slos.domain.TeamRequest;
import org.slos.domain.TeamRequestContext;
import org.slos.splinterlands.TeamInfoResponse;
import org.slos.splinterlands.TeamInfoService;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.LevelLimit;
import org.slos.util.WithMDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TeamInformationService implements TeamRequestProcessStep {
    @Autowired private TeamInfoService teamInfoService;
    private static final Integer RANK = 4000;

    private final Logger logger = LoggerFactory.getLogger(TeamInformationService.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        TeamRequest teamRequest = teamRequestContext.getTeamRequest();

        CompletableFuture<TeamInfoResponse> teamInfoResponseFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getTeams(teamRequest)));
        CompletableFuture<List<TeamRank>> backupTeamRanksFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(teamRequestContext)));

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(teamInfoResponseFuture, backupTeamRanksFuture);
        try {
            combinedFuture.get();

            TeamInfoResponse teamInfoResponse = teamInfoResponseFuture.get();
            List<TeamRank> backupTeamRanks = backupTeamRanksFuture.get();

            populateTeamRequestContext(teamRequestContext, teamInfoResponse);
            addOpponentHistoryWithAddedWeight(teamInfoResponse.getOpponentHistory());
            teamRequestContext.setPlayerBackupSuggestedTeams(backupTeamRanks);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return teamRequestContext;
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
        int mana = teamRequest.getMana();
        String ruleset = teamRequest.getGameRules();

        TeamInfoResponse teamInfoResponse = teamInfoService.getTeamsInfo(mana, ruleset, RANK, teamRequest.getLevelLimit(), teamRequest.getPlayerTop(), teamRequest.getPlayerBottom());
        logger.info("Team info service stats[" + ruleset + "]: " + teamInfoResponse.getServiceStats());
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
            logger.info("Backup team not possible with restrictive rulest: " + ruleset);
            return new ArrayList<>();
        }

        //CompletableFuture<TeamInfoResponse> teamInfoResponseFuture = CompletableFuture.supplyAsync(() -> getTeams(teamRequest));
        //        CompletableFuture<List<TeamRank>> backupTeamRanksFuture = CompletableFuture.supplyAsync(() -> getBackupTeams(teamRequestContext));
        //
        //        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(teamInfoResponseFuture, backupTeamRanksFuture);
        //        try {
        //            combinedFuture.get();

        Set<CompletableFuture<List<TeamRank>>> ruleRequests = new HashSet<>();

        if ((rules.length > 1) && (GameRuleType.forGameRuleType(rules[0]).isRestrictive() && !GameRuleType.forGameRuleType(rules[1]).isRestrictive())) {
            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType(rules[0]).getId(), levelLimit, playerTop, playerBottom)));
            ruleRequests.add(backupTeamsCompletableFuture);
        }
        if ((rules.length > 1) && (GameRuleType.forGameRuleType(rules[1]).isRestrictive() && !GameRuleType.forGameRuleType(rules[0]).isRestrictive())) {
            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType(rules[1]).getId(), levelLimit, playerTop, playerBottom)));
            ruleRequests.add(backupTeamsCompletableFuture);
        }
        if ((rules.length > 1) && (!GameRuleType.forGameRuleType(rules[1]).isRestrictive() && !GameRuleType.forGameRuleType(rules[0]).isRestrictive())) {
            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture1 = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType(rules[0]).getId(), levelLimit, playerTop, playerBottom)));
            ruleRequests.add(backupTeamsCompletableFuture1);
            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture2 = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType(rules[1]).getId(), levelLimit, playerTop, playerBottom)));
            ruleRequests.add(backupTeamsCompletableFuture2);
//            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture3 = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType("Standard").getId(), levelLimit, playerBottom)));
//            ruleRequests.add(backupTeamsCompletableFuture3);
        }
        if ((rules.length == 1) && (!rules[0].equals(ruleset))) {
            CompletableFuture<List<TeamRank>> backupTeamsCompletableFuture = CompletableFuture.supplyAsync(WithMDC.run(() -> getBackupTeams(mana, GameRuleType.forGameRuleType(rules[0]).getId(), levelLimit, playerTop, playerBottom)));
            ruleRequests.add(backupTeamsCompletableFuture);
        }

        CompletableFuture<List<TeamRank>>[] arr = ruleRequests.stream().toArray(CompletableFuture[]::new);
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(arr);
        try {
            combinedFuture.get();

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
