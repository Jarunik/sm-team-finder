package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.attribute.summoner.SummonerAbility;
import org.slos.battle.abilities.rule.ReduceHalfDamageRule;
import org.slos.battle.abilities.rule.attack.DamageRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.splinterlands.domain.monster.DamageType;

public class VoidAbility extends Ability implements AbilityEffect, SummonerAbility {

    public VoidAbility() {
        super(AbilityType.VOID, AbilityClassification.ATTACK, AbilityClassification.SUMMONER_APPLIED);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public DamageRule getEffect() {
//        return new FlatDamageRule(-1, DamageType.MAGIC);
        return new ReduceHalfDamageRule(DamageType.MAGIC);
    }

    @Override
    public Ability summonerAppliedAbility() {
        return new VoidAbility();
    }

    @Override
    public TargetRuleset summonerAppliesAbilityTo() {
        return TargetRuleset.FRIENDLY;
    }
}
