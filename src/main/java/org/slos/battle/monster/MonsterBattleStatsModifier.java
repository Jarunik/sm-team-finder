package org.slos.battle.monster;

public class MonsterBattleStatsModifier {
    private BattleAttributeType attribute;
    private Integer modifierAmount;

    public MonsterBattleStatsModifier(BattleAttributeType attribute, Integer modifierAmount) {
        this.attribute = attribute;
        this.modifierAmount = modifierAmount;
    }

    public BattleAttributeType getAttribute() {
        return attribute;
    }

    public Integer getModifierAmount() {
        return modifierAmount;
    }

    @Override
    public String toString() {
        return "MonsterBattleStatsModifier{" +
                "attribute=" + attribute +
                ", modifierAmount=" + modifierAmount +
                '}';
    }
}
