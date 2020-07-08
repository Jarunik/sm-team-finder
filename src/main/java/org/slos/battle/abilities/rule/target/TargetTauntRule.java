package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.List;
import java.util.stream.Collectors;

public class TargetTauntRule implements TargetRule {

    @Override
    public List<MonsterBattleStats> selectTargets(MonsterBattleStats attacker, List<MonsterBattleStats> selectFrom, GameContext gameContext) {
        List<MonsterBattleStats> enemiesWithTaunt = selectFrom.stream()
                .filter(monsterBattleStats -> monsterBattleStats.containsAbility(AbilityType.TAUNT))
                .collect(Collectors.toList());

        return enemiesWithTaunt;
    }
}
