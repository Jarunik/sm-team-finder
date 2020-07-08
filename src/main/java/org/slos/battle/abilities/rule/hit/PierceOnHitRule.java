package org.slos.battle.abilities.rule.hit;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.attack.AttackContext;

public class PierceOnHitRule extends OnAttackRule {

    @Override
    public Void execute(AttackContext attackContext, GameContext gameContext) {
        Integer armorValue = attackContext.getTarget().getArmor().getValue();
        if (armorValue < 0) {
            attackContext.getTarget().getHealth().addToValue(armorValue);
        }

        return null;
    }
}
