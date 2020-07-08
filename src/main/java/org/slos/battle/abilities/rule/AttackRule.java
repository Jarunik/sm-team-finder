package org.slos.battle.abilities.rule;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.attack.AttackContext;

public abstract class AttackRule<T> implements AbilityEffect {
    private AttackRuleType attackRuleType;

    public AttackRule(AttackRuleType attackRuleType) {
        this.attackRuleType = attackRuleType;
    }

    public AttackRuleType getAttackRuleType() {
        return attackRuleType;
    }

    public abstract T execute(AttackContext attackContext, GameContext gameContext);

    @Override
    public String toString() {
        return "AttackRule{" +
                "attackRuleType=" + attackRuleType +
                '}';
    }
}
