package org.slos.battle.abilities.attribute.summoner;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public interface SummonerAbility {

    TargetRuleset summonerAppliesAbilityTo();
    Ability summonerAppliedAbility();
}
