package org.slos.battle.abilities.rule.hit;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.attack.AttackContext;

public class ShatterOnHitRule extends OnAttackRule {

    @Override
    public Void execute(AttackContext attackContext, GameContext gameContext) {
        Integer armorValue = attackContext.getTarget().getArmor().getValue();
        attackContext.getTarget().getArmor().removeAll();
        armorValue = attackContext.getTarget().getArmor().getValue();
        gameContext.log("Shattered on %1$s", attackContext.getTarget().getId());

        return null;
    }
}
