package org.slos.battle.abilities.rule;

import org.hibernate.validator.internal.util.CollectionHelper;
import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.attack.DamageRule;
import org.slos.battle.attack.AttackContext;
import org.slos.splinterlands.domain.monster.DamageType;

import java.util.Set;

public class ReduceHalfDamageRule extends DamageRule {
    private final Set<DamageType> damageType;

    public ReduceHalfDamageRule(DamageType... damageType) {
        super(AttackRuleType.TARGET_DAMAGE);
        this.damageType = CollectionHelper.asSet(damageType);
    }

    @Override
    public Integer execute(AttackContext attackContext, GameContext gameContext) {
        return 0;
    }

    public Integer figureDamage(AttackContext attackContext, GameContext gameContext, Integer currentCalculatedDamage) {
        int returnDamage;

        if (damageType.contains(attackContext.getDamageType())) {
            if (currentCalculatedDamage == 1) {
                returnDamage = 0;
            }
            else {
                Integer damage = currentCalculatedDamage / 2;
                if (currentCalculatedDamage % 2 == 1) {
                    damage++;
                }

                returnDamage = damage;
            }
            gameContext.log("Reducing half damage to: %1$s", returnDamage);
        }
        else {
            returnDamage = currentCalculatedDamage;
        }

        return returnDamage;
    }
}
