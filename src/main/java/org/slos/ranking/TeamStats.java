package org.slos.ranking;

import org.slos.Team;
import org.slos.util.ToJson;

public class TeamStats implements ToJson {
    private Team team;
    private volatile Integer totalWins = 0;
    private volatile Integer totalDraws = 0;
    private volatile Integer totalLosses = 0;
    private volatile Integer errorCount = 0;
    private volatile Boolean hadMirrorMatch = false;

    public TeamStats(Team team) {
        this.team = team;
    }

    public Boolean getHadMirrorMatch() {
        return hadMirrorMatch;
    }

    public void setHadMirrorMatch(Boolean hadMirrorMatch) {
        this.hadMirrorMatch = hadMirrorMatch;
    }

    public synchronized void addToWin() {
        totalWins++;
    }

    public synchronized void addToWin(Integer count) {
        totalWins += count;
    }

    public synchronized void addToLoss() {
        totalLosses++;
    }

    public synchronized void addToDraw() {
        totalDraws++;
    }

    public synchronized void addToErrorCount() {
        errorCount++;
    }

    public synchronized Team getTeam() {
        return team;
    }

    public synchronized Integer getTotalWins() {
        return totalWins;
    }

    public synchronized Integer getTotalDraws() {
        return totalDraws;
    }

    public synchronized Integer getErrorCount() {
        return errorCount;
    }

    public synchronized Integer getTotalLosses() {
        return totalLosses;
    }

    public synchronized void setTotalLosses(Integer totalLosses) {
        this.totalLosses = totalLosses;
    }

    public synchronized Float getWinRatio() {
        return (float)totalWins / (float)(totalWins + totalLosses + totalDraws);
    }

    @Override
    public String toString() {
        return "TeamStats{" +
                "team=" + team +
                ", totalWins=" + totalWins +
                ", totalDraws=" + totalDraws +
                ", totalLosses=" + totalLosses +
                ", errorCount=" + errorCount +
                '}';
    }
}
