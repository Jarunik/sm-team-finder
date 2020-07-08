package org.slos.splinterlands.domain.monster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonsterDetails {
    private Integer id;
    private String name;
    private ColorType color;
    private MonsterType type;
    private String subType;
    private Integer rarity;
    private Integer dropRate;
    private MonsterStats monsterStats;
    private Boolean isStarter;
    private String editions;
    private Integer createdBlockNum;
    private String lastUpdateTx;
    private Integer totalPrinted;

    @JsonCreator
    public MonsterDetails(@JsonProperty("id") Integer id, @JsonProperty("name") String name, @JsonProperty("color") ColorType color, @JsonProperty("type") MonsterType type, @JsonProperty("sub_type") String subType, @JsonProperty("rarity") Integer rarity, @JsonProperty("drop_rate") Integer dropRate, @JsonProperty("stats") MonsterStats monsterStats, @JsonProperty("is_starter") Boolean isStarter, @JsonProperty("editions") String editions, @JsonProperty("created_block_num") Integer createdBlockNum, @JsonProperty("last_update_tx") String lastUpdateTx, @JsonProperty("total_printed") Integer totalPrinted) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.type = type;
        this.subType = subType;
        this.rarity = rarity;
        this.dropRate = dropRate;
        this.monsterStats = monsterStats;
        this.isStarter = isStarter;
        this.editions = editions;
        this.createdBlockNum = createdBlockNum;
        this.lastUpdateTx = lastUpdateTx;
        this.totalPrinted = totalPrinted;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ColorType getColor() {
        return color;
    }

    public MonsterType getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public Integer getRarity() {
        return rarity;
    }

    public Integer getDropRate() {
        return dropRate;
    }

    public MonsterStats getMonsterStats() {
        return monsterStats;
    }

    public Boolean getStarter() {
        return isStarter;
    }

    public String getEditions() {
        return editions;
    }

    public Integer getCreatedBlockNum() {
        return createdBlockNum;
    }

    public String getLastUpdateTx() {
        return lastUpdateTx;
    }

    public Integer getTotalPrinted() {
        return totalPrinted;
    }

    @Override
    public String toString() {
        return "MonsterDetails{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", type=" + type +
                ", subType='" + subType + '\'' +
                ", rarity=" + rarity +
                ", dropRate=" + dropRate +
                ", monsterStats=" + monsterStats +
                ", isStarter=" + isStarter +
                ", editions='" + editions + '\'' +
                ", createdBlockNum=" + createdBlockNum +
                ", lastUpdateTx='" + lastUpdateTx + '\'' +
                ", totalPrinted=" + totalPrinted +
                '}';
    }
}
