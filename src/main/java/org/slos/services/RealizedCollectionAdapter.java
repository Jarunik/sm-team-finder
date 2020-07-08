package org.slos.services;

import org.slos.MonsterBattleStatsService;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.domain.RealizedCollection;
import org.slos.domain.RealizedCollectionCard;
import org.slos.splinterlands.collection.Collection;
import org.slos.splinterlands.collection.CollectionCard;
import org.slos.splinterlands.domain.monster.MonsterDetails;
import org.slos.splinterlands.domain.monster.MonsterDetailsList;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class RealizedCollectionAdapter {
    @Autowired MonsterBattleStatsService monsterBattleStatsService;
    @Autowired
    MonsterDetailsList monsterDetailsList;

    public RealizedCollection convertToRealizedCollection(Collection collection) {
        String collectionOwner = collection.getPlayer();
        List<RealizedCollectionCard> realizedCollectionCards = new ArrayList<>();

        for (CollectionCard collectionCard : collection.getCards()) {
            RealizedCollectionCard realizedCollectionCard = convertToRealizedCard(collectionCard);
            realizedCollectionCards.add(realizedCollectionCard);
        }

        return new RealizedCollection(collectionOwner, realizedCollectionCards);
    }

    public RealizedCollectionCard convertToRealizedCard(CollectionCard collectionCard) {

        MonsterBattleStats monsterBattleStats = monsterBattleStatsService.getMonsterBattleStats(collectionCard.getMonsterId(), collectionCard.getLevel());
        MonsterDetails monsterDetails = monsterDetailsList.getDetails(collectionCard.getMonsterId());

        return new RealizedCollectionCard(collectionCard, monsterDetails, monsterBattleStats);
    }
}
