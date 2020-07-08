package org.slos.splinterlands.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum LevelLimit {
    @JsonProperty("Novice")
    NOVICE(0, "Novice"),
    @JsonProperty("Bronze")
    BRONZE(1, "Bronze"),
    @JsonProperty("Silver")
    SILVER(2, "Silver"),
    @JsonProperty("Gold")
    GOLD(3, "Gold"),
    @JsonProperty("Diamond")
    DIAMOND(4, "Diamond"),
    @JsonProperty("Champion")
    CHAMPION(4, "Champion");

    private int id;
    private String stringValue;

    LevelLimit(int id, String stringValue) {
        this.id = id;
        this.stringValue = stringValue;
    }

    public int getId() {
        return id;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static LevelLimit fromId(Integer id) {
        switch (id) {
            case 0: return NOVICE;
            case 1: return BRONZE;
            case 2: return SILVER;
            case 3: return GOLD;
            case 4: return DIAMOND;
            case 5: return CHAMPION;
        }
        return null;
    }

    @JsonCreator
    public static LevelLimit forLevelLimit(String levelLimit) {
        for(LevelLimit ll: values()) {
            if(levelLimit.toUpperCase().contains(ll.toString())) {
//                System.out.println("Returning level limit of: " + ll + " from " + levelLimit);
                return ll;
            }
        }
        return null;
    }
}
