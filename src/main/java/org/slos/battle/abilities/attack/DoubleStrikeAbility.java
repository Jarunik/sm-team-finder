package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.MultipleAttacksRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class DoubleStrikeAbility extends Ability implements AbilityEffect {

    public DoubleStrikeAbility() {
        super(AbilityType.DOUBLE_STRIKE, AbilityClassification.ON_TURN_END);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public AbilityEffect getEffect() {
        return new MultipleAttacksRule(2);
    }
}
