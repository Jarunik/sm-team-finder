package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.abilities.rule.hit.LeechOnHitRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class LifeLeechAbility extends Ability implements AbilityEffect {

    public LifeLeechAbility() {
        super(AbilityType.LIFE_LEECH, AbilityClassification.ATTACK);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public OnAttackRule getEffect() {
        return new LeechOnHitRule();
    }
}
