package org.slos.battle.abilities.rule.attack;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.AttackRuleType;
import org.slos.battle.attack.AttackContext;

public class KnockoutRule extends DamageRule {

    public KnockoutRule() {
        super(AttackRuleType.ATTACKER_DAMAGE);
    }

    @Override
    public Integer execute(AttackContext attackContext, GameContext gameContext) {return 0;}

    @Override
    public Integer figureDamage(AttackContext attackContext, GameContext gameContext, Integer currentCalculatedDamage) {
        Integer damage = currentCalculatedDamage;
        if (attackContext.getTarget().containsAbility(AbilityType.STUNNED)) {
            damage = damage * 2;
        }

        return damage;
    }
}
