package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.ArrayList;
import java.util.List;

public class TargetNotFirstRule implements TargetRule {
    @Override
    public List<MonsterBattleStats> selectTargets(MonsterBattleStats monsterBattleStats, List<MonsterBattleStats> selectFrom, GameContext gameContext) {
        if ((selectFrom == null) || selectFrom.size() == 0){
            return new ArrayList<>();
        }

        if (selectFrom.size() == 1) {
            return selectFrom;
        }

        gameContext.log("Target not First: " + selectFrom.subList(1, selectFrom.size()));
        return selectFrom.subList(1, selectFrom.size());
    }
}
