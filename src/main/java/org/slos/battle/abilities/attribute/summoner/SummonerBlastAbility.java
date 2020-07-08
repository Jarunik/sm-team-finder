package org.slos.battle.abilities.attribute.summoner;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.attack.BlastAbility;
import org.slos.battle.abilities.rule.target.TargetFriendlyRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

import java.util.Collections;

public class SummonerBlastAbility extends Ability {

    public SummonerBlastAbility() {
        super(AbilityType.SUMMONER_BLAST, AbilityClassification.BUFF);
    }

    @Override
    public AbilityEffect getEffect() {
        return new BlastAbility();
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset(Collections.singletonList(new TargetFriendlyRule()));
    }
}
