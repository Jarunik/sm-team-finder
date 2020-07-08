package org.slos;

import org.slos.battle.board.Board;
import org.slos.battle.board.BoardSection;
import org.slos.battle.board.Deck;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class BoardTests {
    private Integer boardSize = 7;
    private Board board;
    private Deck topDeck;
    private Deck bottomDeck;

    @BeforeEach
    public void setBoard() {
        board = new Board(boardSize);

        //Integer mana, MonsterType type, DamageType attackType, Integer attackValue, Integer armor, Integer health, Integer speed, List<AbilityType> abilities

        MonsterBattleStats summoner = new MonsterBattleStats(1, 3, MonsterType.SUMMONER, DamageType.MAGIC, 0, 2, 2, 3, null);
        List<MonsterBattleStats> monsters = new ArrayList<>();

        for (int x = 0; x < boardSize; x++) {
            monsters.add(new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 0, 2, x, 3, null));
        }

        topDeck = new Deck(summoner, monsters);
        bottomDeck = new Deck(summoner, monsters);
    }

    @Test
    public void itShouldPopulateTheBoardWithDecks() {
        board.populateBoard(topDeck, bottomDeck);

        assertNotNull(board.getBoardTop());
        assertNotNull(board.getBoardBottom());
        validateDeckPlacement(topDeck, board.getBoardTop());
        validateDeckPlacement(bottomDeck, board.getBoardBottom());
    }

    @Test
    public void itShouldShiftBoard() {
        board.populateBoard(topDeck, bottomDeck);
        BoardSection boardTop = board.getBoardTop();
        boardTop.removeFromLocation(3);

        board.shitfLeft(board.getBoardTop());

        assertSame(topDeck.getMonsters().get(0), boardTop.peekFromLocation(1));
        assertSame(topDeck.getMonsters().get(1), boardTop.peekFromLocation(2));
        assertSame(topDeck.getMonsters().get(3), boardTop.peekFromLocation(3));
        assertSame(topDeck.getMonsters().get(4), boardTop.peekFromLocation(4));
        assertSame(topDeck.getMonsters().get(5), boardTop.peekFromLocation(5));
        assertTrue(boardTop.isEmpty(6));
    }

    @Test
    public void itShouldShiftBoard2() {
        board.populateBoard(topDeck, bottomDeck);
        BoardSection boardTop = board.getBoardTop();
        boardTop.removeFromLocation(3);
        boardTop.removeFromLocation(4);

        board.shitfLeft(board.getBoardTop());

        assertSame(topDeck.getMonsters().get(0), boardTop.peekFromLocation(1));
        assertSame(topDeck.getMonsters().get(1), boardTop.peekFromLocation(2));
        assertSame(topDeck.getMonsters().get(4), boardTop.peekFromLocation(3));
        assertSame(topDeck.getMonsters().get(5), boardTop.peekFromLocation(4));
        assertTrue(boardTop.isEmpty(5));
        assertTrue(boardTop.isEmpty(6));
    }

    private void validateDeckPlacement(Deck topDeck, BoardSection boardTop) {
        for (int x = 0; x < boardSize - 1; x++) {
            assertSame(topDeck.getMonsters().get(x), boardTop.peekFromLocation(x + 1));
        }
    }
}
