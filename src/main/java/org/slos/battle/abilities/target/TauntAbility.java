package org.slos.battle.abilities.target;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.attribute.BuffAbility;
import org.slos.battle.abilities.buff.Buff;
import org.slos.battle.abilities.rule.target.TargetEnemyRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class TauntAbility extends Ability implements BuffAbility, AbilityEffect {

    public TauntAbility() {
        super(AbilityType.TAUNT, AbilityClassification.TARGET_CONDITION, AbilityClassification.BUFF);
    }

    @Override
    public Buff getBuffEffect() {
        return new IsTauntedAbility();
    }

    @Override
    public AbilityEffect getEffect() {
        return null;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        TargetRuleset targetRuleset = new TargetRuleset.Builder()
                .addRule(new TargetEnemyRule())
                .build();

        return targetRuleset;
    }
}
