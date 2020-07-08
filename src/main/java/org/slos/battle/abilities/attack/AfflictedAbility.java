package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.AfflictedRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class AfflictedAbility extends Ability {

    public AfflictedAbility() {
        super(AbilityType.AFFLICTED, AbilityClassification.CUSTOM);
    }

    @Override
    public AbilityEffect getEffect() {
        return new AfflictedRule();
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}
