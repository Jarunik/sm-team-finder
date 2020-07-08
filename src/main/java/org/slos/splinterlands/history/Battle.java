package org.slos.splinterlands.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.slos.util.ToJson;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Battle implements ToJson {
    private int player_1_rating_initial;
    private int player_2_rating_initial;
    private String player_1;
    private String player_2;
    private String winner;
    private int player_1_rating_final;
    private int player_2_rating_final;
    @JsonDeserialize(using = DetailsDeserializer.class)
    private Details details;
    private int manaCap;
    private String ruleset;

    public Battle(@JsonProperty("player_1_rating_initial") int player_1_rating_initial,
                  @JsonProperty("player_2_rating_initial") int player_2_rating_initial,
                  @JsonProperty("winner") String winner,
                  @JsonProperty("player_1_rating_final") int player_1_rating_final,
                  @JsonProperty("player_2_rating_final") int player_2_rating_final,
                  @JsonProperty("details") Details details,
                  @JsonProperty("mana_cap") int manaCap,
                  @JsonProperty("ruleset") String ruleset,
                  @JsonProperty("player_1") String player_1,
                  @JsonProperty("player_2") String player_2) {
        this.player_1_rating_initial = player_1_rating_initial;
        this.player_2_rating_initial = player_2_rating_initial;
        this.player_1 = player_1;
        this.player_2 = player_2;
        this.winner = winner;
        this.player_1_rating_final = player_1_rating_final;
        this.player_2_rating_final = player_2_rating_final;
        this.details = details;
        this.manaCap = manaCap;
        this.ruleset = ruleset;
    }

    public int getPlayer_1_rating_initial() {
        return player_1_rating_initial;
    }

    public int getPlayer_2_rating_initial() {
        return player_2_rating_initial;
    }

    public String getWinner() {
        return winner;
    }

    public int getPlayer_1_rating_final() {
        return player_1_rating_final;
    }

    public String getPlayer_1() {
        return player_1;
    }

    public String getPlayer_2() {
        return player_2;
    }

    public int getPlayer_2_rating_final() {
        return player_2_rating_final;
    }

    public Details getDetails() {
        return details;
    }

    public int getManaCap() {
        return manaCap;
    }

    public String getRuleset() {
        return ruleset;
    }

    @Override
    public String toString() {
        return "Battle{" +
                "player_1_rating_initial=" + player_1_rating_initial +
                ", player_2_rating_initial=" + player_2_rating_initial +
                ", winner='" + winner + '\'' +
                ", player_1_rating_final=" + player_1_rating_final +
                ", player_2_rating_final=" + player_2_rating_final +
                ", details=" + details +
                '}';
    }
}
