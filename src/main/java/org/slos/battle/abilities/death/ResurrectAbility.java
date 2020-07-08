package org.slos.battle.abilities.death;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.death.ResurrectRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class ResurrectAbility extends Ability implements AbilityEffect {
    private ResurrectRule resurrectRule = new ResurrectRule();

    public ResurrectAbility() {
        super(AbilityType.RESURRECT, AbilityClassification.ON_DEATH);
    }

    @Override
    public AbilityEffect getEffect() {
        return resurrectRule;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}
