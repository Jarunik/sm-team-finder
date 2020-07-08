package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.attribute.summoner.SummonerAbility;
import org.slos.battle.abilities.rule.attack.BlastOnHitRule;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class BlastAbility extends Ability implements AbilityEffect, SummonerAbility {

    public BlastAbility() {
        super(AbilityType.BLAST, AbilityClassification.ATTACK, AbilityClassification.SUMMONER_APPLIED);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public OnAttackRule getEffect() {
        return new BlastOnHitRule();
    }

    @Override
    public TargetRuleset summonerAppliesAbilityTo() {
        return TargetRuleset.FRIENDLY;
    }

    @Override
    public Ability summonerAppliedAbility() {
        return new BlastAbility();
    }
}
