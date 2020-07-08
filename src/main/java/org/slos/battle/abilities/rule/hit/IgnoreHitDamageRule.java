package org.slos.battle.abilities.rule.hit;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.attack.FlatDamageRule;
import org.slos.battle.attack.AttackContext;
import org.slos.splinterlands.domain.monster.DamageType;

public class IgnoreHitDamageRule extends FlatDamageRule {
    private Integer ignoreCount;
    private Integer uses = 0;

    public IgnoreHitDamageRule(Integer ignoreCount) {
        super(0, DamageType.ANY);
        this.ignoreCount = ignoreCount;
    }

    @Override
    public Integer figureDamage(AttackContext attackContext, GameContext gameContext, Integer currentCalculatedDamage) {
        if (uses < ignoreCount) {
            uses++;
            return 0;
        }

        return attackContext.getDamageValue();
    }
}

