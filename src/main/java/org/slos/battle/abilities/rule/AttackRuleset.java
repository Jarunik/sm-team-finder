package org.slos.battle.abilities.rule;

import org.slos.battle.abilities.rule.attack.DamageRule;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.abilities.rule.hit.OnHitRule;
import org.slos.battle.abilities.rule.hit.OnKillingHitRule;

import java.util.ArrayList;
import java.util.List;

public class AttackRuleset {
    private List<DamageRule> damageRules;
    private List<OnAttackRule> onAttackRules;
    private List<OnHitRule> onHitRules;
    private List<OnKillingHitRule> onKillingHitRules;
    private List<AfterAttackRule> afterAttackRules;

    public static final AttackRuleset EMPTY = new AttackRuleset(new ArrayList<>(), new ArrayList<>(),  new ArrayList<>(),  new ArrayList<>(),  new ArrayList<>());

    public AttackRuleset(List<DamageRule> damageRules, List<OnAttackRule> onAttackRules, List<OnHitRule> onHitRules, List<OnKillingHitRule> onKillingHitRules, List<AfterAttackRule> afterAttackRules) {
        this.damageRules = damageRules;
        this.onAttackRules = onAttackRules;
        this.onHitRules = onHitRules;
        this.onKillingHitRules = onKillingHitRules;
        this.afterAttackRules = afterAttackRules;
    }

    public List<DamageRule> getDamageRules() {
        return damageRules;
    }

    public List<OnAttackRule> getOnAttackRules() {
        return onAttackRules;
    }

    public List<OnHitRule> getOnHitRules() {
        return onHitRules;
    }

    public List<OnKillingHitRule> getOnKillingHitRules() {
        return onKillingHitRules;
    }

    public List<AfterAttackRule> getAfterAttackRules() {
        return afterAttackRules;
    }

    @Override
    public String toString() {
        return "AttackRuleset{" +
                "damageRules=" + damageRules +
                ", attackRules=" + onAttackRules +
                ", onHitRules=" + onHitRules +
                ", onKillingHitRules=" + onKillingHitRules +
                ", afterAttackRules=" + afterAttackRules +
                '}';
    }
}
