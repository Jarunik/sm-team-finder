package org.slos.splinterlands.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slos.splinterlands.domain.monster.ColorType;

public enum QuestMode {
    @JsonProperty("Lyanna's Call")
    LEYANNAS_CALL("Lyanna's Call", ColorType.GREEN),
    @JsonProperty("Rising Dead")
    RISING_DEAD("Rising Dead", ColorType.BLACK),
    @JsonProperty("Defend the Borders")
    DEFEND_THE_BOARDERS("Defend the Borders", ColorType.WHITE),
    @JsonProperty("Pirate Attacks")
    PIRATE_ATTACKS("Pirate Attacks", ColorType.BLUE),
    @JsonProperty("Stir the Volcano")
    STIR_THE_VOLCANO("Stir the Volcano", ColorType.RED),
    @JsonProperty("Gloridax Revenge")
    GLORIDAX_REVENGE("Gloridax Revenge", ColorType.GOLD),
    @JsonProperty("Proving Grounds")
    PROVING_GROUNDS("Proving Grounds", null),
    @JsonProperty("Stubborn Mercenaries")
    STUBBORN_MERCENARIES("Stubborn Mercenaries", null);

    private String id;
    private ColorType color;

    QuestMode(String id, ColorType color) {
        this.id = id;
        this.color = color;
    }

    public String id() {
        return id;
    }

    public ColorType color() {
        return color;
    }

    @JsonCreator
    public static QuestMode forQuestMode(String questMode) {

        for(QuestMode questModeType: values()) {
            if(questModeType.id().equals(questMode)) {
                return questModeType;
            }
        }
        return null;
    }
}
