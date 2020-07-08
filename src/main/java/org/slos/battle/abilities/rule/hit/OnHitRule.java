package org.slos.battle.abilities.rule.hit;

import org.slos.battle.abilities.rule.AttackRule;
import org.slos.battle.abilities.rule.AttackRuleType;

public abstract class OnHitRule extends AttackRule {
    protected OnHitRule() {
        super(AttackRuleType.ON_HIT);
    }
}
