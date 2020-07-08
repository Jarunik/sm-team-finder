package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.attribute.summoner.SummonerAbility;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.abilities.rule.hit.ApplyOnHitRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class AfflictionAbility extends Ability implements AbilityEffect, SummonerAbility {
    public AfflictionAbility() {
        super(AbilityType.AFFLICTION, AbilityClassification.ATTACK, AbilityClassification.SUMMONER_APPLIED);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public OnAttackRule getEffect() {
        return new ApplyOnHitRule(new AfflictedAbility(), 50);
    }

    @Override
    public TargetRuleset summonerAppliesAbilityTo() {
        return TargetRuleset.ENEMY;
    }

    @Override
    public Ability summonerAppliedAbility() {
        return new AfflictedAbility();
    }
}
