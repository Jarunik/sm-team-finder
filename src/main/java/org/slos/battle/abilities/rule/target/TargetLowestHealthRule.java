package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.Collections;
import java.util.List;

public class TargetLowestHealthRule implements TargetRule {
    @Override
    public List<MonsterBattleStats> selectTargets(MonsterBattleStats attacker, List<MonsterBattleStats> selectFrom, GameContext gameContext) {
        if ((selectFrom == null) || selectFrom.size() == 0){
            return null;
        }

        MonsterBattleStats lowestHealthTarget = selectFrom.get(0);
        for (MonsterBattleStats target : selectFrom) {
            if (target.getHealth().getValue() < lowestHealthTarget.getHealth().getValue()) {
                lowestHealthTarget = target;
            }
        }

        return Collections.singletonList(lowestHealthTarget);
    }
}
