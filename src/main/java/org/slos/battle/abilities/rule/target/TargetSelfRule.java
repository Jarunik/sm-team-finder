package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.Collections;
import java.util.List;

public class TargetSelfRule implements TargetRule {
    @Override
    public List<MonsterBattleStats> selectTargets(MonsterBattleStats monsterBattleStats, List<MonsterBattleStats> selectFrom, GameContext gameContext) {
        if (monsterBattleStats == null) {
            throw new IllegalStateException("Can not target self on null.");
        }

        return Collections.singletonList(monsterBattleStats);
    }
}
