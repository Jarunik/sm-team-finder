package org.slos.splinterlands.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slos.util.ToJson;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Details implements ToJson {
    private Team team1;
    private Team team2;
    private String winner;
    private String loser;

    public Details(@JsonProperty("team1") Team team1,
                   @JsonProperty("team2") Team team2,
                   @JsonProperty("winner") String winner,
                   @JsonProperty("loser") String loser) {
        this.team1 = team1;
        this.team2 = team2;
        this.winner = winner;
        this.loser = loser;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public String getWinner() {
        return winner;
    }

    public String getLoser() {
        return loser;
    }

    @Override
    public String toString() {
        return "Details{" +
                "team1=" + team1 +
                ", team2=" + team2 +
                ", winner='" + winner + '\'' +
                ", loser='" + loser + '\'' +
                '}';
    }
}
