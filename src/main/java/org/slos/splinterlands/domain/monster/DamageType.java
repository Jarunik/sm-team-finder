package org.slos.splinterlands.domain.monster;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DamageType {
    @JsonProperty("attack")
    ATTACK,
    @JsonProperty("ranged")
    RANGED,
    @JsonProperty("magic")
    MAGIC,
    @JsonProperty("none")
    NONE,
    ANY,
    SYSTEM
}
