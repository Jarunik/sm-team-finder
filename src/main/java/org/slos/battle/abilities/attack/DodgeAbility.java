package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.AccuracyRule;
import org.slos.battle.abilities.rule.DodgeRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class DodgeAbility extends Ability implements AbilityEffect {

    public DodgeAbility() {
        super(AbilityType.DODGE, AbilityClassification.TARGET_EVADE);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public AccuracyRule getEffect() {
        return new DodgeRule();
    }
}
