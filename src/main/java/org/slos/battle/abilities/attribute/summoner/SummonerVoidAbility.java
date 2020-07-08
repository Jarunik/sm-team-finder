package org.slos.battle.abilities.attribute.summoner;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.attack.VoidAbility;
import org.slos.battle.abilities.rule.target.TargetFriendlyRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

import java.util.Collections;

public class SummonerVoidAbility extends Ability {

    public SummonerVoidAbility() {
        super(AbilityType.SUMMONER_VOID, AbilityClassification.BUFF);
    }

    @Override
    public AbilityEffect getEffect() {
        return new VoidAbility();
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset(Collections.singletonList(new TargetFriendlyRule()));
    }
}
