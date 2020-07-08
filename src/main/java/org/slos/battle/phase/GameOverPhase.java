package org.slos.battle.phase;

import org.slos.battle.GameContext;
import org.slos.battle.GameState;

public class GameOverPhase implements GamePhase {

    @Override
    public void execute(GameContext gameContext) {
        gameContext.log("Game Over!");
        gameContext.setGameState(GameState.GAME_COMPLETED);
    }
}
