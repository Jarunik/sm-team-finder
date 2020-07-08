package org.slos.battle.abilities.buff;

import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.monster.BattleAttributeType;

public class BattleAttributeBuff implements Buff, AbilityEffect {
    private BattleAttributeType battleAttributeType;
    private int buffQuantity;

    public BattleAttributeBuff(BattleAttributeType battleAttributeType, int buffQuantity) {
        this.battleAttributeType = battleAttributeType;
        this.buffQuantity = buffQuantity;
    }

    public BattleAttributeType getBattleAttributeType() {
        return battleAttributeType;
    }

    public int getBuffQuantity() {
        return buffQuantity;
    }

    @Override
    public BuffEffectType getBuffEffectType() {
        return BuffEffectType.BATTLE_ATTRIBUTE;
    }

    @Override
    public String toString() {
        return "BattleAttributeBuff{" +
                "battleAttributeType=" + battleAttributeType +
                ", buffQuantity=" + buffQuantity +
                '}';
    }
}
