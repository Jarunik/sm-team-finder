package org.slos.splinterlands.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slos.util.ToJson;

import java.util.HashSet;
import java.util.Set;

public class AllowedCards implements ToJson {
    @JsonProperty("type")
    private TournamentRule type;
    private Foil foil;
    private Set<Integer> editions;

    public AllowedCards(@JsonProperty("type") TournamentRule type, @JsonProperty("foil") Foil foil, @JsonProperty("editions") Set<Integer> editions) {
        this.type = type;
        this.foil = foil;
        this.editions = editions;
    }

    public static AllowedCards all() {
        Set<Integer> editions = new HashSet<>();
        editions.add(0);
        editions.add(1);
        editions.add(2);
        editions.add(3);
        editions.add(4);
        editions.add(5);
        editions.add(6);

        return new AllowedCards(TournamentRule.ALL_PLAYABLE, Foil.ALL, editions);
    }

    public TournamentRule getType() {
        return type;
    }

    public void setType(TournamentRule type) {
        this.type = type;
    }

    public Foil getFoil() {
        return foil;
    }

    public void setFoil(Foil foil) {
        this.foil = foil;
    }

    public Set<Integer> getEditions() {
        return editions;
    }

    public void setEditions(Set<Integer> editions) {
        this.editions = editions;
    }

    @Override
    public String toString() {
        return toJson();
    }
}
