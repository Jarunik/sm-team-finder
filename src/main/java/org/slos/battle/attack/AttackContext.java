package org.slos.battle.attack;

import org.slos.battle.abilities.rule.AttackRuleset;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.util.ToJson;

public class AttackContext implements ToJson {
    private MonsterBattleStats attacker;
    private MonsterBattleStats target;
    private DamageType damageType;
    private Integer damageValue;
    private AttackRuleset attackRuleset;
    private boolean isInitiatingAttack;

    public AttackContext(MonsterBattleStats attacker, MonsterBattleStats target, DamageType damageType, Integer damageValue, AttackRuleset attackRuleset) {
        this(attacker, target, damageType, damageValue, attackRuleset, true);
    }

    public AttackContext(MonsterBattleStats attacker, MonsterBattleStats target, DamageType damageType, Integer damageValue, AttackRuleset attackRuleset, boolean isInitiatingAttack) {
        this.attacker = attacker;
        this.target = target;
        this.damageType = damageType;
        this.damageValue = damageValue;
        this.attackRuleset = attackRuleset;
        this.isInitiatingAttack = isInitiatingAttack;
    }

    public MonsterBattleStats getAttacker() {
        return attacker;
    }

    public MonsterBattleStats getTarget() {
        return target;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public Integer getDamageValue() {
        return damageValue;
    }

    public AttackRuleset getAttackRuleset() {
        return attackRuleset;
    }

    public boolean isInitiatingAttack() {
        return isInitiatingAttack;
    }

    @Override
    public String toString() {
        return "AttackContext{" +
                "attacker=" + attacker +
                ", target=" + target +
                ", damageType=" + damageType +
                ", damageValue=" + damageValue +
                ", attackRuleset=" + attackRuleset +
                ", isInitiatingAttack=" + isInitiatingAttack +
                '}';
    }
}
