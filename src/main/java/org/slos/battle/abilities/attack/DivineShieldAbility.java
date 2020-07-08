package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.AttackRule;
import org.slos.battle.abilities.rule.hit.IgnoreHitDamageRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class DivineShieldAbility extends Ability implements AbilityEffect {
    private AttackRule<Integer> attackRule = new IgnoreHitDamageRule(1);

    public DivineShieldAbility() {
        super(AbilityType.DIVINE_SHIELD, AbilityClassification.ATTACK);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public AttackRule<Integer> getEffect() {
        return attackRule;
    }
}
