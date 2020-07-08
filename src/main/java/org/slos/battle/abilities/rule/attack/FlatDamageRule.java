package org.slos.battle.abilities.rule.attack;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.AttackRuleType;
import org.slos.battle.attack.AttackContext;
import org.slos.splinterlands.domain.monster.DamageType;

import java.util.Arrays;
import java.util.List;

public class FlatDamageRule extends DamageRule {

    protected final Integer modificationAmount;
    protected final List<DamageType> damageTypes;

    public FlatDamageRule(Integer modificationAmount, DamageType... damageType) {
        super(AttackRuleType.TARGET_DAMAGE);
        this.modificationAmount = modificationAmount;
        this.damageTypes = Arrays.asList(damageType);
    }

    @Override
    public Integer execute(AttackContext attackContext, GameContext gameContext) {return 0;}

    @Override
    public Integer figureDamage(AttackContext attackContext, GameContext gameContext, Integer currentCalculatedDamage) {
        Integer attackValue = attackContext.getDamageValue();

        if ((damageTypes.contains(attackContext.getDamageType())) || (damageTypes.contains(DamageType.ANY))) {
            Integer newAttackValue = attackValue + modificationAmount;

            return zeroCheck(newAttackValue);
        }

        return attackValue;
    }

    protected Integer zeroCheck(Integer value) {
        if (value < 0) {
            return 0;
        }
        return value;
    }
}
