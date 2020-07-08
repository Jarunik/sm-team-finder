package org.slos.battle;

import org.slos.battle.board.Board;
import org.slos.battle.board.BoardSection;
import org.slos.battle.phase.GamePhase;

public class BattleMaster {

    public GameWinner runGame(GameContext gameContext) {
        validateGame(gameContext);

        for (GamePhase gamePhase : gameContext.getGamePhases()) {
            gamePhase.execute(gameContext);
        }
        return gameContext.getGameWinner();
    }

    private void validateGame(GameContext gameContext) {
        Board board = gameContext.getBoard();
        BoardSection boardTop = board.getBoardTop();
        BoardSection boardBottom = board.getBoardBottom();

        if ((boardTop == null || boardTop.isEmpty(0) || boardTop.isEmpty(1) ||
            (boardBottom == null || boardBottom.isEmpty(0) || boardBottom.isEmpty(1)))) {
            throw new IllegalStateException("Board top and bottom must be present and contain one summoner and monster: " + board.toSimpleString());
        }
    }
}
