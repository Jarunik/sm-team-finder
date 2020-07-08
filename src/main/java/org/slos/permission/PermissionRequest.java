package org.slos.permission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slos.util.ToJson;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionRequest implements ToJson {
    private String player;
    private Integer rank;
    private Integer rating;

    public PermissionRequest(){}
    public PermissionRequest(@JsonProperty("player") String player, @JsonProperty("rank") Integer rank, @JsonProperty("rating") Integer rating) {
        this.player = player;
        this.rank = rank;
        this.rating = rating;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return toJson();
    }
}
