package org.slos.splinterlands.domain.monster;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MonsterType {
    @JsonProperty("Summoner")
    SUMMONER,
    @JsonProperty("Monster")
    MONSTER
}
