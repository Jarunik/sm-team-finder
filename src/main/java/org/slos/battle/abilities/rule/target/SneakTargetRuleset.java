package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AttackCondition;
import org.slos.battle.abilities.target.TargetRulesetAbility;
import org.slos.battle.monster.MonsterBattleStats;

public class SneakTargetRuleset implements AttackCondition, TargetRulesetAbility {

    @Override
    public int getTargetPriority() {
        return 1;
    }

    @Override
    public boolean canAttack(MonsterBattleStats attacker, MonsterBattleStats target, GameContext gameContext) {
        return true; //TODO: is this correct?
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset.Builder()
                .addRule(new TargetEnemyRule())
                .addRule(new TargetLastRule())
                .build();
    }
}
