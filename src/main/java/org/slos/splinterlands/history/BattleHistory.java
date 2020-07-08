package org.slos.splinterlands.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slos.util.ToJson;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BattleHistory implements ToJson {
    private String player;
    private List<Battle> battles;
    private Long createdAt;

    public BattleHistory(@JsonProperty("player") String player, @JsonProperty("battles") List<Battle> battles) {
        this.player = player;
        this.battles = battles;
    }

    public String getPlayer() {
        return player;
    }

    public List<Battle> getBattles() {
        return battles;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "BattleHistory{" +
                "player='" + player + '\'' +
                ", battles=" + battles +
                '}';
    }
}
