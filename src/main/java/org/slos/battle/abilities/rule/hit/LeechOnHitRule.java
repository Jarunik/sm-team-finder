package org.slos.battle.abilities.rule.hit;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.attack.AttackContext;

public class LeechOnHitRule extends OnAttackRule {

    @Override
    public Void execute(AttackContext attackContext, GameContext gameContext) {
        Integer amountToHeal = attackContext.getDamageValue();
        attackContext.getAttacker().getHealth().addToValue(amountToHeal);

        return null;
    }
}
