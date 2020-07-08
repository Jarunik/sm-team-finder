package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.hit.OnHitRule;
import org.slos.battle.abilities.rule.hit.ReflectDamageOnHitRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.splinterlands.domain.monster.DamageType;

public class ThornsAbility extends Ability implements AbilityEffect {

    public ThornsAbility() {
        super(AbilityType.THORNS, AbilityClassification.ATTACK);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
//    public OnHitRule getEffect() {
    public OnHitRule getEffect() {
        return new ReflectDamageOnHitRule(DamageType.ATTACK, 2, false);
    }
}
