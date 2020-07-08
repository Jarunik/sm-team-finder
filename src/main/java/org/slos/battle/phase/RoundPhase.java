package org.slos.battle.phase;

import org.slos.battle.ExccededAllowedTimeRuntimeException;
import org.slos.battle.GameContext;
import org.slos.battle.GameState;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.attack.IsStunnedAbility;
import org.slos.battle.abilities.rule.AttackRuleset;
import org.slos.battle.abilities.rule.OnRoundStartRule;
import org.slos.battle.abilities.rule.turn.OnTurnEndRule;
import org.slos.battle.abilities.rule.turn.OnTurnStartRule;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.attack.AttackService;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoundPhase implements GamePhase {
    private AttackService attackService = new AttackService();
    private static final int TURN_LIMIT = 3000;
    private int turnCount = 0;

    private final Logger logger = LoggerFactory.getLogger(RoundPhase.class);

    @Override
    public void execute(GameContext gameContext) {
        gameContext.setGameState(GameState.ATTACKING);

        while (!gameContext.gameHasWinner()) {
            executeRound(gameContext);
        }
    }

    public void executeRound(GameContext gameContext) {
        for (MonsterBattleStats monsterBattleStats : gameContext.getAllMonsters()) {
            if (!(gameContext.getGameState().equals(GameState.GAME_COMPLETED)) && (monsterBattleStats.isDead())) {
                logger.error("Dead monster on board: " + gameContext.getBoard().toSimpleString());
                gameContext.log("Dead monster on board!");
                throw new IllegalStateException("Dead monster on board before attack: " + monsterBattleStats);
            }
        }

        gameContext.newRound();

        checkAndApplyOnRoundStartAbilities(gameContext);
        checkAndApplyFatigue(gameContext);

        if (gameContext.gameHasWinner()) {
                return;
        }

        MonsterBattleStats attacker = gameContext.getNextMonsterForTurn();
        while ((attacker != null) && (gameContext.getGameState() != GameState.GAME_COMPLETED) && (!gameContext.gameHasWinner())) {
            gameContext.log("Starting turn for: %1$s - %2$s", attacker.getId(), attacker);
            turnCount++;

            if (turnCount > TURN_LIMIT) {
                gameContext.printHistory();

                throw new RuntimeException("Maximum turn count reached: " + gameContext.gameHasWinner() + " - " + gameContext.getBoard().toSimpleString());
            }

            boolean attackerIsStunned = false;

            Ability[] attackerAbilities = attacker.getAbilities().toArray(new Ability[attacker.getAbilities().size()]);
            for (Ability ability : attackerAbilities) {
                if (ability instanceof IsStunnedAbility) {
                    attackerIsStunned = true;
                    attacker.getAbilities().remove(ability);
                }
            }

            if (!attackerIsStunned) {
                executeAllAttackTypesForAttacker(gameContext, attacker);
            }

            attacker = gameContext.getNextMonsterForTurn();
        }

        checkAndApplyEarthquake(gameContext);
    }

    private void executeAllAttackTypesForAttacker(GameContext gameContext, MonsterBattleStats attacker) {
        boolean isOfDamageType = false;
        if (attacker.isOfDamageType(DamageType.MAGIC)) {
            executeTurn(attacker, DamageType.MAGIC, gameContext);
            isOfDamageType = true;
        }
        if (attacker.isOfDamageType(DamageType.RANGED)) {
            executeTurn(attacker, DamageType.RANGED, gameContext);
            isOfDamageType = true;
        }
        if (attacker.isOfDamageType(DamageType.ATTACK)) {
            executeTurn(attacker, DamageType.ATTACK, gameContext);
            isOfDamageType = true;
        }
        if (!isOfDamageType) {
            executeTurn(attacker, DamageType.NONE, gameContext);
        }
    }

    private void checkAndApplyOnRoundStartAbilities(GameContext gameContext) {

        List<MonsterBattleStats> allMonsters = new ArrayList<>();

        allMonsters.addAll(gameContext.getBoard().getBoardTop().peekMonsterBattleStats());
        allMonsters.addAll(gameContext.getBoard().getBoardBottom().peekMonsterBattleStats());

        for (MonsterBattleStats monsterBattleStats : allMonsters) {
            List<Ability> onRoundStartAbilities = monsterBattleStats.getAbilities().stream()
                    .filter(ability -> ability.containsClassification(AbilityClassification.ON_ROUND_START))
                    .collect(Collectors.toList());

            for (Ability ability : onRoundStartAbilities) {
                ((OnRoundStartRule)ability.getEffect()).executeOnRoundStart(monsterBattleStats, gameContext);
            }

            if (monsterBattleStats.isDead()) {
                gameContext.getDeathService().registerDeath(monsterBattleStats, null);
            }
        }
    }

    private void checkAndApplyFatigue(GameContext gameContext) {
        Integer roundNumber = gameContext.getRound();
        if (roundNumber > 20) {

            int fatigueDamage = roundNumber - 20;
            List<MonsterBattleStats> allMonsters = gameContext.getAllMonsters();

            for (MonsterBattleStats monsterBattleStats : allMonsters) {
                AttackContext attackContext = new AttackContext(null, monsterBattleStats, DamageType.SYSTEM, fatigueDamage, AttackRuleset.EMPTY);
                gameContext.getAttackService().getAttackDamageExecutionService().attack(attackContext, gameContext);
            }
        }
    }

    private void checkAndApplyEarthquake(GameContext gameContext) {
        if (gameContext.hasRule(GameRuleType.EARTHQUAKE)) {
            gameContext.log("Triggering Earthquake");

            for (MonsterBattleStats monsterBattleStats : gameContext.getAllMonsters()) {
                if (!monsterBattleStats.containsAbility(AbilityType.FLYING)) {
                    AttackContext attackContext = new AttackContext(null, monsterBattleStats, DamageType.ATTACK,2, null);
                    gameContext.getAttackService().getAttackDamageExecutionService().applyDefaultDamage(attackContext, gameContext);

                    checkAndTriggerMonsterDeath(gameContext, monsterBattleStats);
                }
            }
        }
    }

    private void checkAndTriggerMonsterDeath(GameContext gameContext, MonsterBattleStats monsterBattleStats) {
        if (monsterBattleStats.isDead()) {
            gameContext.getDeathService().registerDeath(monsterBattleStats, null);
        }
    }

    public void executeTurn(MonsterBattleStats attacker, DamageType damageType, GameContext gameContext) {

        if ((gameContext.getMaxProcessingTime() != null) && (gameContext.getMaxProcessingTime() < System.currentTimeMillis())) {
            throw new ExccededAllowedTimeRuntimeException();
        }

        AttackContext preAttackContext = new AttackContext(attacker, null, null, null, null);
        boolean canStartTurn = executeOnTurnStartAbilities(preAttackContext, gameContext);

        AttackContext attackContext = null;
        if (canStartTurn) {
             attackContext = attackService.attack(attacker, damageType, gameContext);
        }
        else {
            attackContext = new AttackContext(attacker, null, damageType, null, null);
        }

        executeOnTurnEndAbilities(attackContext, gameContext);
    }

    private void executeOnTurnEndAbilities(AttackContext attackContext, GameContext gameContext) {
        if ((attackContext != null) && (attackContext.getAttacker() != null)) {
            MonsterBattleStats attacker = attackContext.getAttacker();
            List<Ability> onTurnEndAbilities = attacker.getAbilities().stream()
                    .filter(ability -> ability.containsClassification(AbilityClassification.ON_TURN_END))
                    .collect(Collectors.toList());

            for (Ability ability : onTurnEndAbilities) {
                OnTurnEndRule onTurnEndRule = (OnTurnEndRule) ability.getEffect();
                onTurnEndRule.executeOnTurnEnd(attackContext, gameContext);
            }
        }
    }

    private boolean executeOnTurnStartAbilities(AttackContext attackContext, GameContext gameContext) {
        MonsterBattleStats attacker = attackContext.getAttacker();
        boolean canStartTurn = true;

        List<Ability> onTurnStartAbilities  = attacker.getAbilities().stream()
                .filter(ability -> ability.containsClassification(AbilityClassification.ON_TURN_START))
                .collect(Collectors.toList());

        for (Ability ability : onTurnStartAbilities) {
            OnTurnStartRule onTurnStartRule = (OnTurnStartRule) ability.getEffect();
            boolean canStartTurnFromAbility = onTurnStartRule.executeOnTurnStart(attacker, gameContext);
            canStartTurn = (canStartTurn && canStartTurnFromAbility);

            if (attacker.getHealth().getValue() <= 0) {
                canStartTurn = false;
            }
        }

        return canStartTurn;
    }
}
