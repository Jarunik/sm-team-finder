package org.slos.battle.abilities.rule;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;

public class FlyingRule extends AccuracyRule {

    @Override
    public Integer execute(AttackContext attackContext, GameContext gameContext) {
        MonsterBattleStats attacker = attackContext.getAttacker();
        boolean attackerIsFlying = attacker.getAbilities().stream()
                .filter(ability -> ability.getAbilityType() == AbilityType.FLYING)
                .count()
                > 0;

        if (((attackContext.getDamageType() == DamageType.ATTACK) || (attackContext.getDamageType() == DamageType.RANGED)) && (!attackerIsFlying))
        {
            return -15;
        }

        return 0;
    }
}
