package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.AttackRule;
import org.slos.battle.abilities.rule.attack.KnockoutRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class KnockoutAbility extends Ability implements AbilityEffect {

    public KnockoutAbility() {
        super(AbilityType.KNOCK_OUT, AbilityClassification.ATTACK);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public AttackRule<Integer> getEffect() {
        return new KnockoutRule();
    }
}
