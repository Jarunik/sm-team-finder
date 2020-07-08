package org.slos.query;

import java.util.List;

public class CardRecord {
    private Integer monsterId;
    private String monsterColor;
    private Integer monsterLevel;
    private Integer manaCost;
    private Integer attackDamage;
    private Integer rangedDamage;
    private Integer magicDamage;
    private Integer armor;
    private Integer health;
    private Integer speed;
    private List<String> abilities;

    public CardRecord(){}
    public CardRecord(Integer monsterId, String monsterColor, Integer monsterLevel, Integer manaCost, Integer attackDamage, Integer rangedDamage, Integer magicDamage, Integer armor, Integer health, Integer speed, List<String> abilities) {
        this.monsterId = monsterId;
        this.monsterColor = monsterColor;
        this.monsterLevel = monsterLevel;
        this.manaCost = manaCost;
        this.attackDamage = attackDamage;
        this.rangedDamage = rangedDamage;
        this.magicDamage = magicDamage;
        this.armor = armor;
        this.health = health;
        this.speed = speed;
        this.abilities = abilities;
    }

    public Integer getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Integer monsterId) {
        this.monsterId = monsterId;
    }

    public String getMonsterColor() {
        return monsterColor;
    }

    public void setMonsterColor(String monsterColor) {
        this.monsterColor = monsterColor;
    }

    public Integer getMonsterLevel() {
        return monsterLevel;
    }

    public void setMonsterLevel(Integer monsterLevel) {
        this.monsterLevel = monsterLevel;
    }

    public Integer getManaCost() {
        return manaCost;
    }

    public void setManaCost(Integer manaCost) {
        this.manaCost = manaCost;
    }

    public Integer getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(Integer attackDamage) {
        this.attackDamage = attackDamage;
    }

    public Integer getRangedDamage() {
        return rangedDamage;
    }

    public void setRangedDamage(Integer rangedDamage) {
        this.rangedDamage = rangedDamage;
    }

    public Integer getMagicDamage() {
        return magicDamage;
    }

    public void setMagicDamage(Integer magicDamage) {
        this.magicDamage = magicDamage;
    }

    public Integer getArmor() {
        return armor;
    }

    public void setArmor(Integer armor) {
        this.armor = armor;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    @Override
    public String toString() {
        return "CardRecord{" +
                "monsterId=" + monsterId +
                ", monsterLevel=" + monsterLevel +
                ", manaCost=" + manaCost +
                ", attackDamage=" + attackDamage +
                ", rangedDamage=" + rangedDamage +
                ", magicDamage=" + magicDamage +
                ", armor=" + armor +
                ", health=" + health +
                ", speed=" + speed +
                ", abilities=" + abilities +
                '}';
    }
}
