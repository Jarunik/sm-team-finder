package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.target.SnaredRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class SnaredAbility extends Ability implements AbilityEffect {

    public SnaredAbility() {
        super(AbilityType.SNARED, AbilityClassification.TARGET_EVADE);
    }

    @Override
    public AbilityEffect getEffect() {
        return new SnaredRule();
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}
