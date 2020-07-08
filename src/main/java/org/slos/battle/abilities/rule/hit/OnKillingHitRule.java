package org.slos.battle.abilities.rule.hit;

import org.slos.battle.abilities.rule.AttackRule;
import org.slos.battle.abilities.rule.AttackRuleType;

public abstract class OnKillingHitRule extends AttackRule {
    public OnKillingHitRule() {
        super(AttackRuleType.ON_KILL);
    }
}
