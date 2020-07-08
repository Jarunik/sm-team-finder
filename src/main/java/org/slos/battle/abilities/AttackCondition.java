package org.slos.battle.abilities;

import org.slos.battle.GameContext;
import org.slos.battle.monster.MonsterBattleStats;

public interface AttackCondition extends AbilityEffect {

    boolean canAttack(MonsterBattleStats attacker, MonsterBattleStats target, GameContext gameContext);
}
