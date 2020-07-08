package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.abilities.rule.hit.PierceOnHitRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class PierceAbility extends Ability implements AbilityEffect {

    public PierceAbility() {
        super(AbilityType.PIERCING, AbilityClassification.ATTACK);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public OnAttackRule getEffect() {
        return new PierceOnHitRule();
    }
}
