package org.slos.battle.abilities.attribute;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.buff.BattleAttributeBuff;
import org.slos.battle.abilities.buff.Buff;
import org.slos.battle.abilities.rule.target.TargetEnemyRule;
import org.slos.battle.abilities.rule.target.TargetMeleeOrRangedRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.monster.BattleAttributeType;

public class BlindAbility extends Ability implements BuffAbility {
    public BlindAbility() {
        super(AbilityType.BLIND, AbilityClassification.BUFF);
    }

    @Override
    public AbilityEffect getEffect() {
        return null;//TODO: refactor this away from buff
    }

    @Override
    public Buff getBuffEffect() {
        return new BattleAttributeBuff(BattleAttributeType.HIT_CHANCE, -15);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset.Builder()
                .addRule(new TargetEnemyRule())
                .addRule(new TargetMeleeOrRangedRule())
                .build();
    }
}