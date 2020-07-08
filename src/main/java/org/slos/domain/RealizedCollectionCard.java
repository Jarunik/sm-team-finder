package org.slos.domain;

import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.collection.CollectionCard;
import org.slos.splinterlands.domain.monster.MonsterDetails;

public class RealizedCollectionCard {
    private CollectionCard collectionCard;
    private MonsterDetails monsterDetails;
    private MonsterBattleStats monsterBattleStats;

    public RealizedCollectionCard(CollectionCard collectionCard, MonsterDetails monsterDetails, MonsterBattleStats monsterBattleStats) {
        this.collectionCard = collectionCard;
        this.monsterDetails = monsterDetails;
        this.monsterBattleStats = monsterBattleStats;
    }

    public CollectionCard getCollectionCard() {
        return collectionCard;
    }

    public void setCollectionCard(CollectionCard collectionCard) {
        this.collectionCard = collectionCard;
    }

    public MonsterDetails getMonsterDetails() {
        return monsterDetails;
    }

    public void setMonsterDetails(MonsterDetails monsterDetails) {
        this.monsterDetails = monsterDetails;
    }

    public MonsterBattleStats getMonsterBattleStats() {
        return monsterBattleStats;
    }

    public void setMonsterBattleStats(MonsterBattleStats monsterBattleStats) {
        this.monsterBattleStats = monsterBattleStats;
    }
}
