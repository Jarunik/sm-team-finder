package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.hit.OnHitRule;
import org.slos.battle.abilities.rule.hit.ReflectDamageOnHitRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.splinterlands.domain.monster.DamageType;

public class ReturnFireAbility extends Ability implements AbilityEffect {

    public ReturnFireAbility() {
        super(AbilityType.RETURN_FIRE, AbilityClassification.ATTACK);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
//    public OnHitRule getEffect() {
    public OnHitRule getEffect() {
        return new ReflectDamageOnHitRule(DamageType.RANGED, null, true);
    }
}
