package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.AccuracyRule;
import org.slos.battle.abilities.rule.FlyingRule;
import org.slos.battle.attack.AttackContext;

public class SnaredRule extends AccuracyRule {
    private FlyingRule flyingRule = new FlyingRule();

    @Override
    public Integer execute(AttackContext attackContext, GameContext gameContext) {
        int bonusFromFlyingRule = flyingRule.execute(attackContext, gameContext);
        return (bonusFromFlyingRule * (-1));
    }
}
