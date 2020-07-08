package org.slos;

import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.slos.util.ToJson;

public class SimpleCard implements ToJson {
    private Integer id;
    private String collectionId;
    private Integer level;
    private String name;
    private ColorType colorType;
    private boolean isGoldFoil;
    private Integer rarity;
    private MonsterType monsterType;

    public SimpleCard(SimpleCard simpleCard) {
        this(simpleCard.id, simpleCard.collectionId, simpleCard.level, simpleCard.name, simpleCard.colorType, simpleCard.isGoldFoil, simpleCard.rarity, simpleCard.monsterType);
    }

    public SimpleCard(Integer id, String collectionId, Integer level, String name, ColorType colorType, boolean isGoldFoil, Integer rarity, MonsterType monsterType) {
        this.id = id;
        this.collectionId = collectionId;
        this.level = level;
        this.name = name;
        this.colorType = colorType;
        this.isGoldFoil = isGoldFoil;
        this.rarity = rarity;
        this.monsterType = monsterType;
    }

    public Integer getId() {
        return id;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public Integer getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public ColorType getColorType() {
        return colorType;
    }

    public boolean isGoldFoil() {
        return isGoldFoil;
    }

    public Integer getRarity() {
        return rarity;
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    @Override
    public String toString() {
        return toJson();
    }
}
