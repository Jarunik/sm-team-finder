package org.slos;

import org.slos.battle.GameContext;
import org.junit.jupiter.api.Test;

public class GameContextTests implements TestHelper {

    @Test
    void itShouldRandomizeSameSpeedOnTurnSelection() {
        GameContext gameContext = new GameContext(getDefaultBoard(), null);
    }
}
