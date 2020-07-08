package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.AttackRule;
import org.slos.battle.abilities.rule.hit.RetaliateRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class RetaliateAbility extends Ability implements AbilityEffect {

    public RetaliateAbility() {
        super(AbilityType.RETALIATE, AbilityClassification.ATTACK);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public AttackRule getEffect() {
        return new RetaliateRule();
    }
}
