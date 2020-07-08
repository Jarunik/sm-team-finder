package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;

import java.util.List;
import java.util.stream.Collectors;

public class TargetMeleeOrRangedRule implements TargetRule {
    @Override
    public List<MonsterBattleStats> selectTargets(MonsterBattleStats monsterBattleStats, List<MonsterBattleStats> selectFrom, GameContext gameContext) {
        return selectFrom.stream()
                .filter(monsterBattleStats1 ->
                        (monsterBattleStats1.isOfDamageType(DamageType.ATTACK)) ||
                        (monsterBattleStats1.isOfDamageType(DamageType.RANGED)))
                .collect(Collectors.toList());
    }
}
