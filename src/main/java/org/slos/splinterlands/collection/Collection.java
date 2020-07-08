package org.slos.splinterlands.collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slos.util.ToJson;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Collection implements ToJson {
    private String player;
    private CollectionCard[] cards;

    @JsonCreator
    public Collection(@JsonProperty("player") String player, @JsonProperty("cards") CollectionCard[] cards) {
        this.player = player;
        this.cards = cards;
    }

    public String getPlayer() {
        return player;
    }

    public CollectionCard[] getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return "Collection{" +
                "player='" + player + '\'' +
                ", cards=" + Arrays.toString(cards) +
                '}';
    }
}
