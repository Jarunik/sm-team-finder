package org.slos.battle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.rule.OnDeathRule;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.board.Board;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.List;
import java.util.stream.Collectors;

public class DeathService {
    @JsonIgnore
    private final GameContext gameContext;

    public DeathService(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public void newTurn() {
    }

    public void registerDeath(MonsterBattleStats deadMonster, AttackContext attackContext) {
        gameContext.log("Registering death for: %1$s", deadMonster);
        Board board = gameContext.getBoard();
        List<MonsterBattleStats> allMonsters = board.getBoardBottom().peekMonsterBattleStats();
        allMonsters.addAll(board.getBoardTop().peekMonsterBattleStats());

            gameContext.getBuffService().monsterDied(deadMonster, gameContext);

            executeOnDeathAbilities(deadMonster, allMonsters);

            if ((attackContext != null) && (attackContext.getTarget() != null) && (attackContext.getAttackRuleset() != null) && (attackContext.getAttackRuleset().getOnKillingHitRules() != null)) {
                gameContext.getAttackService().getAttackDamageExecutionService().executeAttackRules(attackContext.getAttackRuleset().getOnKillingHitRules(), attackContext, gameContext);
            }

            if ((deadMonster.isDead()) && (deadMonster.getPlacedIn() != null)) {
                deadMonster.getPlacedIn().removePlacement();
                board.consolidateBoard();
                gameContext.getAttackQueue().remove(deadMonster);
            }

            gameContext.resetAttackQueue(gameContext.getAttackQueue());
    }

    private void executeOnDeathAbilities(MonsterBattleStats monsterBattleStats, List<MonsterBattleStats> allMonsters) {
        for (MonsterBattleStats checkForOnDeathAbility : allMonsters) {
            List<Ability> onBattleStatChangeAbilities = checkForOnDeathAbility.getAbilities().stream()
                    .filter(ability -> ability.containsClassification(AbilityClassification.ON_DEATH))
                    .collect(Collectors.toList());

            for (Ability ability : onBattleStatChangeAbilities) {
                OnDeathRule onDeathRule = (OnDeathRule) ability.getEffect();
                onDeathRule.executeOnDeathEffect(monsterBattleStats, checkForOnDeathAbility, gameContext);
            }
        }
    }
}
