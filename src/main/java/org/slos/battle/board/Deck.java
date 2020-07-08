package org.slos.battle.board;

import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.slos.util.ToJson;

import java.util.Arrays;
import java.util.List;

public class Deck implements ToJson {
    private String id;
    private MonsterBattleStats summoner;
    private List<MonsterBattleStats> monsters;

    public Deck(String id, MonsterBattleStats summoner, List<MonsterBattleStats> monsters) {
        this.id = id;
        this.summoner = summoner;
        this.monsters = monsters;
    }

    public Deck(MonsterBattleStats summoner, MonsterBattleStats... monsters) {
        this(summoner, Arrays.asList(monsters));
    }

    public Deck(MonsterBattleStats summoner, List<MonsterBattleStats> monsters) {
        this.summoner = summoner;
        this.monsters = monsters;

        validateCards(summoner, monsters);
    }

    public MonsterBattleStats getSummoner() {
        return summoner;
    }

    public List<MonsterBattleStats> getMonsters() {
        return monsters;
    }

    public String getId() {
        return id;
    }

    private void validateCards(MonsterBattleStats summoner, List<MonsterBattleStats> monsters) {
        if (summoner.getType() != MonsterType.SUMMONER) {
            throw new IllegalStateException("Invalid Monster for Deck: " + summoner.getType());
        }

        for (MonsterBattleStats stats : monsters) {
            if (stats.getType() != MonsterType.MONSTER) {
                throw new IllegalStateException("Invalid type for Monster: " + stats.getType());
            }
        }
    }

    @Override
    public String toString() {
        return "Deck{" +
                "id='" + id + '\'' +
                ", summoner=" + summoner +
                ", monsters=" + monsters +
                '}';
    }
}
