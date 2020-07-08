package org.slos.battle.abilities.rule.turn;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.OnRoundStartRule;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;

public class PoisonedRule implements OnRoundStartRule {
    private final static int DAMAGE_AMOUNT = 2;

    @Override
    public boolean executeOnRoundStart(MonsterBattleStats victim, GameContext gameContext) {
        gameContext.log("Poison causing damage: %1$s", DAMAGE_AMOUNT);
        AttackContext attackContext = new AttackContext(null, victim, DamageType.SYSTEM, DAMAGE_AMOUNT, null);
        gameContext.getAttackService().getAttackDamageExecutionService().applyDamage(attackContext, gameContext);

        return true;
    }
}
