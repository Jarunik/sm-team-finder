package org.slos.battle.abilities.rule.attack;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.hit.ReflectDamageOnHitRule;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.attack.AttackDamageExecutionService;
import org.slos.battle.board.BoardPlacement;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;

public class BlastOnHitRule extends OnAttackRule {

    @Override
    public Void execute(AttackContext attackContext, GameContext gameContext) {
        BoardPlacement targetPlacement = attackContext.getTarget().getPlacedIn();

        if (targetPlacement.getLocation() < gameContext.getBoard().getBoardPositionCount() - 1) {
            MonsterBattleStats adjacentMonster = targetPlacement.getBoardSection().peekFromLocation(targetPlacement.getLocation() + 1);
            applyDamageAndCheckForDeath(attackContext, gameContext, adjacentMonster);
        }

        if (targetPlacement.getLocation() >= 2) {
            MonsterBattleStats adjacentMonster = targetPlacement.getBoardSection().peekFromLocation(targetPlacement.getLocation() - 1);
            applyDamageAndCheckForDeath(attackContext, gameContext, adjacentMonster);
        }

        return null;
    }

    private void applyDamageAndCheckForDeath(AttackContext attackContext, GameContext gameContext, MonsterBattleStats adjacentMonster) {
        AttackDamageExecutionService attackDamageExecutionService = gameContext.getAttackService().getAttackDamageExecutionService();

        if (adjacentMonster != null) {
            int damageAmount = Math.round(attackContext.getDamageValue() / 2f);
            AttackContext attackContextAdjacent = new AttackContext(attackContext.getAttacker(), adjacentMonster, attackContext.getDamageType(), damageAmount, null, false);

            gameContext.log("Blasting %1$s for %2$s", attackContextAdjacent.getTarget().getId(), damageAmount);
            attackDamageExecutionService.applyDamage(attackContextAdjacent, gameContext);

            if ((attackContext.getDamageType().equals(DamageType.MAGIC)) && (attackContextAdjacent.getTarget().containsAbility(AbilityType.MAGIC_REFLECT))) {
                ReflectDamageOnHitRule reflect = new ReflectDamageOnHitRule(DamageType.MAGIC, null, true);
                reflect.execute(attackContextAdjacent, gameContext);
            }
            if ((attackContext.getDamageType().equals(DamageType.RANGED)) && (attackContextAdjacent.getTarget().containsAbility(AbilityType.RETURN_FIRE))) {
                ReflectDamageOnHitRule reflect = new ReflectDamageOnHitRule(DamageType.MAGIC, null, true);
                reflect.execute(attackContextAdjacent, gameContext);
            }
        }

        if ((adjacentMonster != null) && (adjacentMonster.isDead())) {
            gameContext.getDeathService().registerDeath(adjacentMonster, attackContext);
        }
    }
}
