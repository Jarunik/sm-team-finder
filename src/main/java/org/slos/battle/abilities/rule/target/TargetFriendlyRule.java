package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.board.BoardSection;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.List;

public class TargetFriendlyRule implements TargetRule {
    @Override
    public List<MonsterBattleStats> selectTargets(MonsterBattleStats monsterBattleStats, List<MonsterBattleStats> selectFrom, GameContext gameContext) {
        return findBoardSectionWithMonsterBattleStats(monsterBattleStats, gameContext).peekMonsterBattleStats();
    }

    private BoardSection findBoardSectionWithMonsterBattleStats(MonsterBattleStats monsterBattleStats, GameContext gameContext) {
        if (gameContext.getBoard().getBoardTop().containsMonsterBattleStats(monsterBattleStats)) {
            return gameContext.getBoard().getBoardTop();
        }
        else {
            return gameContext.getBoard().getBoardBottom();
        }
    }
}
