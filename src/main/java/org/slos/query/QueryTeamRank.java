package org.slos.query;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryTeamRank {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Color")
    private String color;
    @JsonProperty("Rank")
    private Float rank;
    @JsonProperty("TotalPlayed")
    private Integer totalPlayed;
    @JsonProperty("Wins")
    private Integer wins;
    @JsonProperty("Losses")
    private Integer losses;

    public QueryTeamRank(String id, String color, Float rank, Integer totalPlayed, Integer wins, Integer losses) {
        this.id = id;
        this.color = color;
        this.rank = rank;
        this.totalPlayed = totalPlayed;
        this.wins = wins;
        this.losses = losses;

        if (this.losses == null) {
            this.losses = 0;
        }
    }

    public String getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public Float getRank() {
        return rank;
    }

    public Integer getTotalPlayed() {
        return totalPlayed;
    }

    public Integer getWins() {
        return wins;
    }

    public Integer getLosses() {
        return losses;
    }

    @Override
    public String toString() {
        return "TeamRank{" +
                "id='" + id + '\'' +
                ", color='" + color + '\'' +
                ", rank=" + rank +
                ", totalPlayed=" + totalPlayed +
                ", wins=" + wins +
                ", losses=" + losses +
                '}';
    }
}
