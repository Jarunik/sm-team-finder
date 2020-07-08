package org.slos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slos.Team;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.util.ToJson;

public class TeamRank implements ToJson {
    //{"Color":"Green","Id":"112-60-28-63-102-31-24","Rank":597.38380966988041}
    @JsonProperty("Color")
    private ColorType color;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Rank")
    private Float rank;
    @JsonProperty("TotalPlayed")
    private Integer totalPlayed;
    @JsonProperty("Wins")
    private Integer wins;
    @JsonProperty("Losses")
    private Integer losses;
    @JsonProperty("Source")
    private String source;
    @JsonIgnore
    private Team team;

    public static final String SOURCE_UNKNOWN = "Unknown";

    @JsonIgnoreProperties(ignoreUnknown = true)
    public TeamRank(@JsonProperty("Color") ColorType color, @JsonProperty("Id") String id, @JsonProperty("Rank") Float rank, @JsonProperty("TotalPlayed") Integer totalPlayed, @JsonProperty("Wins") Integer wins, @JsonProperty("Losses") Integer losses, Team team) {
        this.color = color;
        this.id = id;
        this.rank = rank;
        this.totalPlayed = totalPlayed;
        this.wins = wins;
        this.losses = losses;
        this.team = team;
        if (team != null) {
            this.source = team.getSource();
        }
        else {
            this.source = SOURCE_UNKNOWN;
        }
    }

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public TeamRank(@JsonProperty("Color") ColorType color, @JsonProperty("Id") String id, @JsonProperty("Rank") Float rank, @JsonProperty("TotalPlayed") Integer totalPlayed, @JsonProperty("Wins") Integer wins, @JsonProperty("Losses") Integer losses, @JsonProperty("Source") String source, Team team) {
        this.color = color;
        this.id = id;
        this.rank = rank;
        this.totalPlayed = totalPlayed;
        this.wins = wins;
        this.losses = losses;
        this.source = source;
        this.team = team;
    }

    public boolean hasCard(int cardId) {
        if ((team != null) && (team.getCards().size() > 0)) {
            return team.hasCard(cardId);
        }

        String[] cards = id.split("-");
        for (String teamRankCardId : cards) {
            if (teamRankCardId.equals(""+cardId)) {
                return true;
            }
        }

        return false;
    }

    public Team getTeam() {
        return team;
    }

    public ColorType getColor() {
        return color;
    }

    public void setColor(ColorType color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getRank() {
        return rank;
    }

    public void setRank(Float rank) {
        this.rank = rank;
    }

    public Integer getTotalPlayed() {
        return totalPlayed;
    }

    public Integer getWins() {
        return wins;
    }

    public Float getWinRatio() {
        return (float)wins / (float)totalPlayed;
    }

    public Integer getLosses() {
        return losses;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

//    @Override
//    public String toString() {
//        return toJson();
//    }


    @Override
    public String toString() {
        return "TeamRank{" +
                "color=" + color +
                ", id='" + id + '\'' +
                ", rank=" + rank +
                ", totalPlayed=" + totalPlayed +
                ", wins=" + wins +
                ", losses=" + losses +
                ", source='" + source + '\'' +
                ", team=" + team +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((TeamRank)obj).id);
    }
}
