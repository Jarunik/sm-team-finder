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

import java.util.Collections;

public class RustAbility extends Ability implements BuffAbility {
    public RustAbility() {
        super(AbilityType.RUST, AbilityClassification.BUFF);
    }

    @Override
    public Buff getBuffEffect() {
        return new BattleAttributeBuff(BattleAttributeType.ARMOR, -2);
    }

    @Override
    public AbilityEffect getEffect() {
        return null;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset(Collections.singletonList(new TargetEnemyRule()));
    }
}