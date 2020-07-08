package org.slos.battle.abilities.rule.hit;

import org.slos.battle.GameContext;
import org.slos.battle.StatChangeContext;
import org.slos.battle.abilities.attack.EnrageAbility;
import org.slos.battle.abilities.rule.OnBattleAttributeChangeRule;
import org.slos.battle.monster.BattleAttributeType;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;

public class EnrageRule implements OnBattleAttributeChangeRule {
    private final EnrageAbility.EnragedState enragedState;

    public EnrageRule(Integer speedIncreaseAmount, EnrageAbility.EnragedState enragedState) {
        this.enragedState = enragedState;
    }

    @Override
    public void executeStatChangeRule(StatChangeContext statChangeContext, DamageType damageType, GameContext gameContext) {
        MonsterBattleStats target = statChangeContext.getTarget();

        if ((enragedState.isEnraged()) && (statChangeContext.getBattleAttributeType() == BattleAttributeType.HEALTH)) {
            if (target.getHealth().getValue() >= target.getHealth().getBaseValue() + target.getHealth().getBuffBaseValue()) {

                toggleEnrage(target, -1, -1, gameContext);
            }
        }

        if ((!enragedState.isEnraged()) && (statChangeContext.getBattleAttributeType() == BattleAttributeType.HEALTH)) {
            if (target.getHealth().getValue() < target.getHealth().getBaseValue() + target.getHealth().getBuffBaseValue()) {
                toggleEnrage(target, 1, 1, gameContext);
            }
        }
    }

    private void toggleEnrage(MonsterBattleStats target, Integer damageIncreaseAmount, Integer speedIncreaseAmount, GameContext gameContext) {
        for (DamageType damageType : target.getDamageValues().keySet()) {

            int targetDamageValueToChange;

            if (damageIncreaseAmount > 0) {
                targetDamageValueToChange = (int) Math.ceil((double)target.getDamageValue(damageType).getValue() / 2);
            }
            else {
                targetDamageValueToChange = (int) Math.ceil((double)target.getDamageValue(damageType).getValue() / 3);
            }

            targetDamageValueToChange *= damageIncreaseAmount;

            gameContext.log("Enrage Change[" + target.getId() + "] on " + damageType + " for: " + targetDamageValueToChange);

            target.getDamageValue(damageType).addToBuffValue(targetDamageValueToChange);
        }

        int speedValueToChange;

        if (damageIncreaseAmount > 0) {
            speedValueToChange = (int) Math.ceil((double)target.getSpeed().getValue() / 2);
        }
        else {
            speedValueToChange = (int) Math.ceil((double)target.getSpeed().getValue() / 3);
        }

        speedValueToChange *= speedIncreaseAmount;
        gameContext.log("Enrage Change[" + target.getId() + "] on SPEED for: " + speedValueToChange);

        target.getSpeed().addToBuffValue(speedValueToChange);
        enragedState.setEnraged(true);
    }
}
