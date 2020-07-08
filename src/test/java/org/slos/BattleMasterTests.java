package org.slos;

import org.slos.battle.BattleMaster;
import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityFactory;
import org.slos.battle.board.Board;
import org.slos.splinterlands.domain.GameRuleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class BattleMasterTests implements TestHelper {
    private BattleMaster DEFAULT_BATTLE_MASTER = new BattleMaster();
    private AbilityFactory DEFAULT_ABILITY_FACTORY = new AbilityFactory();
    private Board DEFAULT_BOARD = null;
    private GameContext DEFAULT_GAME_CONTEXT = null;

    @BeforeEach
    public void setup() {
        DEFAULT_BOARD = getDefaultBoard();
        Set<GameRuleType> gameRules = new HashSet<>();
        DEFAULT_GAME_CONTEXT = new GameContext(DEFAULT_BOARD, gameRules);
    }

    @Test
    public void itShouldRun() {
        System.out.println("Game Context: " + DEFAULT_GAME_CONTEXT.toJson());

        DEFAULT_BATTLE_MASTER.runGame(DEFAULT_GAME_CONTEXT);
    }
}
