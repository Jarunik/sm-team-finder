package org.slos.battle.abilities.death;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.death.LastStandRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class LastStandAbility extends Ability implements AbilityEffect {
    private LastStandRule lastStandRule = new LastStandRule();

    public LastStandAbility() {
        super(AbilityType.LAST_STAND, AbilityClassification.ON_DEATH, AbilityClassification.ON_TURN_START);
    }

    @Override
    public AbilityEffect getEffect() {
        return lastStandRule;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}