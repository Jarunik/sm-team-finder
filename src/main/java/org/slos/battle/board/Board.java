package org.slos.battle.board;

import org.slos.battle.monster.MonsterBattleStats;
import org.slos.util.ToJson;

public class Board implements ToJson {
    private final Integer boardPositionCount;
    private BoardSection boardTop;
    private BoardSection boardBottom;

    public Board(Integer boardPositionCount) {
        this.boardPositionCount = boardPositionCount;
        boardTop = new BoardSection(1, boardPositionCount);
        boardBottom = new BoardSection(2, boardPositionCount);
    }

    public void populateBoard(Deck topDeck, Deck bottomDeck) {
        populateBoardSection(boardTop, topDeck);
        populateBoardSection(boardBottom, bottomDeck);
    }

    public void consolidateBoard() {
        shitfLeft(boardTop);
        shitfLeft(boardBottom);
    }

    public BoardSection getBoardTop() {
        return boardTop;
    }

    public BoardSection getBoardBottom() {
        return boardBottom;
    }

    public void shitfLeft(BoardSection boardSection) {
        for (int i = 0; i < boardSection.getSectionSize(); i++) {
            if (!boardSection.isEmpty(i)) {
            }
            else {
                for (int x = i+1; x < boardSection.getSectionSize(); x++) {
                    if (!boardSection.isEmpty(x)) {
                        boardSection.placeInSection(i, boardSection.removeFromLocation(x));
                        break;
                    }
                }
            }
        }
    }

    private void populateBoardSection(BoardSection boardPlacement, Deck deck) {
        boardPlacement.placeInSection(0, deck.getSummoner());

        for (int x = 1; x < boardPositionCount && x < deck.getMonsters().size()+1; x++) {
            boardPlacement.placeInSection(x, deck.getMonsters().get(x-1));
        }
    }

    public Integer getBoardPositionCount() {
        return boardPositionCount;
    }

    @Override
    public String toString() {
        return "Board{" +
                "boardTop=" + boardTop +
                ", boardBottom=" + boardBottom +
                '}';
    }

    public String toSimpleString() {
        StringBuilder stringBuilder = new StringBuilder();
        getBoardSide(stringBuilder, boardTop);
        stringBuilder.append( " vs. ");
        getBoardSide(stringBuilder, boardBottom);

        return stringBuilder.toString();
    }

    private void getBoardSide(StringBuilder stringBuilder, BoardSection boardTop) {
        for (MonsterBattleStats monsterBattleStats : boardTop.peekMonsterBattleStats()) {
            stringBuilder.append("[" + monsterBattleStats.getId() + "/");
//            for (DamageType damageType : monsterBattleStats.getDamageValues().keySet()) {
//                stringBuilder.append(monsterBattleStats.getDamageValues().get(damageType) + "/");
//            }
            stringBuilder.append(monsterBattleStats.getArmor().getValue() + "|" + monsterBattleStats.getHealth().getValue() + "]");
        }
    }
}
