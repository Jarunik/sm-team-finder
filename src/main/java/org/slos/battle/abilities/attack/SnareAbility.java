package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.abilities.rule.hit.ApplyOnHitRule;
import org.slos.battle.abilities.rule.target.TargetFlyingRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.rule.target.TargetSelfRule;

public class SnareAbility extends Ability implements AbilityEffect {
    public SnareAbility() {
        super(AbilityType.SNARE, AbilityClassification.ATTACK);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public OnAttackRule getEffect() {
        TargetRuleset targetRuleset = new TargetRuleset.Builder()
                .addRule(new TargetSelfRule())
                .addRule(new TargetFlyingRule())
                .build();
        return new ApplyOnHitRule(new SnaredAbility(), 100, targetRuleset);
    }
}
