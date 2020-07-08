package org.slos.battle.abilities.rule;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.monster.MonsterBattleStats;

public interface OnDeathRule extends AbilityEffect {
    public void executeOnDeathEffect(MonsterBattleStats deadGuy, MonsterBattleStats guyWithAbility, GameContext gameContext);
}
