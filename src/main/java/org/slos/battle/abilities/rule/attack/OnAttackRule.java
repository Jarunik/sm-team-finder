package org.slos.battle.abilities.rule.attack;

import org.slos.battle.abilities.rule.AttackRule;
import org.slos.battle.abilities.rule.AttackRuleType;

public abstract class OnAttackRule extends AttackRule {
    public OnAttackRule() {
        super(AttackRuleType.ON_ATTACK);
    }
}
