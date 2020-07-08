package org.slos.battle.abilities.rule;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.monster.MonsterBattleStats;

public interface OnRoundStartRule extends AbilityEffect {
    boolean executeOnRoundStart(MonsterBattleStats monsterBattleStats, GameContext gameContext);
}
