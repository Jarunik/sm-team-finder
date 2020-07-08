package org.slos.battle.abilities.rule;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.hit.OnHitRule;
import org.slos.battle.attack.AttackContext;

public class EnrageOnHealRule extends OnHitRule {
//    private final Integer damageIncreaseAmount;
//    private final Integer speedIncreaseAmount;
//    private final EnrageAbility.EnragedState enragedState;
//
//    public EnrageOnHealRule(Integer damageIncreaseAmount, Integer speedIncreaseAmount, EnrageAbility.EnragedState enragedState) {
//        this.damageIncreaseAmount = damageIncreaseAmount;
//        this.speedIncreaseAmount = speedIncreaseAmount;
//        this.enragedState = enragedState;
//    }
//
    @Override
    public Void execute(AttackContext attackContext, GameContext gameContext) {
        throw new RuntimeException("IS THIS USED?"); //TODO Fix this
//        if (!enragedState.isEnraged()) {
//            MonsterBattleStats target = attackContext.getTarget();
//
//            if (target.getHealth().getValue() < target.getHealth().getBaseValue()) {
//
//                for (DamageType damageType : target.getDamageValues().keySet()) {
//                    target.getDamageValue(damageType).addToBuffValue(damageIncreaseAmount);
//                }
//
//                target.getSpeed().addToBuffValue(speedIncreaseAmount);
//
//                enragedState.setEnraged(true);
//            }
//        }
//
//        return null;
    }
}
