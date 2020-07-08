package org.slos.domain;

import org.slos.ranking.TeamStats;
import org.slos.util.ToJson;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RankingReport implements ToJson {
    private volatile Map<String, TeamStats> ranks = new ConcurrentHashMap<>( );
    private volatile Long totalGameCount = 0l;
    private TeamRequestContext teamRequestContext;

    public TeamRequestContext getTeamRequestContext() {
        return teamRequestContext;
    }

    public void setTeamRequestContext(TeamRequestContext teamRequestContext) {
        this.teamRequestContext = teamRequestContext;
    }

    public Map<String, TeamStats> getRanks() {
        return ranks;
    }

    public synchronized void incrementTotalGameCount() {
        this.totalGameCount++;
    }

    public synchronized Long getTotalGameCount() {
        return totalGameCount;
    }

    @Override
    public String toString() {
        return "RankingReport{" +
                "ranks=" + ranks +
                ", totalGameCount=" + totalGameCount +
                ", teamRequestContext=" + teamRequestContext +
                '}';
    }
}
