package org.slos.battle.abilities.attribute.summoner;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.attribute.BuffAbility;
import org.slos.battle.abilities.buff.BattleAttributeBuff;
import org.slos.battle.abilities.buff.Buff;
import org.slos.battle.abilities.rule.target.TargetEnemyRule;
import org.slos.battle.abilities.rule.target.TargetFriendlyRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.monster.BattleAttributeType;

import java.util.Collections;

public class SummonerArmorAbility extends Ability implements BuffAbility {
    int buffQuantity;

    public SummonerArmorAbility(int buffQuantity) {
        super(AbilityType.SUMMONER_ARMOR, AbilityClassification.BUFF);
        this.buffQuantity = buffQuantity;
    }

    @Override
    public Buff getBuffEffect() {
        return new BattleAttributeBuff(BattleAttributeType.ARMOR, buffQuantity);
    }

    @Override
    public AbilityEffect getEffect() {
        return null;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        if (buffQuantity > 0) {
            return new TargetRuleset(Collections.singletonList(new TargetFriendlyRule()));
        }
        else {
            return new TargetRuleset(Collections.singletonList(new TargetEnemyRule()));
        }
    }
}
