package org.slos.battle.abilities.rule.hit;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.target.TargetFriendlyRule;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.attack.AttackDamageExecutionService;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;

import java.util.List;

public class ReflectDamageOnHitRule extends OnHitRule {
    private DamageType damageType;
    private Integer damageAmount;
    private Boolean includeSecondaryAttacks;

    public ReflectDamageOnHitRule(DamageType damageType, Integer damageAmount, boolean includeSecondaryAttacks) {
        this.damageType = damageType;
        this.damageAmount = damageAmount;
        this.includeSecondaryAttacks = includeSecondaryAttacks;
    }

    @Override
    public Object execute(AttackContext attackContext, GameContext gameContext) {

        if ((attackContext.isInitiatingAttack()) || (!attackContext.isInitiatingAttack() && includeSecondaryAttacks)) {
            TargetFriendlyRule targetFriendlyRule = new TargetFriendlyRule();
            List<MonsterBattleStats> friendly = targetFriendlyRule.selectTargets(attackContext.getTarget(), null, gameContext);
            if (!friendly.contains(attackContext.getAttacker())) {
                if (attackContext.getAttacker() != null) {
                    Integer damageAmountToApply = damageAmount;

                    if (damageAmountToApply == null) {
                        try {
                            damageAmountToApply = Math.round(attackContext.getDamageValue() / 2f);
                            if (damageAmountToApply == 0) {
                                damageAmountToApply = 1;
                            }
                        } catch (Exception e) {
                            gameContext.log(e.toString());
                            throw e;
                        }
                    }

                    if (attackContext.getDamageType() == damageType) {
                        AttackDamageExecutionService attackDamageExecutionService = gameContext.getAttackService().getAttackDamageExecutionService();
                        AttackContext attackContext1 = new AttackContext(null, attackContext.getAttacker(), damageType, damageAmountToApply, null);

                        gameContext.log("Reflecting damage: %1$s", damageAmountToApply);
                        attackDamageExecutionService.applyDamage(attackContext1, gameContext);
                    }
                }
            }
        }
        return null;
    }
}
