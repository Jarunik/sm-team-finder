package org.slos.battle.abilities.rule.target;

import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.target.TargetRulesetAbility;

public class TauntTargetRuleset implements AbilityEffect, TargetRulesetAbility {

    @Override
    public int getTargetPriority() {
        return 2;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset.Builder()
                .addRule(new TargetEnemyRule())
                .addRule(new TargetTauntRule())
                .addRule(new TargetFirstRule())
                .build();
    }
}
