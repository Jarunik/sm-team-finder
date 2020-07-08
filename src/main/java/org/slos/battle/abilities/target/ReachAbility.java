package org.slos.battle.abilities.target;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.ReachAttackCondition;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.rule.target.TargetSelfRule;

import java.util.Collections;

public class ReachAbility extends Ability {

    public ReachAbility() {
        super(AbilityType.REACH, AbilityClassification.ATTACK_CONDITION);
    }

    @Override
    public AbilityEffect getEffect() {
        return new ReachAttackCondition();
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset(Collections.singletonList(new TargetSelfRule()));
    }
}