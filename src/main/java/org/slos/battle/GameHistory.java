package org.slos.battle;

import org.slos.battle.phase.GamePhase;

import java.util.ArrayList;
import java.util.List;

public class GameHistory {
    private List<GamePhase> gamePhases;

    public GameHistory() {
        this.gamePhases = new ArrayList<>();
    }

    public List<GamePhase> getGamePhases() {
        return gamePhases;
    }

    public void addEvent(GamePhase gamePhase) {
        gamePhases.add(gamePhase);
    }

    @Override
    public String toString() {
        return "GameHistory{" +
                "gamePhases=" + gamePhases +
                '}';
    }
}
