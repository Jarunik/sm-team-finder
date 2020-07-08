package org.slos.query;

import java.util.List;

public class BattleTeamRecord {
    private Integer rank;
    private String color;
    private String simpleHash;
    private CardRecord summoner;
    private List<CardRecord> monsters;
    private Integer totalMana;
    private Integer totalAttackDamage;
    private Integer totalRangedDamage;
    private Integer totalMagicDamage;
    private Integer totalArmor;
    private Integer totalHealth;
    private Integer totalSpeed;
    private Integer totalArmorHeal;
    private Integer totalHealthHeal;
    private Float weightedLevelAverage;

    public BattleTeamRecord() {}
    public BattleTeamRecord(Integer rank, String color, String simpleHash, CardRecord summoner, List<CardRecord> monsters, Integer totalMana, Integer totalAttackDamage, Integer totalRangedDamage, Integer totalMagicDamage, Integer totalArmor, Integer totalHealth, Integer totalSpeed, Integer totalArmorHeal, Integer totalHealthHeal, Float weightedLevelAverage) {
        this.rank = rank;
        this.color = color;
        this.simpleHash = simpleHash;
        this.summoner = summoner;
        this.monsters = monsters;
        this.totalMana = totalMana;
        this.totalAttackDamage = totalAttackDamage;
        this.totalRangedDamage = totalRangedDamage;
        this.totalMagicDamage = totalMagicDamage;
        this.totalArmor = totalArmor;
        this.totalHealth = totalHealth;
        this.totalSpeed = totalSpeed;
        this.totalArmorHeal = totalArmorHeal;
        this.totalHealthHeal = totalHealthHeal;
        this.weightedLevelAverage = weightedLevelAverage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSimpleHash(String simpleHash) {
        this.simpleHash = simpleHash;
    }

    public String getSimpleHash() {
        StringBuffer simpleHash = new StringBuffer();

        simpleHash.append(summoner.getMonsterId()+"-");

        int monsterCount = 0;
        for (CardRecord cardRecord : getMonsters()) {
            simpleHash.append(cardRecord.getMonsterId());
            if (monsterCount < getMonsters().size() - 1) {
                simpleHash.append("-");
            }
            monsterCount++;
        }

        return simpleHash.toString();
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public CardRecord getSummoner() {
        return summoner;
    }

    public void setSummoner(CardRecord summoner) {
        this.summoner = summoner;
    }

    public List<CardRecord> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<CardRecord> monsters) {
        this.monsters = monsters;
    }

    public Integer getTotalMana() {
        return totalMana;
    }

    public void setTotalMana(Integer totalMana) {
        this.totalMana = totalMana;
    }

    public Integer getTotalAttackDamage() {
        return totalAttackDamage;
    }

    public void setTotalAttackDamage(Integer totalAttackDamage) {
        this.totalAttackDamage = totalAttackDamage;
    }

    public Integer getTotalRangedDamage() {
        return totalRangedDamage;
    }

    public void setTotalRangedDamage(Integer totalRangedDamage) {
        this.totalRangedDamage = totalRangedDamage;
    }

    public Integer getTotalMagicDamage() {
        return totalMagicDamage;
    }

    public void setTotalMagicDamage(Integer totalMagicDamage) {
        this.totalMagicDamage = totalMagicDamage;
    }

    public Integer getTotalArmor() {
        return totalArmor;
    }

    public void setTotalArmor(Integer totalArmor) {
        this.totalArmor = totalArmor;
    }

    public Integer getTotalHealth() {
        return totalHealth;
    }

    public void setTotalHealth(Integer totalHealth) {
        this.totalHealth = totalHealth;
    }

    public Integer getTotalSpeed() {
        return totalSpeed;
    }

    public void setTotalSpeed(Integer totalSpeed) {
        this.totalSpeed = totalSpeed;
    }

    public Integer getTotalArmorHeal() {
        return totalArmorHeal;
    }

    public void setTotalArmorHeal(Integer totalArmorHeal) {
        this.totalArmorHeal = totalArmorHeal;
    }

    public Integer getTotalHealthHeal() {
        return totalHealthHeal;
    }

    public void setTotalHealthHeal(Integer totalHealthHeal) {
        this.totalHealthHeal = totalHealthHeal;
    }

    public Float getWeightedLevelAverage() {
        return weightedLevelAverage;
    }

    public void setWeightedLevelAverage(Float weightedLevelAverage) {
        this.weightedLevelAverage = weightedLevelAverage;
    }

    @Override
    public String toString() {
        return "BattleTeamRecord{" +
                "rank=" + rank +
                ", summoner=" + summoner +
                ", monsters=" + monsters +
                ", totalMana=" + totalMana +
                ", totalAttackDamage=" + totalAttackDamage +
                ", totalRangedDamage=" + totalRangedDamage +
                ", totalMagicDamage=" + totalMagicDamage +
                ", totalArmor=" + totalArmor +
                ", totalHealth=" + totalHealth +
                ", totalSpeed=" + totalSpeed +
                ", totalArmorHeal=" + totalArmorHeal +
                ", totalHealthHeal=" + totalHealthHeal +
                ", weightedLevelAverage=" + weightedLevelAverage +
                '}';
    }
}
