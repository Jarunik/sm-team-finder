package org.slos.battle.board;

import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.MonsterType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BoardSection {
    private Integer sectionSize;
    private BoardPlacement boardSection[];
    private int id = -1;

    public BoardSection(int id, Integer sectionSize) {
        this.id = id;
        this.sectionSize = sectionSize;
        boardSection = new BoardPlacement[sectionSize];

        for (int i = 0; i < sectionSize; i++) {
            boardSection[i] = new BoardPlacement(id, this, i);
        }
    }

    public MonsterBattleStats placeInSection(Integer location, MonsterBattleStats monsterBattleStats) {
        MonsterBattleStats previousMonsterBattleStats = boardSection[location].removePlacement();

        boardSection[location].setMonsterBattleStats(monsterBattleStats);

        return previousMonsterBattleStats;
    }

    public Integer getSectionSize() {
        return sectionSize;
    }

    public MonsterBattleStats removeFromLocation(Integer location) {
        return boardSection[location].removePlacement();
    }

    public MonsterBattleStats peekFromLocation(Integer location) {
        return boardSection[location].peekPlacement();
    }

    public Boolean isEmpty(Integer location) {
        return boardSection[location].isEmpty();
    }

    public boolean isEmpty() {
        for (int i = 1; i < sectionSize; i++) {
            if (boardSection[i] != null) {
                return false;
            }
        }

        return true;
    }

    public List<MonsterBattleStats> peekMonsterBattleStats() {
        return Arrays.stream(boardSection)
                .map(boardSection -> boardSection.peekPlacement())
                .filter(monsterBattleStats -> monsterBattleStats != null)
                .filter(monsterBattleStats -> monsterBattleStats.getType() != MonsterType.SUMMONER)
                .collect(Collectors
                .toCollection(ArrayList::new));
    }

    public int getId() {
        return id;
    }

    public Boolean containsMonsterBattleStats(MonsterBattleStats monsterBattleStats) {
        for (BoardPlacement boardPlacement : boardSection) {
            if (boardPlacement.peekPlacement() == monsterBattleStats) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "BoardSection{" +
                "sectionSize=" + sectionSize +
                ", boardSection=" + Arrays.toString(boardSection) +
                '}';
    }
}