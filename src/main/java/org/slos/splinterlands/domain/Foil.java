package org.slos.splinterlands.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Foil {
    @JsonProperty("gold_only")
    GOLD_ONLY("gold_only"),
    @JsonProperty("all")
    ALL("all");

    private String id;

    Foil(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    @JsonCreator
    public static Foil forFoil(String id) {
        for(Foil foil: values()) {
            if(foil.id().equals(id)) {
                return foil;
            }
        }

        return ALL;
    }
}
