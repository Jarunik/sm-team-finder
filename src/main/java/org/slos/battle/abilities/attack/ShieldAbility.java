package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.ReduceHalfDamageRule;
import org.slos.battle.abilities.rule.attack.DamageRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.splinterlands.domain.monster.DamageType;

public class ShieldAbility extends Ability implements AbilityEffect {

    public ShieldAbility() {
        super(AbilityType.SHIELD, AbilityClassification.ATTACK);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public DamageRule getEffect() {
        return new ReduceHalfDamageRule(DamageType.ATTACK, DamageType.RANGED);
    }
}
