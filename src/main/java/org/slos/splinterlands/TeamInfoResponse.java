package org.slos.splinterlands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slos.domain.TeamRank;
import org.slos.util.ToJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamInfoResponse implements ToJson {
    private List<TeamRank> teams;
    private Map<String, Integer> colorStats;
    private List<TeamRank> opponentHistory;
    private ServiceStats serviceStats;

    @JsonCreator
    public TeamInfoResponse(@JsonProperty("teams") List<TeamRank> teams, @JsonProperty("colorStats") Map<String, Integer> colorStats, @JsonProperty("opponentHistory") List<TeamRank> opponentHistory, @JsonProperty("serviceStats") ServiceStats serviceStats) {
        this.teams = teams;
        this.colorStats = colorStats;
        this.opponentHistory = opponentHistory;
        this.serviceStats = serviceStats;

        if (this.colorStats == null) {
            this.colorStats = new HashMap<>();
        }
        if (this.opponentHistory == null) {
            this.opponentHistory = new ArrayList<>();
        }
    }

    public List<TeamRank> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamRank> teams) {
        this.teams = teams;
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

    public ServiceStats getServiceStats() {
        return serviceStats;
    }

    public void setServiceStats(ServiceStats serviceStats) {
        this.serviceStats = serviceStats;
    }

    @Override
    public String toString() {
        return toJson();
    }
}

class ServiceStats implements ToJson {
    private Integer queryRunTime;
    private Integer rankingRunTime;

    public ServiceStats(Integer queryRunTime, Integer rankingRunTime) {
        this.queryRunTime = queryRunTime;
        this.rankingRunTime = rankingRunTime;
    }

    public Integer getQueryRunTime() {
        return queryRunTime;
    }

    public void setQueryRunTime(Integer queryRunTime) {
        this.queryRunTime = queryRunTime;
    }

    public Integer getRankingRunTime() {
        return rankingRunTime;
    }

    public void setRankingRunTime(Integer rankingRunTime) {
        this.rankingRunTime = rankingRunTime;
    }

    @Override
    public String toString() {
        return toJson();
    }
}