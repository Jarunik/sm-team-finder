package org.slos.battle.abilities.rule.attack;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.hit.OnKillingHitRule;
import org.slos.battle.abilities.rule.turn.OnTurnEndRule;
import org.slos.battle.abilities.rule.turn.OnTurnStartRule;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.attack.AttackService;
import org.slos.battle.board.BoardPlacement;
import org.slos.battle.monster.MonsterBattleStats;

public class TrampleRule extends OnKillingHitRule implements OnTurnStartRule, OnTurnEndRule {
    private boolean needsToTrample = false;
    @JsonIgnore
    private MonsterBattleStats attackedTarget = null;
    private BoardPlacement targetBoardPlacement = null;

    @Override
    public boolean executeOnTurnStart(MonsterBattleStats monsterBattleStats, GameContext gameContext) {
        needsToTrample = false;
        attackedTarget = null;
        targetBoardPlacement = null;

        return true;
    }

    @Override
    public void executeOnTurnEnd(AttackContext attackContext, GameContext gameContext) {
        if (attackContext.getAttacker().isDead()) {
            return;
        }

        if (needsToTrample) {
            Integer targetLocation = targetBoardPlacement.getLocation();
            MonsterBattleStats nextTarget = targetBoardPlacement.getBoardSection().peekFromLocation(targetLocation);

            if ((targetBoardPlacement != null) && (nextTarget != null) && (attackedTarget.getId() == nextTarget.getId()) && (targetBoardPlacement.getBoardSection().getSectionSize() >= targetLocation + 1)) {
                if (targetLocation < (targetBoardPlacement.getBoardSection().getSectionSize() - 1)) {
                    nextTarget = targetBoardPlacement.getBoardSection().peekFromLocation(targetLocation + 1);
                }
            }

            if (nextTarget != null) {
                needsToTrample = true;
                MonsterBattleStats attacker = attackContext.getAttacker();
                gameContext.log("Trampling: %1$s --> %2$s", attacker.getId(), nextTarget.getId());
                AttackService attackService = gameContext.getAttackService();
                attackService.attackSpecific(attacker, attackContext.getDamageType(), nextTarget, gameContext);
            }
        }

        needsToTrample = false;
        attackedTarget = null;
        targetBoardPlacement = null;
    }

    @Override
    public Object execute(AttackContext attackContext, GameContext gameContext) {
        attackedTarget = attackContext.getTarget();
        targetBoardPlacement = attackedTarget.getPlacedIn();

        if (attackedTarget.isDead()) {
            needsToTrample = true;
        }

        return null;
    }
}
