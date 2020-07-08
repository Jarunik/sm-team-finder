package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.abilities.rule.hit.ShatterOnHitRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class ShatterAbility extends Ability implements AbilityEffect {

    public ShatterAbility() {
        super(AbilityType.SHATTER, AbilityClassification.ATTACK);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public OnAttackRule getEffect() {
        return new ShatterOnHitRule();
    }
}
