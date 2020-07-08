package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.ArrayList;
import java.util.List;

public class TargetFlyingRule implements TargetRule {

    @Override
    public List<MonsterBattleStats> selectTargets(MonsterBattleStats monsterBattleStats, List<MonsterBattleStats> selectFrom, GameContext gameContext) {
        List<MonsterBattleStats> flying = new ArrayList<>();
        for (MonsterBattleStats possibleTarget : selectFrom) {
            if (possibleTarget.containsAbility(AbilityType.FLYING)) {
                flying.add(possibleTarget);
            }
        }

        return flying;
    }
}
