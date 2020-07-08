package org.slos.battle.abilities;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AbilityType {
    @JsonProperty("Halving")
    HALVING,
    @JsonProperty("Last Stand")
    LAST_STAND,
    @JsonProperty("Knock Out")
    KNOCK_OUT,
    @JsonProperty("Snare")
    SNARE,
    SNARED,
    @JsonProperty("Redemption")
    REDEMPTION,
    @JsonProperty("Taunt")
    TAUNT,
    TAUNTED,
    @JsonProperty("Rust")
    RUST,
    @JsonProperty("Scavenger")
    SCAVENGER,
    @JsonProperty("AfflictionAbility")
    AFFLICTION,
    AFFLICTED,
    @JsonProperty("Blast")
    BLAST,
    @JsonProperty("Blind")
    BLIND,
    @JsonProperty("Clense")
    CLEANSE,
    @JsonProperty("Divine Shield")
    DIVINE_SHIELD,
    @JsonProperty("Dodge")
    DODGE,
    @JsonProperty("Double Strike")
    DOUBLE_STRIKE,
    @JsonProperty("EnrageRule")
    ENRAGE,
    @JsonProperty("Flying")
    FLYING,
    @JsonProperty("Headwinds")
    HEADWINDS,
    @JsonProperty("Heal")
    HEAL,
    @JsonProperty("Tank Heal")
    TANK_HEAL,
    @JsonProperty("Inspire")
    INSPIRE,
    @JsonProperty("Life Leech")
    LIFE_LEECH,
    @JsonProperty("Magic Reflect")
    MAGIC_REFLECT,
    @JsonProperty("Opportunity")
    OPPORTUNITY,
    @JsonProperty("Piercing")
    PIERCING,
    @JsonProperty("Poison")
    POISON,
    POISONED,
    @JsonProperty("Protect")
    PROTECT,
    @JsonProperty("Reach")
    REACH,
    @JsonProperty("Repair")
    REPAIR,
    @JsonProperty("Resurrect")
    RESURRECT,
    @JsonProperty("Retaliate")
    RETALIATE,
    @JsonProperty("Return Fire")
    RETURN_FIRE,
    @JsonProperty("Shatter")
    SHATTER,
    @JsonProperty("Shield")
    SHIELD,
    @JsonProperty("Silence")
    SILENCE,
    @JsonProperty("Slow")
    SLOW,
    @JsonProperty("Sneak")
    SNEAK,
    @JsonProperty("Snipe")
    SNIPE,
    @JsonProperty("Strengthen")
    STRENGTHEN,
    @JsonProperty("Stun")
    STUN,
    STUNNED,
    @JsonProperty("Swiftness")
    SWIFTNESS,
    @JsonProperty("Thorns")
    THORNS,
    @JsonProperty("Trample")
    TRAMPLE,
    @JsonProperty("Triage")
    TRIAGE,
    @JsonProperty("Void")
    VOID,
    @JsonProperty("Weaken")
    WEAKEN,
    @JsonProperty("Demoralize")
    DEMORALIZE,
    SUMMONER_HEALTH,
    SUMMONER_SPEED,
    SUMMONER_ATTACK,
    SUMMONER_RANGED,
    SUMMONER_MAGIC,
    SUMMONER_ARMOR,
    SUMMONER_BLAST,
    SUMMONER_VOID,
    SUMMONER_AFFLICTION
}
