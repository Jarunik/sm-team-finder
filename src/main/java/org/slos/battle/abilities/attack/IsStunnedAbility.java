package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.IsStunnedRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class IsStunnedAbility extends Ability {

    public IsStunnedAbility() {
        super(AbilityType.STUNNED, AbilityClassification.ON_TURN_START);
    }

    @Override
    public AbilityEffect getEffect() {
        return new IsStunnedRule();
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}
