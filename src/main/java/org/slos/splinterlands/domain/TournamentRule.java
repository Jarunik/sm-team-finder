package org.slos.splinterlands.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum TournamentRule {
    @JsonProperty("no_legendaries")
    NO_LEGENDARIES("no_legendaries"),
    @JsonProperty("no_legendary_summoners")
    NO_LEGENDARY_SUMMONERS("no_legendary_summoners"),
    @JsonProperty("all")
    ALL_PLAYABLE("all");

    private String id;

    TournamentRule(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    @JsonCreator
    public static TournamentRule forTournamentRule(String rule) {
        for(TournamentRule tr: values()) {
            if(tr.id().equals(rule)) {
                return tr;
            }
        }
        return ALL_PLAYABLE;
    }
}
