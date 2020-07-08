package org.slos.battle.abilities.rule;

import org.slos.battle.GameContext;
import org.slos.battle.attack.AttackContext;
import org.slos.splinterlands.domain.monster.DamageType;

public class DodgeRule extends AccuracyRule {

    @Override
    public Integer execute(AttackContext attackContext, GameContext gameContext) {
        if ((attackContext.getDamageType() == DamageType.ATTACK) || (attackContext.getDamageType() == DamageType.RANGED))
        {
            return -15;
        }

        return 0;
    }
}
