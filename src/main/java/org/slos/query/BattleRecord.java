package org.slos.query;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BattleRecord {
    private Float rating;
    private String id;
    @JsonIgnore
    private int block;
    private String color;
    @JsonIgnore
    private int playerRating;
    @JsonIgnore
    private Float teamDifferenceWeight;
    @JsonIgnore
    private String playerId;

    public BattleRecord(String id, int block, String color, int playerRating, Float teamDifferenceWeight, String playerId) {
        this.id = id;
        this.block = block;
        this.color = color;
        this.playerRating = playerRating;
        this.teamDifferenceWeight = teamDifferenceWeight;
        this.playerId = playerId;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getColor() {
        return color;
    }

    public Float getRating() {
        return rating;
    }

    public String getId() {
        return id;
    }

    public int getBlock() {
        return block;
    }

    public int getPlayerRating() {
        return playerRating;
    }

    public Float getTeamDifferenceWeight() {
        return teamDifferenceWeight;
    }

    public String getPlayerId() {
        return playerId;
    }

    @Override
    public String toString() {
        return "BattleRecord{" +
                "rating=" + rating +
                ", id='" + id + '\'' +
                ", block=" + block +
                ", color='" + color + '\'' +
                ", playerRating=" + playerRating +
                ", teamDifferenceWeight=" + teamDifferenceWeight +
                ", playerId='" + playerId + '\'' +
                '}';
    }
}
