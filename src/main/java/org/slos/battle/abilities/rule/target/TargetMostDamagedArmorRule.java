package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.Collections;
import java.util.List;

public class TargetMostDamagedArmorRule implements TargetRule {

    @Override
    public List<MonsterBattleStats> selectTargets(MonsterBattleStats healer, List<MonsterBattleStats> selectFrom, GameContext gameContext) {
        if ((selectFrom == null) || (selectFrom.size() == 0)){
            return null;
        }

        MonsterBattleStats mostDamaged = null;
        Integer highestAmount = 0;

        for (MonsterBattleStats monsterBattleStats : selectFrom) {
            Integer checkDamage = monsterBattleStats.getArmor().getBaseValue() + monsterBattleStats.getArmor().getBuffBaseValue() - monsterBattleStats.getArmor().getValue();

            if ((checkDamage > 0) && (checkDamage > highestAmount)) {
                mostDamaged = monsterBattleStats;
                highestAmount = checkDamage;
            }
        }

        return Collections.singletonList(mostDamaged);
    }
}
