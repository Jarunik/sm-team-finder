package org.slos.battle.abilities;

import org.slos.battle.GameContext;
import org.slos.battle.monster.MonsterBattleStats;

public class ReachAttackCondition implements AttackCondition {

    @Override
    public boolean canAttack(MonsterBattleStats attacker, MonsterBattleStats target, GameContext gameContext) {
        gameContext.log("Checking reach: %1$s", (attacker.getPlacedIn().getLocation() <= 2));
//        throw new RuntimeException();
        return attacker.getPlacedIn().getLocation() <= 2;
    }
}
