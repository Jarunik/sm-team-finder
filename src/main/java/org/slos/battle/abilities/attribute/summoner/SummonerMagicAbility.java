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
import org.slos.battle.abilities.rule.target.TargetMagicRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.monster.BattleAttributeType;

public class SummonerMagicAbility extends Ability implements BuffAbility {
    int buffQuantity;

    public SummonerMagicAbility(int buffQuantity) {
        super(AbilityType.SUMMONER_MAGIC, AbilityClassification.BUFF);
        this.buffQuantity = buffQuantity;
    }

    @Override
    public Buff getBuffEffect() {
        return new BattleAttributeBuff(BattleAttributeType.MAGIC, buffQuantity);
    }

    @Override
    public AbilityEffect getEffect() {
        return null;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        if (buffQuantity > 0) {
            return new TargetRuleset.Builder()
                    .addRule(new TargetFriendlyRule())
                    .addRule(new TargetMagicRule())
                    .build();
        }
        else {
            return new TargetRuleset.Builder()
                    .addRule(new TargetEnemyRule())
                    .addRule(new TargetMagicRule())
                    .build();
        }
    }
}
