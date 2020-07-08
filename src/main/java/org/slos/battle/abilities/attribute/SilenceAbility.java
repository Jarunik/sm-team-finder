package org.slos.battle.abilities.attribute;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.buff.BattleAttributeBuff;
import org.slos.battle.abilities.buff.Buff;
import org.slos.battle.abilities.rule.target.TargetEnemyRule;
import org.slos.battle.abilities.rule.target.TargetMagicRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.monster.BattleAttributeType;

public class SilenceAbility extends Ability implements BuffAbility {
    public SilenceAbility() {
        super(AbilityType.SILENCE, AbilityClassification.BUFF);
    }

    @Override
    public Buff getBuffEffect() {
        return new BattleAttributeBuff(BattleAttributeType.MAGIC, -1);
    }

    @Override
    public AbilityEffect getEffect() {
        return null;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset.Builder()
                .addRule(new TargetEnemyRule())
                .addRule(new TargetMagicRule())
                .build();
    }
}