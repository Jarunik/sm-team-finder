package org.slos.battle.abilities.rule;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.turn.OnTurnEndRule;
import org.slos.battle.abilities.rule.turn.OnTurnStartRule;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.attack.AttackService;
import org.slos.battle.monster.MonsterBattleStats;

public class MultipleAttacksRule implements OnTurnEndRule, OnTurnStartRule {
    private Integer attackCount;
    private Integer attacksCompleted = 0;

    public MultipleAttacksRule(Integer attackCount) {
        this.attackCount = attackCount;
    }

    @Override
    public boolean executeOnTurnStart(MonsterBattleStats monsterBattleStats, GameContext gameContext) {
        attackCount = 0;
        return true;
    }

    @Override
    public void executeOnTurnEnd(AttackContext attackContext, GameContext gameContext) {
        MonsterBattleStats attacker = attackContext.getAttacker();

        if (attacksCompleted < attackCount) {
            if (!attacker.isDead()) {
                AttackService attackService = gameContext.getAttackService();
                attackService.attack(attacker, attackContext.getDamageType(), gameContext);

                attacksCompleted++;
            }
        }
    }
}
