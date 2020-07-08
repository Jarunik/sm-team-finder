package org.slos.domain;

import org.slos.Team;
import org.slos.services.RealizedCollectionAdapter;
import org.slos.splinterlands.collection.CollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TeamRequestContext {
    private TeamRequest teamRequest;
    private RealizedCollection playerCollection;
    private RealizedCollection opponentCollection;
    private RealizedCollection backupOpponentCollection;
    private RealizedCollectionAdapter realizedCollectionAdapter;
    private RankingReport rankingReport;
    private Long teamRequestTimeout;
    private Integer battlesPerMatchup;
    private Boolean shouldYield = false;
    private Map<String, Integer> colorStats;
    private CollectionService collectionService;
    private List<TeamRank> opponentHistory;

    private List<TeamRank> playerSuggestedTeams;
    private List<TeamRank> opponentSuggestedTeams;

    private List<TeamRank> playerBackupSuggestedTeams;

    private List<Team> playerTeams;
    private List<Team> opponentTeams;
    private List<Team> additionalPlayerTeams;

    private Set<String> initialSuggestedTeams;
    private Set<String> generatedTeams = new HashSet<>();

    private List<TeamRank> resultTeamRanks;
    private static final Object lock = new Object();

    private final Logger logger = LoggerFactory.getLogger(TeamRequestContext.class);

    public TeamRequestContext(TeamRequest teamRequest, RealizedCollectionAdapter realizedCollectionAdapter, Long teamRequestTimeout, Integer battlesPerMatchup, CollectionService collectionService) {
        this.teamRequest = teamRequest;
        this.realizedCollectionAdapter = realizedCollectionAdapter;
        this.teamRequestTimeout = teamRequestTimeout;
        this.battlesPerMatchup = battlesPerMatchup;
        this.collectionService = collectionService;
    }

    public void addAdditionalSuggestedTeams(List<Team> generatedTeams) {
        if (additionalPlayerTeams == null) {
            additionalPlayerTeams = new ArrayList<>();
        }

        additionalPlayerTeams.addAll(generatedTeams);
    }

    public List<Team> getAdditionalPlayerTeams() {
        return additionalPlayerTeams;
    }

    public List<TeamRank> getPlayerBackupSuggestedTeams() {
        return playerBackupSuggestedTeams;
    }

    public void setPlayerBackupSuggestedTeams(List<TeamRank> playerBackupSuggestedTeams) {
        this.playerBackupSuggestedTeams = playerBackupSuggestedTeams;
    }

    public RealizedCollection getBackupOpponentCollection() {
        return backupOpponentCollection;
    }

    public void setBackupOpponentCollection(RealizedCollection backupOpponentCollection) {
        this.backupOpponentCollection = backupOpponentCollection;
    }

    public void setPlayerCollection(RealizedCollection playerCollection) {
        this.playerCollection = playerCollection;
    }

    public void setOpponentCollection(RealizedCollection opponentCollection) {
        this.opponentCollection = opponentCollection;
    }

    public TeamRequest getTeamRequest() {
        return teamRequest;
    }

    public RealizedCollection getPlayerCollection() {
        return playerCollection;
    }

    public RealizedCollection getOpponentCollection() {
        return opponentCollection;
    }

    public List<TeamRank> getPlayerSuggestedTeams() {
        return playerSuggestedTeams;
    }

    public void setPlayerSuggestedTeams(List<TeamRank> playerSuggestedTeams) {
        this.playerSuggestedTeams = playerSuggestedTeams;
        initialSuggestedTeams = playerSuggestedTeams.stream().map(teamRank -> teamRank.getId()).collect(Collectors.toSet());
    }

    public List<TeamRank> getOpponentSuggestedTeams() {
        return opponentSuggestedTeams;
    }

    public Set<String> getGeneratedTeams() {
        return generatedTeams;
    }

    public Set<String> getInitialSuggestedTeams() {
        return initialSuggestedTeams;
    }

    public void setInitialSuggestedTeams(Set<String> initialSuggestedTeams) {
        this.initialSuggestedTeams = initialSuggestedTeams;
    }

    public void setOpponentSuggestedTeams(List<TeamRank> opponentSuggestedTeams) {
        this.opponentSuggestedTeams = opponentSuggestedTeams;
    }

    public List<Team> getPlayerTeams() {
        return playerTeams;
    }

    public void setPlayerTeams(List<Team> playerTeams) {
        this.playerTeams = playerTeams;
    }

    public List<Team> getOpponentTeams() {
        return opponentTeams;
    }

    public void setOpponentTeams(List<Team> opponentTeams) {
        this.opponentTeams = opponentTeams;
    }

    public RankingReport getRankingReport() {
        return rankingReport;
    }

    public void setRankingReport(RankingReport rankingReport) {
        this.rankingReport = rankingReport;
    }

    public Long getTeamRequestTimeout() {
        return teamRequestTimeout;
    }

    public Integer getBattlesPerMatchup() {
        return battlesPerMatchup;
    }

    public List<TeamRank> getResultTeamRanks() {
        return resultTeamRanks;
    }

    public void setResultTeamRanks(List<TeamRank> resultTeamRanks) {
        this.resultTeamRanks = resultTeamRanks;
    }

    public Boolean getShouldYield() {
        return shouldYield;
    }

    public void setShouldYield(Boolean shouldYield) {
        this.shouldYield = shouldYield;
    }

    public Map<String, Integer> getColorStats() {
        return colorStats;
    }

    public void setColorStats(Map<String, Integer> colorStats) {
        this.colorStats = colorStats;
    }

    public List<TeamRank> getOpponentHistory() {
        return opponentHistory;
    }

    public void setOpponentHistory(List<TeamRank> opponentHistory) {
        this.opponentHistory = opponentHistory;
    }
}
