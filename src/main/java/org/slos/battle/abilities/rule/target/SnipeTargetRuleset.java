package org.slos.battle.abilities.rule.target;

import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.target.TargetRulesetAbility;

public class SnipeTargetRuleset implements AbilityEffect, TargetRulesetAbility {

    @Override
    public int getTargetPriority() {
        return 1;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset.Builder()
                .addRule(new TargetEnemyRule())
                .addRule(new TargetNotFirstRule())
                .addRule(new TargetNonMeleeRule())
                .addRule(new TargetFirstRule())
                .build();
    }
}
