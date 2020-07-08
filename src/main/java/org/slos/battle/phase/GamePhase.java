package org.slos.battle.phase;

import org.slos.battle.GameContext;

public interface GamePhase {
    void execute(GameContext gameContext);
}
