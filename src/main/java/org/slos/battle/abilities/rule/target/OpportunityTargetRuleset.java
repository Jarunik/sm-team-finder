package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AttackCondition;
import org.slos.battle.abilities.target.TargetRulesetAbility;
import org.slos.battle.monster.MonsterBattleStats;

public class OpportunityTargetRuleset implements AbilityEffect, TargetRulesetAbility, AttackCondition {

    @Override
    public boolean canAttack(MonsterBattleStats attacker, MonsterBattleStats target, GameContext gameContext) {
        return true;
    }

    @Override
    public int getTargetPriority() {
        return 1;//TODO: refactor this to more meaningful enum?
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset.Builder()
                .addRule(new TargetEnemyRule())
                .addRule(new TargetLowestHealthRule())
                .addRule(new TargetFirstRule())
                .build();
    }
}
