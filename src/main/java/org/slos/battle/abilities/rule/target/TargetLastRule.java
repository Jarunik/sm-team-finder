package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.Collections;
import java.util.List;

public class TargetLastRule implements TargetRule{
    @Override
    public List<MonsterBattleStats> selectTargets(MonsterBattleStats monsterBattleStats, List<MonsterBattleStats> selectFrom, GameContext gameContext) {
        if ((selectFrom == null) || selectFrom.size() == 0){
            return null;
        }

        return Collections.singletonList(selectFrom.get(selectFrom.size() - 1));
    }
}
