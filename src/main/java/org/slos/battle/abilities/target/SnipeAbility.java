package org.slos.battle.abilities.target;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.target.SnipeTargetRuleset;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.rule.target.TargetSelfRule;

import java.util.Collections;

public class SnipeAbility extends Ability {

    public SnipeAbility() {
        super(AbilityType.SNIPE, AbilityClassification.TARGET_CONDITION);
    }

    @Override
    public AbilityEffect getEffect() {
        return new SnipeTargetRuleset();
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset(Collections.singletonList(new TargetSelfRule()));
    }
}