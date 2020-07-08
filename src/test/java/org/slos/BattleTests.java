package org.slos;

import org.slos.battle.BattleMaster;
import org.slos.battle.GameContext;
import org.slos.battle.GameWinner;
import org.slos.battle.board.Board;
import org.slos.battle.board.Deck;
import org.slos.battle.decision.ChoiceGate;
import org.slos.battle.decision.MasterChoiceContext;
import org.slos.battle.decision.strategy.ChoiceStrategyMode;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.collection.CollectionCard;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.history.Battle;
import org.slos.splinterlands.history.BattleHistory;
import org.slos.splinterlands.history.PlayerBattleHistoryService;
import org.slos.util.ToJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slos.splinterlands.history.Team;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
public class BattleTests implements TestHelper {
    @Autowired private MonsterBattleStatsService monsterBattleStatsService;
    @Autowired
    private PlayerBattleHistoryService playerBattleHistoryService;

    @Test
    public void itShouldValidateBattlesFromHistory() {
        int battleRuns = 400;
        int acceptableThreshold = 0;

        List<String> historiesToCheck = new LinkedList<>();
        historiesToCheck.add("bji1203");
        historiesToCheck.add("wonsama");
        historiesToCheck.add("hossainbd");
        historiesToCheck.add("imperfect-one");
        historiesToCheck.add("toocurious");
        historiesToCheck.add("goldfashioned");
        historiesToCheck.add("smk2000");
        historiesToCheck.add("bigjoy");
        historiesToCheck.add("jacekw");
        historiesToCheck.add("donekim");
        historiesToCheck.add("fenrir78");
        historiesToCheck.add("ninjascott");


        for (String historyFor : historiesToCheck) {
            List<BattleTeams> battles = getLatestFromHistory(historyFor);
            for (BattleTeams battleTeams : battles) {
                boolean valid = verifyBattle(battleRuns, acceptableThreshold, battleTeams);

                if (!valid) {
                    boolean secondValidation = verifyBattle(battleRuns * 10, acceptableThreshold, battleTeams);

                    if (!secondValidation) {
                        throw new RuntimeException();
                    }
                }
            }
        }
    }

    private boolean verifyBattle(int battleRuns, int acceptableThreshold, BattleTeams battleTeams) {
        System.out.println("Checking battle: " + battleTeams.toJson());
        int topWinCount = 0;
        int bottomWinCount = 0;
        String topPlayer = battleTeams.topPlayer;
        String bottomPlayer = battleTeams.bottomPlayer;

        battles:
        for (int i = 0; i < battleRuns; i++) {
            Board board = new Board(7);
            Deck topDeck = getDeckFromTeam(battleTeams.topTeam);
            Deck bottomDeck = getDeckFromTeam(battleTeams.bottomTeam);
            board.populateBoard(topDeck, bottomDeck);
            if (i == 0) {
                System.out.println(board.toSimpleString());
            }
            Set<GameRuleType> getFromRuleset = battleTeams.rules;

            GameContext gameContext = new GameContext(board, getFromRuleset);
            BattleMaster battleMaster = new BattleMaster();

            gameContext.setTrackHistory(false);
            gameContext.setChoiceStrategyMode(ChoiceStrategyMode.RANDOM_WEIGHTED);
            GameWinner gameWinner = null;
            try {
                gameWinner = battleMaster.runGame(gameContext);
            } catch (Exception e) {
                gameContext.printHistory();
                e.printStackTrace();
            }

            if (gameWinner.equals(GameWinner.TOP)) {
                topWinCount++;
            } else if (gameWinner.equals(GameWinner.BOTTOM)) {
                bottomWinCount++;
            }

            if ((topWinCount > acceptableThreshold) && (bottomWinCount > acceptableThreshold)) {
                break battles;
            }
        }

        System.out.println("Top win count: " + topWinCount + " - " + topPlayer);
        System.out.println("Bottom win count: " + bottomWinCount + " - " + bottomPlayer);
        String winner = topWinCount >= bottomWinCount ? topPlayer : bottomPlayer;
        System.out.println("Winner: " + winner);


        return verifyClearWinner(battleRuns, acceptableThreshold, battleTeams, topWinCount, bottomWinCount, topPlayer, winner);
    }

    private boolean verifyClearWinner(int battleRuns, int acceptableThreshold, BattleTeams battleTeams, int topWinCount, int bottomWinCount, String topPlayer, String winner) {
        if ((topWinCount >= battleRuns - acceptableThreshold) || (bottomWinCount >= battleRuns - acceptableThreshold)) {
            System.out.println("Sure win for: " + topPlayer);
            if (!winner.equals(battleTeams.gameWinner)) {

                System.out.println("Simulation Winner: " + winner + " vs. Actual Winner: " + battleTeams.gameWinner);
                System.err.println("ERROR ON CLEAR WINNER: " + battleTeams);

                System.out.println("Code:");
                System.out.println("summoner1 = monsterBattleStatsService.getMonsterBattleStats(" + battleTeams.getTopTeam().getSummoner().getMonsterId() + ", " + battleTeams.getTopTeam().getSummoner().getLevel() + ");");
                for (CollectionCard card : battleTeams.topTeam.getMonsters()) {
                    System.out.println("topMonsters.add(monsterBattleStatsService.getMonsterBattleStats(" + card.getMonsterId() + ", " + card.getLevel() + "));");
                }
                System.out.println("summoner2 = monsterBattleStatsService.getMonsterBattleStats(" + battleTeams.getBottomTeam().getSummoner().getMonsterId() + ", " + battleTeams.getBottomTeam().getSummoner().getLevel() + ");");
                for (CollectionCard card : battleTeams.bottomTeam.getMonsters()) {
                    System.out.println("bottomMonsters.add(monsterBattleStatsService.getMonsterBattleStats(" + card.getMonsterId() + ", " + card.getLevel() + "));");
                }

                return false;
            }
        }

        return true;
    }

    @Test
    public void itShouldRunBattles() {
        System.out.println("Starting.");
        int gameCount = 1;
        ChoiceStrategyMode choiceMode = ChoiceStrategyMode.RANDOM_WEIGHTED;
//        ChoiceStrategyMode choiceMode = ChoiceStrategyMode.WEIGHT_ENFORCED;
        int topWinCount = 0;
        int bottomWinCount = 0;
        int draw = 0;
        int maxRounds = 0;
        long totalRounds = 0;
        Set<Long> uniqueGames = new HashSet<>();
        int gameDuplicates = 0;
        boolean logHistory = true;

        Deck topDeck = null;
        Deck bottomDeck = null;

        MasterChoiceContext masterChoiceContext = new MasterChoiceContext();
        GameContext gameContext = null;
        Board board = null;

        String topDeckToString = "";
        String bottomDeckToString = "";

        System.gc();

        Long start = System.currentTimeMillis();

        int errorCount = 0;
        Throwable firstError = null;

        for (int x = 0; x < gameCount; x++) {
            try {
                Set<GameRuleType> gameRules = new HashSet<>();
//                gameRules.add(GameRuleType.LOST_MAGIC);
                gameRules.add(GameRuleType.KEEP_YOUR_DISTANCE);


                MonsterBattleStats summoner1 = null;
                MonsterBattleStats summoner2 = null;
                List<MonsterBattleStats> topMonsters = new ArrayList<>();
                List<MonsterBattleStats> bottomMonsters = new ArrayList<>();

                summoner1 = monsterBattleStatsService.getMonsterBattleStats(56, 3);
                topMonsters.add(monsterBattleStatsService.getMonsterBattleStats(82, 3));
                topMonsters.add(monsterBattleStatsService.getMonsterBattleStats(169, 6));
                topMonsters.add(monsterBattleStatsService.getMonsterBattleStats(93, 6));
                topMonsters.add(monsterBattleStatsService.getMonsterBattleStats(79, 8));
                topMonsters.add(monsterBattleStatsService.getMonsterBattleStats(134, 6));
                topMonsters.add(monsterBattleStatsService.getMonsterBattleStats(14, 8));
                summoner2 = monsterBattleStatsService.getMonsterBattleStats(189, 8);
                bottomMonsters.add(monsterBattleStatsService.getMonsterBattleStats(180, 10));
                bottomMonsters.add(monsterBattleStatsService.getMonsterBattleStats(218, 10));
                bottomMonsters.add(monsterBattleStatsService.getMonsterBattleStats(185, 8));
                bottomMonsters.add(monsterBattleStatsService.getMonsterBattleStats(33, 4));

                topDeck = new Deck(summoner1, topMonsters);
                bottomDeck = new Deck(summoner2, bottomMonsters);

//                topMonsters.add(monsterBattleStatsService.getMonsterBattleStats(82, 2));
//                topMonsters.add(monsterBattleStatsService.getMonsterBattleStats(82, 2));
//                topMonsters.add(monsterBattleStatsService.getMonsterBattleStats(82, 2));
//                bottomMonsters.add(monsterBattleStatsService.getMonsterBattleStats(82, 2));
//                bottomMonsters.add(monsterBattleStatsService.getMonsterBattleStats(82, 2));
//                bottomMonsters.add(monsterBattleStatsService.getMonsterBattleStats(82, 2));

                topDeckToString = topDeck.toString();
                bottomDeckToString = bottomDeck.toString();

//            System.out.println("Top Deck:    " + topDeck);
//            System.out.println("Bottom Deck: " + bottomDeck);

                board = new Board(7);
                board.populateBoard(topDeck, bottomDeck);
//                System.out.println("Board: " + board.toSimpleString());
                gameContext = new GameContext(board, gameRules);
                gameContext.setMasterChoiceContext(masterChoiceContext);
//                gameContext.setChoiceStrategyMode(ChoiceStrategyMode.WEIGHT_ENFORCED);
                gameContext.setChoiceStrategyMode(choiceMode);
                gameContext.setTrackHistory(logHistory);
                BattleMaster battleMaster = new BattleMaster();

//                System.out.println("About to play game: " + board.toSimpleString());

                battleMaster.runGame(gameContext);

                if ((board.getBoardTop().peekMonsterBattleStats().size() == 0) && (board.getBoardBottom().peekMonsterBattleStats().size() == 0)) {
                    draw++;
                }
                else if (board.getBoardTop().peekMonsterBattleStats().size() == 0) {
                    bottomWinCount++;
                }
                else if (board.getBoardBottom().peekMonsterBattleStats().size() == 0) {
                    topWinCount++;
                }

                if (maxRounds < gameContext.getRound()) {
                    maxRounds = gameContext.getRound();
                }
                totalRounds += gameContext.getRound();

                if (uniqueGames.contains(masterChoiceContext.getLatestDecisionHash())) {
                    gameDuplicates++;
                } else {
                    uniqueGames.add(masterChoiceContext.getLatestDecisionHash());
                }

                if (x < gameCount - 1) {
                    masterChoiceContext.reset();
                }

                if (gameContext.isTrackHistory()) {
                    List<String> history = gameContext.getAttackHistory();
                    for (String log : history) {
                        System.out.println(log);
                    }
                }
            }
            catch (Throwable t) {
                t.printStackTrace();

                gameContext.printHistory();

                errorCount++;
                if (firstError == null) {
                    firstError = t;
                }

                if (gameContext.isTrackHistory()) {
                    List<String> history = gameContext.getAttackHistory();

                    for (String log : history) {
                        System.out.println(log);
                    }
                }
                throw t;
            }
        }
        DecimalFormat df = new DecimalFormat("###.###");

        System.out.println("Last Game Result: " + board.toSimpleString());
        System.out.println();
        System.out.println("Total Game Count: " + gameCount);
        System.out.println("Exceptions During Games: " + errorCount);
        System.out.println("Draws: " + draw + " = " + df.format((Float.valueOf(draw) / Float.valueOf(gameCount - errorCount)) * 100) + "%");
        System.out.println("Top wins: " + topWinCount + " = " + df.format((Float.valueOf(topWinCount) / Float.valueOf(gameCount - errorCount)) * 100) + "%");
        System.out.println("Bottom wins: " + bottomWinCount + " = " + df.format((Float.valueOf(bottomWinCount) / Float.valueOf(gameCount - errorCount)) * 100) + "%");
        System.out.println("Max Rounds: " + maxRounds);
        System.out.println("Average Rounds: " + (totalRounds/gameCount));
        if (choiceMode.requiresMasterContext()) {
            System.out.println("Non-unique outcomes: " + gameDuplicates + " + " + "Unique outcomes: " + uniqueGames.size());
            System.out.println("Total ChoiceGates implemented: " + masterChoiceContext.getGateTotal());
        }
        System.out.println("Total decisions made: " + masterChoiceContext.getTotalChoicesMade());
        System.out.println("Average choices per game: " + masterChoiceContext.getTotalChoicesMade() / gameCount);
        System.out.println("Total time: " + (System.currentTimeMillis() - start) + "ms  -  Average Per Game: " + ((System.currentTimeMillis() - start)/gameCount) + "ms");
        System.out.println();
        System.out.println("Top Deck: " + topDeckToString);
        System.out.println("Bottom Deck: " + bottomDeckToString);

        for (List<ChoiceGate> choiceGates : masterChoiceContext.getChoiceGatesById().values()) {
            if (!choiceGates.get(0).getGateId().equals("ATTACK_ORDER")) {

                Map<Integer, Long> totals = new HashMap<>();
                System.out.print(choiceGates.get(0).getGateId() + "[" + choiceGates.size() + "]: ");
                Integer[] keys = (Integer[]) choiceGates.get(0).getChoiceGateContext().getTimesUsed().keySet().toArray(new Integer[0]);

                for (Integer key : keys) {
                    totals.put(key, 0l);
                }


                for (ChoiceGate choiceGate : choiceGates) {
                    for (Integer key : keys) {
                        Long previousCount = totals.get(key);
                        totals.put(key, (previousCount + (Long) choiceGate.getChoiceGateContext().getTimesUsed().get(key)));
                    }
                }

                System.out.print("" + totals + "  =  ");
                Long totalUses = totals.values().stream().reduce(0l, (a, b) -> a + b);

                long totalTotals = 0l;
                for (Integer key : totals.keySet()) {
                    totalTotals += totals.get(key);
                }

                for (Integer key : totals.keySet()) {
                    System.out.print(key + " -> " + df.format((((float)totals.get(key)) / ((float)totalTotals)) * 100) + "% ");
                }
                System.out.println();
            }
            else {
                System.out.println(choiceGates.get(0).getGateId() + "[" + choiceGates.size() + "]: ");
            }
        }
        System.out.println();
        System.out.println();
        System.out.println();
        if (firstError!= null) {
            firstError.printStackTrace();
        }
    }

    private List<BattleTeams> getLatestFromHistory(String userId) {
        List<BattleTeams> battleTeamsList = new ArrayList<>();
        BattleHistory battleHistory = playerBattleHistoryService.getBattleHistoryFor(userId);
        List<Battle> battles = battleHistory.getBattles();

        for (Battle battle : battles) {
            Team team1 = battle.getDetails().getTeam1();
            Team team2 = battle.getDetails().getTeam2();

            if ((team1 != null) && (team2 != null)) {
                String rules = battle.getRuleset();
                Set<GameRuleType> ruleSet = GameRuleType.getFromRuleset(rules);
                BattleTeams battleTeams = new BattleTeams(team1, team2, ruleSet, battle.getManaCap(), battle.getPlayer_1(), battle.getPlayer_2(), battle.getWinner());
                battleTeamsList.add(battleTeams);
            }
        }

        return battleTeamsList;
    }

    public Deck getDeckFromTeam(Team team) {
        List<MonsterBattleStats> monsters = new ArrayList<>();
        for (CollectionCard collectionCard : team.getMonsters()) {
            monsters.add(monsterBattleStatsService.getMonsterBattleStats(collectionCard.getMonsterId(), collectionCard.getLevel()));
        }
        MonsterBattleStats summoner = monsterBattleStatsService.getMonsterBattleStats(team.getSummoner().getMonsterId(), team.getSummoner().getLevel());
        Deck deck = new Deck(summoner, monsters);

        return deck;
    }

    private class BattleTeams implements ToJson {
        private Team topTeam;
        private Team bottomTeam;
        private Set<GameRuleType> rules;
        private int mana;
        private String gameWinner;
        private String topPlayer;
        private String bottomPlayer;

        public BattleTeams(){}

        public BattleTeams(Team topTeam, Team bottomTeam, Set<GameRuleType> rules, int mana, String topPlayer, String bottomPlayer, String gameWinner) {
            this.topTeam = topTeam;
            this.bottomTeam = bottomTeam;
            this.rules = rules;
            this.mana = mana;
            this.gameWinner = gameWinner;
            this.topPlayer = topPlayer;
            this.bottomPlayer = bottomPlayer;
        }

        public Team getTopTeam() {
            return topTeam;
        }

        public Team getBottomTeam() {
            return bottomTeam;
        }

        public Set<GameRuleType> getRules() {
            return rules;
        }
    }
}
