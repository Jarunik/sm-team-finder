package org.slos.battle.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slos.battle.monster.MonsterBattleStats;

public class BoardPlacement {
    protected Integer location;
    protected MonsterBattleStats monsterBattleStats;
    @JsonIgnore
    protected BoardSection boardSection;
    private int id;

    public BoardPlacement(int id, BoardSection boardSection, Integer location) {
        this.id = id;
        this.boardSection = boardSection;
        this.location = location;
    }

    public void setMonsterBattleStats(MonsterBattleStats monsterBattleStats) {
        this.monsterBattleStats = monsterBattleStats;
        if (monsterBattleStats != null) {
            monsterBattleStats.setPlacedIn(this);
        }
    }

    public MonsterBattleStats removePlacement() {
        MonsterBattleStats removingPlacement = monsterBattleStats;
        if (monsterBattleStats != null) {
            BoardPlacement phantomPlacementForDeadGuy = new BoardPlacement(-1, boardSection, location);
            monsterBattleStats.setPlacedIn(phantomPlacementForDeadGuy);
        }
        this.monsterBattleStats = null;

        return removingPlacement;
    }

    public MonsterBattleStats peekPlacement() {
        return monsterBattleStats;
    }

    public boolean isEmpty() {
        return monsterBattleStats == null;
    }

    public Integer getLocation() {
        return location;
    }

    public int getId() {
        return id;
    }

    public BoardSection getBoardSection() {
        return boardSection;
    }

    @Override
    public String toString() {
        return "BoardPlacement{" +
                "location=" + location +
                ((monsterBattleStats != null) ? ", monsterBattleStats=" + monsterBattleStats.getId() : "" )+
                '}';
    }
}