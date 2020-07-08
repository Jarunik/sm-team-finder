package org.slos.battle.abilities.death;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.death.ScavengerRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class ScavengerAbility extends Ability implements AbilityEffect {
    private ScavengerRule scavengerRule = new ScavengerRule();

    public ScavengerAbility() {
        super(AbilityType.SCAVENGER, AbilityClassification.ON_DEATH);
    }

    @Override
    public AbilityEffect getEffect() {
        return scavengerRule;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}
