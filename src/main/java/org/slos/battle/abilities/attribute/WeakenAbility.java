package org.slos.battle.abilities.attribute;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.buff.BattleAttributeBuff;
import org.slos.battle.abilities.buff.Buff;
import org.slos.battle.abilities.rule.target.TargetEnemyRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.monster.BattleAttributeType;

public class WeakenAbility extends Ability implements BuffAbility {
    public WeakenAbility() {
        super(AbilityType.WEAKEN, AbilityClassification.BUFF);
    }

    @Override
    public Buff getBuffEffect() {
        return new BattleAttributeBuff(BattleAttributeType.HEALTH, -1);
    }

    @Override
    public AbilityEffect getEffect() {
        return null;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset.Builder()
                .addRule(new TargetEnemyRule())
//                .addRule(new TargetGreaterOneHealthRule())
                .build();
    }
}