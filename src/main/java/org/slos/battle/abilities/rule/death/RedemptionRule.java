package org.slos.battle.abilities.rule.death;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.AttackRuleset;
import org.slos.battle.abilities.rule.OnDeathRule;
import org.slos.battle.abilities.rule.target.TargetEnemyRule;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;

import java.util.List;

public class RedemptionRule implements OnDeathRule {
    boolean alreadyTriggered = false;

    @Override
    public void executeOnDeathEffect(MonsterBattleStats deadGuy, MonsterBattleStats guyWithAbility, GameContext gameContext) {
        if ((deadGuy == guyWithAbility) && (!alreadyTriggered)) {
            alreadyTriggered = true;

            TargetEnemyRule targetEnemyRule = new TargetEnemyRule();
            List<MonsterBattleStats> enemies = targetEnemyRule.selectTargets(deadGuy, null, gameContext);
            gameContext.log("Applying redemption from: %1$s", deadGuy.getId());


            for (MonsterBattleStats monsterBattleStats : enemies) {
                gameContext.log("Redemption damage on: %1$s", monsterBattleStats);
                AttackContext attackContext = new AttackContext(null, monsterBattleStats, DamageType.ATTACK, 1, AttackRuleset.EMPTY);
                gameContext.getAttackService().getAttackDamageExecutionService().applyDamage(attackContext, gameContext);
                if (monsterBattleStats.isDead()) {
                    gameContext.getDeathService().registerDeath(monsterBattleStats, attackContext);
                }
            }
        }
    }
}
