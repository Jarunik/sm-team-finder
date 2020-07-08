package org.slos.battle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slos.battle.abilities.AbilityFactory;
import org.slos.battle.abilities.buff.BuffService;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.attack.AttackService;
import org.slos.battle.board.Board;
import org.slos.battle.decision.ChoiceGate;
import org.slos.battle.decision.ChoiceGateFactory;
import org.slos.battle.decision.MasterChoiceContext;
import org.slos.battle.decision.strategy.ChoiceStrategyMode;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.battle.phase.GameInitializationPhase;
import org.slos.battle.phase.GameOverPhase;
import org.slos.battle.phase.GamePhase;
import org.slos.battle.phase.RoundPhase;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.slos.util.ToJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameContext implements ToJson {
    private BuffService buffService = new BuffService();
    private GameState gameState = GameState.VIRGIN;
    private Board board;
    private Integer round = 0;
    private List<GamePhase> queuedGamePhases = new ArrayList<>();
    private List<MonsterBattleStats> attackQueue;
    private AttackService attackService = new AttackService();
    private DeathService deathService;
    private Set<GameRuleType> gameRules;
    private AbilityFactory abilityFactory = new AbilityFactory();
    private ChoiceStrategyMode choiceStrategyMode;
    private MasterChoiceContext masterChoiceContext = new MasterChoiceContext();
    private List<String> attackHistory = new ArrayList<>();
    private boolean trackHistory = false;
    private Long maxProcessingTime = null;

    private final Logger logger = LoggerFactory.getLogger(GameContext.class);

    public GameContext(Board board, Set<GameRuleType> gameRules) {
        queuedGamePhases.add(new GameInitializationPhase());
        queuedGamePhases.add(new RoundPhase());
        queuedGamePhases.add(new GameOverPhase());

        this.board = board;
        this.gameRules = gameRules == null ? new HashSet<>() : gameRules;
        deathService = new DeathService(this);
    }

    public void log(String log, Object... objects) {
        if (trackHistory) {
            attackHistory.add(board.toSimpleString() + " --> " + String.format(log, objects));
        }
    }

    public void setMaxProcessingTime(long maxProcessingTime) {
        this.maxProcessingTime = maxProcessingTime;
    }

    public Long getMaxProcessingTime() {
        return maxProcessingTime;
    }

    public void registerAttack(AttackContext attackContext) {
        if (trackHistory) {
            log("%1$s [%2$s-%3$s] --> %4$s",attackContext.getAttacker().getId(), attackContext.getDamageType(), attackContext.getDamageValue(), attackContext.getTarget().getId());
        }
    }

    public void setTrackHistory(boolean trackHistory) {
        this.trackHistory = trackHistory;
    }

    public List<String> getAttackHistory() {
        return attackHistory;
    }

    public void printHistory() {
        for (String log : attackHistory) {
            logger.info(log);
        }
    }

    public void setMasterChoiceContext(MasterChoiceContext masterChoiceContext) {
        this.masterChoiceContext = masterChoiceContext;
    }

    public MasterChoiceContext getMasterChoiceContext() {
        return masterChoiceContext;
    }

    public boolean hasRule(GameRuleType gameRule) {
        return gameRules.contains(gameRule);
    }

    public Set<GameRuleType> getGameRules() {
        return gameRules;
    }

    public void resetAttackQueue() {
        resetAttackQueue(null);
    }

    public void resetAttackQueue(List<MonsterBattleStats> existingQueue) {


        log("Resetting Attack Queue with: %1$s", existingQueue);
        attackQueue = getAttackQueue(existingQueue);

        if ((existingQueue != null) && (existingQueue.size() > 12)) {
            throw new RuntimeException("Attack queue size is invalid: " + existingQueue.size() + "  -  " + existingQueue);
        }

//        for (MonsterBattleStats attacker : attackQueue) {
//            if (attacker.getHealth().getValue() <= 0) {
//                throw new IllegalStateException("Dead monsters can't attack.");
//            }
//        }
    }

    public List<MonsterBattleStats> getAttackQueue() {
        return attackQueue;
    }

    public BuffService getBuffService() {
        return buffService;
    }

    public AttackService getAttackService() {
        return attackService;
    }

    public void newRound() {
        round++;
        log("New round starting: %1$s", round);

        if (round > 30) {
            throw new IllegalStateException("Round 30 reached.");
        }

//        for (MonsterBattleStats monsterBattleStats : getAllMonsters()) {
//            if (monsterBattleStats.isDead()) {
//                throw new IllegalStateException("Dead monster on board at start of round.");
//            }
//        }

        resetAttackQueue();
    }

    public ChoiceStrategyMode getChoiceStrategyMode() {
        return choiceStrategyMode;
    }

    public void setChoiceStrategyMode(ChoiceStrategyMode choiceStrategyMode) {
        this.choiceStrategyMode = choiceStrategyMode;
    }

    public DeathService getDeathService() {
        return deathService;
    }

//    public List<MonsterBattleStats> checkAndClearTheDead() {
//        for (MonsterBattleStats monsterBattleStats)
//    }

    public boolean gameHasWinner() {
        if ((board.getBoardTop().isEmpty(1)) || (board.getBoardBottom().isEmpty(1))) {
            setGameState(GameState.GAME_COMPLETED);
            return true;
        }
        return false;
    }

    @JsonIgnore
    public GameWinner getGameWinner() {
        if (!gameHasWinner()) {
            throw new IllegalStateException("Game is not yet completed.");
        }

        if ((board.getBoardTop().peekMonsterBattleStats().size() == 0) && (board.getBoardBottom().peekMonsterBattleStats().size() == 0)) {
            return GameWinner.DRAW;
        }
        else if (board.getBoardTop().peekMonsterBattleStats().size() == 0) {
            return GameWinner.BOTTOM;
        }
        else if (board.getBoardBottom().peekMonsterBattleStats().size() == 0) {
            return GameWinner.TOP;
        }


//        if (board.getBoardTop().isEmpty(1) && board.getBoardBottom().isEmpty(1)) {
//            return GameWinner.DRAW;
//        }
//        if (board.getBoardTop().isEmpty(1)) {
//            return GameWinner.BOTTOM;
//        }
//        if (board.getBoardBottom().isEmpty(1)) {
//            return GameWinner.TOP;
//        }
//        System.out.println("Board: " + board.toSimpleString());
//        System.out.println("Top: " + board.getBoardTop().peekMonsterBattleStats());
//        System.out.println("Bottom: " + board.getBoardBottom().peekMonsterBattleStats());

        throw new IllegalStateException("Unable to determine winner.");
    }

    public void monsterSpeedAltered() {
        resetAttackQueue();
    }

    @JsonIgnore
    public MonsterBattleStats getNextMonsterForTurn() {
        if (attackQueue.size() == 0) {
            return null;
        }
        return attackQueue.remove(0);
    }

    public int attackersInQueue() {
        return attackQueue.size();
    }

    public AbilityFactory getAbilityFactory() {
        return abilityFactory;
    }

    //TODO: Move this to the attack service?
    private List<MonsterBattleStats> getAttackQueue(List<MonsterBattleStats> existingList) {
        List<MonsterBattleStats> sortedOrder = new ArrayList<>();

        if ((existingList != null) && (existingList.size() == 0)) {
            return sortedOrder;
        }

        List<MonsterBattleStats> allMonsters = existingList;
        if (allMonsters == null) {
            allMonsters = new ArrayList<>();
        }

        if (existingList == null) {
            allMonsters.addAll(board.getBoardTop().peekMonsterBattleStats());
            allMonsters.addAll(board.getBoardBottom().peekMonsterBattleStats());
        }

        int topSpeed = allMonsters.stream()
                .filter(m -> m != null)
                .mapToInt(m -> m.getSpeed().getValue())
                .max().getAsInt();

        if (!gameRules.contains(GameRuleType.REVERSE_SPEED)) {
            for (int i = topSpeed; i >= 0; i--) {
                final int ii = i;
                setQueueForSpeed(sortedOrder, allMonsters, ii);
            }
        }
        else {
            for (int i = 1; i <= topSpeed; i++) {
                final int ii = i;
                setQueueForSpeed(sortedOrder, allMonsters, ii);
            }
        }

        return sortedOrder;
    }

    private void setQueueForSpeed(List<MonsterBattleStats> sortedOrder, List<MonsterBattleStats> allMonsters, int ii) {
        Set<MonsterBattleStats> currentSpeedMonsters = allMonsters.stream()
                .filter(m->m != null)
                .filter(m->m.getSpeed().getValue() == ii)
                .filter(m->m.getType() == MonsterType.MONSTER)
                .collect(Collectors.toSet());

        sortedOrder.addAll(addOfType(DamageType.NONE, currentSpeedMonsters, sortedOrder));
        sortedOrder.addAll(addOfType(DamageType.MAGIC, currentSpeedMonsters, sortedOrder));
        sortedOrder.addAll(addOfType(DamageType.RANGED, currentSpeedMonsters, sortedOrder));
        sortedOrder.addAll(addOfType(DamageType.ATTACK, currentSpeedMonsters, sortedOrder));
    }

    private List<MonsterBattleStats> addOfType(DamageType damageType, Set<MonsterBattleStats> monsterBattleStatsList, List<MonsterBattleStats> excludeList) {
        if ((monsterBattleStatsList == null) || (monsterBattleStatsList.size() == 0)) {
            return Collections.EMPTY_LIST;
        }

        List<MonsterBattleStats> monsterBattleStats = monsterBattleStatsList.stream()
                    .filter(m -> m.isOfDamageType(damageType))
                    .collect(Collectors.toList());

        for (MonsterBattleStats exclude : excludeList) {
            if (monsterBattleStats.contains(exclude)) {
                monsterBattleStats.remove(exclude);
            }
        }

        if (monsterBattleStats.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        if (monsterBattleStats.size() == 1) {
            return Collections.singletonList(monsterBattleStats.get(0));
        }

        Collections.shuffle(monsterBattleStats);

        if (monsterBattleStats.size() > 6) {
//            System.out.println("Warning: Permutation size too large for choice gate on monster turn selection: " + monsterBattleStats.size());
            return monsterBattleStats;
        }

        ChoiceGate<List<MonsterBattleStats>> choiceGate = getChoiceGateForAttacker(monsterBattleStats);
        List<MonsterBattleStats> result = choiceGate.getResult();
        return result;
    }

    private ChoiceGate<List<MonsterBattleStats>> getChoiceGateForAttacker(List<MonsterBattleStats> list) {
        ChoiceGateFactory.Configuration<List<MonsterBattleStats>> choiceGateConfiguration = new ChoiceGateFactory.Configuration<List<MonsterBattleStats>>()
                .setGateId("ATTACK_ORDER")
                .setChoiceMode(getChoiceStrategyMode())
                .returnPermutationOf(list)
                .returnIfEmptyOrNull(Collections.EMPTY_LIST);

        return getMasterChoiceContext().getChoiceGateFactory().buildGate(choiceGateConfiguration);
    }

    public Integer getRound() {
        return round;
    }

    public List<GamePhase> getGamePhases() {
        return queuedGamePhases;
    }

    public Board getBoard() {
        return board;
    }

    public List<MonsterBattleStats> getAllMonsters() {
        List<MonsterBattleStats> monsters = new ArrayList<>();
        monsters.addAll(getBoard().getBoardTop().peekMonsterBattleStats());
        monsters.addAll(getBoard().getBoardBottom().peekMonsterBattleStats());

        return monsters;
    }

    public boolean isTrackHistory() {
        return trackHistory;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public String toString() {
        return "GameContext{" +
                "gameState=" + gameState +
                ", board=" + board +
                ", round=" + round +
                ", queuedGamePhases=" + queuedGamePhases +
                '}';
    }
}
