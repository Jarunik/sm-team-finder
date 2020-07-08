package org.slos.battle.abilities.rule;

import org.slos.battle.GameContext;
import org.slos.battle.StatChangeContext;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.splinterlands.domain.monster.DamageType;

public interface OnBattleAttributeChangeRule extends AbilityEffect {
    void executeStatChangeRule(StatChangeContext statChangeContext, DamageType damageType, GameContext gameContext);
}
