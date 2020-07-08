package org.slos.battle.abilities.target;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.target.OpportunityTargetRuleset;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.rule.target.TargetSelfRule;

import java.util.Collections;

public class OpportunityAbility extends Ability {

    public OpportunityAbility() {
        super(AbilityType.OPPORTUNITY, AbilityClassification.TARGET_CONDITION, AbilityClassification.ATTACK_CONDITION);
    }

    @Override
    public AbilityEffect getEffect() {
        return new OpportunityTargetRuleset();
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset(Collections.singletonList(new TargetSelfRule()));
    }
}