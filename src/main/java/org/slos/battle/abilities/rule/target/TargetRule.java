package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.List;

public interface TargetRule {
    List<MonsterBattleStats> selectTargets(MonsterBattleStats monsterBattleStats, List<MonsterBattleStats> selectFrom, GameContext gameContext);
}
