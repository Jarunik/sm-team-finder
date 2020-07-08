package org.slos;

import org.slos.battle.board.BoardSection;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardSectionTests {
    private static final Integer SECTION_SIZE = 7;
    private BoardSection boardSection;

    @BeforeEach
    public void setup() {
        boardSection = new BoardSection(1, SECTION_SIZE);
    }

    @Test
    public void itShouldPopulateSection() {
        MonsterBattleStats monsterBattleStats = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 3, 2, 1, 3, null);
        boardSection.placeInSection(0, monsterBattleStats);

        assertSame(monsterBattleStats, boardSection.peekFromLocation(0));
    }

    @Test
    public void itShouldNotBeEmptyIfSet() {
        MonsterBattleStats monsterBattleStats = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 3, 2, 1, 3, null);
        boardSection.placeInSection(0, monsterBattleStats);

        assertFalse(boardSection.isEmpty(0));
    }

    @Test
    public void itShouldReplaceAndReturnExistingPlacement() {
        MonsterBattleStats monsterBattleStats1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 3, 2, 1, 3, null);
        MonsterBattleStats monsterBattleStats2 = new MonsterBattleStats(1, 2, MonsterType.MONSTER, DamageType.ATTACK, 2, 2, 2, 2, null);
        boardSection.placeInSection(0, monsterBattleStats1);
        MonsterBattleStats replacedMonsterBattleStats = boardSection.placeInSection(0, monsterBattleStats2);

        assertFalse(boardSection.isEmpty(0));
        assertEquals(monsterBattleStats2, boardSection.peekFromLocation(0));
        assertEquals(monsterBattleStats1, replacedMonsterBattleStats);
    }

    @Test
    public void itShouldBeEmptyIfNotSet() {
        assertTrue(boardSection.isEmpty(0));
    }

    @Test
    public void itShouldRemoveSection() {
        MonsterBattleStats monsterBattleStats = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 3, 2, 1, 3, null);
        boardSection.placeInSection(0, monsterBattleStats);

        boardSection.removeFromLocation(0);

        assertTrue(boardSection.isEmpty(0));
    }
}
