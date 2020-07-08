package org.slos.battle.abilities.rule.turn;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.monster.MonsterBattleStats;

public interface OnTurnStartRule extends AbilityEffect {
    boolean executeOnTurnStart(MonsterBattleStats monsterBattleStats, GameContext gameContext);
}
