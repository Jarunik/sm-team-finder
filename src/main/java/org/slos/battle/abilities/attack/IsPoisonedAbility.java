package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.OnRoundStartRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.rule.turn.PoisonedRule;

public class IsPoisonedAbility extends Ability {

    public IsPoisonedAbility() {
        super(AbilityType.POISONED, AbilityClassification.ON_ROUND_START);
    }

    @Override
    public OnRoundStartRule getEffect() {
        return new PoisonedRule();
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}
