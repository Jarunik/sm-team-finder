package org.slos.battle.abilities.rule.attack;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.AttackRule;
import org.slos.battle.abilities.rule.AttackRuleType;
import org.slos.battle.attack.AttackContext;

public abstract class DamageRule extends AttackRule<Integer> {

    public DamageRule(AttackRuleType attackRuleType) {
        super(attackRuleType);
    }

    @Override
    public abstract Integer execute(AttackContext attackContext, GameContext gameContext);

    public abstract Integer figureDamage(AttackContext attackContext, GameContext gameContext, Integer currentCalculatedDamage);
}