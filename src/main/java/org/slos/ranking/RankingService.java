package org.slos.ranking;

import org.slos.MonsterBattleStatsService;
import org.slos.SimpleCard;
import org.slos.Team;
import org.slos.battle.BattleMaster;
import org.slos.battle.ExccededAllowedTimeRuntimeException;
import org.slos.battle.GameContext;
import org.slos.battle.GameWinner;
import org.slos.battle.board.Board;
import org.slos.battle.board.Deck;
import org.slos.battle.decision.strategy.ChoiceStrategyMode;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.domain.RankingReport;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.util.WithMDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RankingService {
    @Autowired private MonsterBattleStatsService monsterBattleStatsService;
    @Value("${defaults.threadsForSimulations}") private Integer threadsForSimulations;

    private final Logger logger = LoggerFactory.getLogger(MonsterBattleStatsService.class);

    public RankingReport getBestDeckFor(List<Team> decksTop, List<Team> decksBottom, Set<GameRuleType> gameRules, int matchesPerTeam, long stopGamesAt, String teamsFor) {

        Set<String> duplicateCheck = new HashSet<>();
        for (Team team : new ArrayList<>(decksTop)) {
            if (duplicateCheck.contains(team.getId())) {
                System.out.println("DUPLICATE TEAM FOUND: " + team);
                logger.error("DUPLICATE TEAM FOUND: " + team);
                decksTop.remove(team);
//                throw new RuntimeException("Duplicate team found!");
            }
            else {
                duplicateCheck.add(team.getId());
            }

            int totalManaCount = 0;
            for (SimpleCard card : team.getCards()) {
                totalManaCount += monsterBattleStatsService.getMonsterDetals(card.getId()).getMonsterStats().getMana()[0];
            }
        }

        logger.info("It has begun! - Games per matchup: " + matchesPerTeam);
        StringBuffer topTeams = new StringBuffer();
        decksTop.stream().forEach(team -> topTeams.append(team.toMinimalId() + " " + team.getRank() + " -- "));
        StringBuffer bottomTeams = new StringBuffer();
        decksBottom.stream().forEach(team -> bottomTeams.append(team.toMinimalId() + " " + team.getRank() + " -- "));
        logger.info(String.format("Top Teams:    %1$s", decksTop.size()));
        logger.info(String.format("Bottom Teams: %1$s", decksBottom.size()));
        logger.info("");

        RankingReport rankingReport = new RankingReport();
        ExecutorService executor = Executors.newFixedThreadPool(threadsForSimulations);
        List<CompletableFuture<Object>> futures = new ArrayList<>();
        final Set<Integer> counter = new HashSet<>();

        for (int i = 0; i < decksTop.size(); i++) {
            for (int x = 0; x < decksBottom.size(); x++) {
                try {
                    final int ii = i;
                    final int xx = x;
                    futures.add(CompletableFuture.supplyAsync(WithMDC.run(() -> {

                                final long startAt = System.currentTimeMillis();

                                if (startAt < stopGamesAt) {
                                    boolean printGame = false;

                                    Team team = decksTop.get(ii);
                                    String teamId = team.getId();

                                    synchronized (rankingReport) {
                                        if (!rankingReport.getRanks().containsKey(teamId)) {
                                            TeamStats teamStats = new TeamStats(team);
                                            rankingReport.getRanks().put(teamId, teamStats);
                                        }
                                    }

                                    if (!team.getId().equals(decksBottom.get(xx).getId())) {
                                        playMatch(team, decksBottom.get(xx), gameRules, matchesPerTeam, rankingReport, printGame, stopGamesAt);
                                    }
                                    else {
                                        TeamStats teamStats = rankingReport.getRanks().get(teamId);
                                        teamStats.setHadMirrorMatch(true);
                                    }
                                }
                                else {
                                    counter.add(Integer.valueOf(ii));
                                }
                                return null;
                            }), executor));

                } catch (Exception e) {
                    logger.error(decksTop.get(i).toSimpleString() + " <- vs. -> " + decksBottom.get(x).toSimpleString());
                    logger.error("Exception playing match!", e);
                }
            }
        }

        try {
            CompletableFuture allGames = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
            allGames.get();

            executor.shutdown();

            if (counter.size() > 0) {
                logger.error("Stopping matches due to max processing time exceeded. Matches complete: " + (counter.size()) + "/" + decksTop.size());
            }
        }
        catch (Exception e) {
            logger.error("Failed: ", e);
            System.out.println(e);
        }

        logger.info("Finished. Total game count: " + rankingReport.getTotalGameCount());

        //TODO: Figure out why teams with mirror match skipped have max win count
//        for (TeamStats teamStats : rankingReport.getRanks().values()) {
//            if (teamStats.getHadMirrorMatch()) {
//                System.out.println("MM Start: " + teamStats.getTotalWins());
//                teamStats.addToWin((int)(decksBottom.size() * teamStats.getWinRatio()));
//                System.out.println("MM End: " + teamStats.getTotalWins());
//            }
//        }

        return rankingReport;
    }

    private Void playMatch(Team topTeam, Team bottomTeam, Set<GameRuleType> gameRules, int matchCount, RankingReport rankingReport, boolean printGame, long stopGamesAt) {
        if ((topTeam == null) || (bottomTeam == null)) {
            return null;
        }

        TeamStats teamStats = rankingReport.getRanks().get(topTeam.getId());

        if (teamStats == null) {
            throw new RuntimeException("Team rank not found: " + topTeam.getId());
        }

        for (int i = 0; i < matchCount; i++) {
            GameContext gameContext = null;
            Deck topDeck = deseralizeToDeck(topTeam);

            try {
                Board board = new Board(7);
                Deck bottomDeck = deseralizeToDeck(bottomTeam);

                //TODO: Evaluate doing this or not
//                if (topTeam.getCards().get(0).getLevel()) {
                    for (MonsterBattleStats monsterBattleStats : bottomDeck.getMonsters()) {
                        monsterBattleStats.getHealth().addToBaseValue(1);
                    }
//                }

                board.populateBoard(topDeck, bottomDeck);
                gameContext = new GameContext(board, gameRules);
                gameContext.setChoiceStrategyMode(ChoiceStrategyMode.RANDOM_WEIGHTED);
                gameContext.setTrackHistory(printGame);
                gameContext.setMaxProcessingTime(stopGamesAt);
                BattleMaster battleMaster = new BattleMaster();

                GameWinner winner = battleMaster.runGame(gameContext);

                switch (winner) {
                    case TOP:
                        teamStats.addToWin();
                        break;
                    case BOTTOM:
                        teamStats.addToLoss();
                        break;
                    case DRAW:
                        teamStats.addToDraw();
                        break;
                    default:
                        teamStats.addToErrorCount();
                        break;
                }

                rankingReport.incrementTotalGameCount();

                if (printGame) {
                    gameContext.printHistory();
                }
            }
            catch (ExccededAllowedTimeRuntimeException e) {
                logger.error(e.getMessage());
            }
            catch (Throwable t) {
                teamStats.addToErrorCount();
                t.printStackTrace();

//                System.out.println("Exception report: ");
//                System.out.println("Top Team: " + topTeam);
//                System.out.println("Bottom Team: " + bottomTeam);
//                for (String message : gameContext.getAttackHistory()) {
//                    System.out.println(message);
//                }

                throw t;
            }
        }

        return null;
    }

    private Deck deseralizeToDeck(Team team) {
        MonsterBattleStats summoner = monsterBattleStatsService.getMonsterBattleStats(team.getCards().get(0).getId(), team.getCards().get(0).getLevel());
        List<MonsterBattleStats> deckMonsterBattleStats = new ArrayList<>();

        for (int i = 1; i < team.getCards().size(); i++) {
            deckMonsterBattleStats.add(monsterBattleStatsService.getMonsterBattleStats(team.getCards().get(i).getId(), team.getCards().get(i).getLevel()));
        }

        return new Deck(team.getId(), summoner, deckMonsterBattleStats);
    }
}
