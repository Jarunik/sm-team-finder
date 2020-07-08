package org.slos.splinterlands.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashSet;
import java.util.Set;

public enum GameRuleType {
    STANDARD("Standard",false),
    BACK_TO_BASICS("Back to Basics", false),
    SILENCED_SUMMONERS("Silenced Summoners", false),
    AIM_TRUE("Aim True", false),
    SUPER_SNEAK("Super Sneak", false),
    WEAK_MAGIC("Weak Magic", false),
    UNPROTECTED("Unprotected", false),
    TARGET_PRACTICE("Target Practice", false),
    FOG_OF_WAR("Fog of War", false),
    ARMORED_UP("Armored Up", false),
    HEALED_OUT("Healed Out", false),
    EARTHQUAKE("Earthquake", false),
    REVERSE_SPEED("Reverse Speed", false),
    KEEP_YOUR_DISTANCE("Keep Your Distance", true),
    LOST_LEGENDARIES("Lost Legendaries", true),
    MELEE_MAYHEM("Melee Mayhem", false),
    TAKING_SIDES("Taking Sides", true),
    RISE_OF_THE_COMMONS("Rise of the Commons", true),
    UP_CLOSE_AND_PERSONAL("Up Close & Personal", true),
    BROKEN_ARROWS("Broken Arrows", true),
    LOST_MAGIC("Lost Magic", true),
    CLOSE_RANGE("Close Range", false),
    ODD_ONES_OUT("Odd Ones Out", true),
    LITTLE_LEAGUE("Little League", true),
    EVEN_STEVENS("Even Stevens", true),
    EQUALIZER("Equalizer", false),
    HEAVY_HITTERS("Heavy Hitters", false);

    private boolean isRestrictive;
    private String id;

    GameRuleType(String id, boolean isRestrictive) {
        this.isRestrictive = isRestrictive;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isRestrictive() {
        return isRestrictive;
    }

    public static Set<GameRuleType> getFromRuleset(String ruleset) {
        Set<GameRuleType> ruleTypeSet = new HashSet<>();

        String[] rulesRaw = ruleset.split("\\|");
        for (int i = 0; i < rulesRaw.length; i++) {
            GameRuleType gameRuleType = GameRuleType.forGameRuleType(rulesRaw[i]);
            ruleTypeSet.add(gameRuleType);
        }

        return ruleTypeSet;
    }

    @JsonCreator
    public static GameRuleType forGameRuleType(String gameRule) {
        for(GameRuleType gameRuleType: values()) {
            if(gameRuleType.getId().equals(gameRule)) {
                return gameRuleType;
            }
        }
        return null;
    }
}
