package org.slos.battle.abilities.target;

import org.slos.battle.abilities.rule.target.TargetRuleset;

public interface TargetRulesetAbility {
    TargetRuleset getTargetRuleset();
    int getTargetPriority();
}
