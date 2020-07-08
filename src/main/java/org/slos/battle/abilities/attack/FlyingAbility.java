package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.AccuracyRule;
import org.slos.battle.abilities.rule.FlyingRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class FlyingAbility extends Ability implements AbilityEffect {

    public FlyingAbility() {
        super(AbilityType.FLYING, AbilityClassification.TARGET_EVADE);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public AccuracyRule getEffect() {
        return new FlyingRule();
    }
}
