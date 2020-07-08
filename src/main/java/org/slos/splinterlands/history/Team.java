package org.slos.splinterlands.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slos.splinterlands.collection.CollectionCard;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.util.ToJson;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Team implements ToJson {
    private String player;
    private int rating;
    private ColorType color;
    private CollectionCard summoner;
    private List<CollectionCard> monsters;

    public Team(@JsonProperty("player") String player,
                @JsonProperty("rating") int rating,
                @JsonProperty("color") ColorType color,
                @JsonProperty("summoner") CollectionCard summoner,
                @JsonProperty("monsters") List<CollectionCard> monsters) {
        this.player = player;
        this.rating = rating;
        this.color = color;
        this.summoner = summoner;
        this.monsters = monsters;
    }

    public String getPlayer() {
        return player;
    }

    public int getRating() {
        return rating;
    }

    public ColorType getColor() {
        return color;
    }

    public CollectionCard getSummoner() {
        return summoner;
    }

    public List<CollectionCard> getMonsters() {
        return monsters;
    }

    @Override
    public String toString() {
        return "Team{" +
                "player='" + player + '\'' +
                ", rating=" + rating +
                ", color=" + color +
                ", summoner=" + summoner +
                ", monsters=" + monsters +
                '}';
    }
}
