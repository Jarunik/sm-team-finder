package org.slos.battle.monster;

import org.slos.splinterlands.domain.monster.DamageType;

import java.util.Map;

public class BattleAttributes {
    private Map<DamageType, BattleAttribute> damageValues;
    private BattleAttribute armor;
    private BattleAttribute health;
    private BattleAttribute speed;
    private BattleAttribute baseHitChance = new BattleAttribute(100);

    public BattleAttributes(Map<DamageType, BattleAttribute> damageValues, BattleAttribute armor, BattleAttribute health, BattleAttribute speed, BattleAttribute baseHitChance) {
        this.damageValues = damageValues;
        this.armor = armor;
        this.health = health;
        this.speed = speed;
        this.baseHitChance = baseHitChance;
    }

    public Map<DamageType, BattleAttribute> getDamageValues() {
        return damageValues;
    }

    public void setDamageValues(Map<DamageType, BattleAttribute> damageValues) {
        this.damageValues = damageValues;
    }

    public BattleAttribute getArmor() {
        return armor;
    }

    public void setArmor(BattleAttribute armor) {
        this.armor = armor;
    }

    public BattleAttribute getHealth() {
        return health;
    }

    public void setHealth(BattleAttribute health) {
        this.health = health;
    }

    public BattleAttribute getSpeed() {
        return speed;
    }

    public void setSpeed(BattleAttribute speed) {
        this.speed = speed;
    }

    public BattleAttribute getBaseHitChance() {
        return baseHitChance;
    }

    public void setBaseHitChance(BattleAttribute baseHitChance) {
        this.baseHitChance = baseHitChance;
    }

    @Override
    public String toString() {
        return "BattleAttributes{" +
                "damageValue=" + damageValues +
                ", armor=" + armor +
                ", health=" + health +
                ", speed=" + speed +
                ", baseHitChance=" + baseHitChance +
                '}';
    }
}
