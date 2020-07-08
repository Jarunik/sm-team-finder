package org.slos.battle.abilities.target;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.buff.Buff;
import org.slos.battle.abilities.buff.BuffEffectType;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.rule.target.TargetSelfRule;
import org.slos.battle.abilities.rule.target.TauntTargetRuleset;

import java.util.Collections;

public class IsTauntedAbility extends Ability implements Buff, AbilityEffect, TargetRulesetAbility {

    public IsTauntedAbility() {
        super(AbilityType.TAUNTED, AbilityClassification.TARGET_CONDITION);
    }

    @Override
    public int getTargetPriority() {
        return 2;
    }

    @Override
    public BuffEffectType getBuffEffectType() {
        return BuffEffectType.TARGET_CONDITION;
    }

    @Override
    public AbilityEffect getEffect() {
        return new TauntTargetRuleset();
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset(Collections.singletonList(new TargetSelfRule()));
    }
}