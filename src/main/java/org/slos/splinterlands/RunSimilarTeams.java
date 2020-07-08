package org.slos.splinterlands;

import org.slos.AppConfig;
import org.slos.SimpleCard;
import org.slos.Team;
import org.slos.TeamRequestProcessStep;
import org.slos.domain.RankingReport;
import org.slos.domain.RealizedCollection;
import org.slos.domain.RealizedCollectionCard;
import org.slos.domain.TeamRequestContext;
import org.slos.ranking.RankingService;
import org.slos.splinterlands.collection.CollectionCard;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RunSimilarTeams implements TeamRequestProcessStep {
    @Autowired private RankingService rankingService;
    @Autowired AppConfig appConfig;
    @Value("${defaults.similarTeamCountPermutation}") Integer similarTeamCountPermutation;
    @Value("${defaults.similarTeamCountTeams}") Integer similarTeamCountTeams;

    private final Logger logger = LoggerFactory.getLogger(RunSimilarTeams.class);

    public static final String CHICKEN_FIRST_SOURCE = "ChickenFirst";
    public static final String SIMILAR_TEAMS = "SimilarTeams";

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        if (teamRequestContext.getShouldYield()) {
            return teamRequestContext;
        }

        RankingReport rankingReport = teamRequestContext.getRankingReport();
        Long teamRequestTimeout = teamRequestContext.getTeamRequestTimeout();
        List<Team> opponentTeams = teamRequestContext.getOpponentTeams();
        Set<String> newTeamsToTry = new HashSet<>();
        RealizedCollection playerCollection = teamRequestContext.getPlayerCollection();

        List<Team> goodTeams = new ArrayList<>();
        for (int i = 0; i < similarTeamCountTeams && i < teamRequestContext.getResultTeamRanks().size(); i++) {
            if (teamRequestContext.getResultTeamRanks().get(i) != null) {
                goodTeams.add(rankingReport.getRanks().get(teamRequestContext.getResultTeamRanks().get(i).getId()).getTeam());
            }
        }

        List<Team> playerTeams = new ArrayList<>(generateSimilarTeams(goodTeams, similarTeamCountPermutation));
        newTeamsToTry.addAll(playerTeams.stream().map(team -> team.getId()).collect(Collectors.toSet()));

        List<Team> replacementCardTeams = new ArrayList<>(generateSwappedCardTeams(goodTeams, playerCollection));
        newTeamsToTry.addAll(replacementCardTeams.stream().map(team -> team.getId()).collect(Collectors.toSet()));

        if (playerCollection.hasCard(131)) {
            List<Team> teamsWithChickenFirst = new ArrayList<>(getWithChickenFirst(goodTeams, playerCollection));
            newTeamsToTry.addAll(teamsWithChickenFirst.stream().map(team -> team.getId()).collect(Collectors.toSet()));
        }

        RankingReport similarTeamsRankingReport = rankTeams(playerTeams, opponentTeams, teamRequestContext, teamRequestTimeout, teamRequestContext.getTeamRequest().getPlayerTop());

        for (String id : similarTeamsRankingReport.getRanks().keySet()) {
            teamRequestContext.getRankingReport().getRanks().put(id, similarTeamsRankingReport.getRanks().get(id));
        }

        return teamRequestContext;
    }

    private List<Team> getWithChickenFirst(List<Team> goodTeams, RealizedCollection realizedCollection) {
        List<Team> withChickenFirst = new ArrayList<>();
        Optional<CollectionCard> chickenOptional = realizedCollection.getCards().stream().filter(realizedCollectionCard -> realizedCollectionCard.getMonsterDetails().getId() == 131).findFirst().map(realizedCollectionCard -> realizedCollectionCard.getCollectionCard());

        if (chickenOptional.isPresent()) {
            for (Team team : goodTeams) {
                if (!team.hasCard(131) && team.getCards().size() == 7) {
                    continue;
                }
                CollectionCard chicken = chickenOptional.get();
                List<SimpleCard> cardsWithChickenFirst = new ArrayList<>();
                int id = chicken.getMonsterId();
                String collectionId = realizedCollection.getPlayer();
                int level = chicken.getLevel();
                String name = "Furious Chicken";
                ColorType colorType = ColorType.GRAY;
                Boolean isGoldFoil = chicken.getGold();
                int rarity = 3;
                MonsterType monsterType = MonsterType.MONSTER;

                cardsWithChickenFirst.add(new SimpleCard(id, collectionId, level, name, colorType, isGoldFoil, rarity, monsterType));

                cardsWithChickenFirst.add(team.getCards().get(0));
                for (int i = 1; i < team.getCards().size(); i++) {
                    if (team.getCards().get(i).getId() != 131) {
                        cardsWithChickenFirst.add(new SimpleCard(team.getCards().get(i)));
                    }
                }

                Team teamWithChickenFirst = new Team(cardsWithChickenFirst, team.getColorType(), team.getRank(), team.getSource() + "|" + CHICKEN_FIRST_SOURCE);

                logger.info("Created chicken first team: " + teamWithChickenFirst.getId());
                logger.info("From team: " + team.getId());

                withChickenFirst.add(teamWithChickenFirst);
            }
        }

        return withChickenFirst;
    }

    private Set<Team> generateSwappedCardTeams(List<Team> teams, RealizedCollection collection) {
        Set<Team> newTeams = new HashSet<>();
        Set<Team> alreadyRanTeams = new HashSet<>();
        alreadyRanTeams.addAll(teams);

        for (Team team : teams) {
            for (SimpleCard simpleCard : team.getCards()) {

                List<Integer> replacementCards = appConfig.getCardReplacementOptions().get(simpleCard.getId());
                if (replacementCards != null) {

                    for (Integer replacementId : replacementCards) {
                        String id = null;
                        List<SimpleCard> newTeamCards = new ArrayList<>();
                        for (SimpleCard simpleCardTeam : team.getCards()) {
                            newTeamCards.add(simpleCardTeam);
                        }

                        ColorType colorType = team.getColorType();

                        if (collection.hasCard(replacementId)) {
                            //TODO: Find highest level available instead of first
                            Optional<RealizedCollectionCard> replacementCardOptional = collection.getHighestLevelCardOf(replacementId);

                            if (replacementCardOptional.isPresent()) {
                                RealizedCollectionCard card = replacementCardOptional.get();
                                Integer cardId = card.getMonsterDetails().getId();
                                String collectionId = collection.getPlayer();
                                Integer level = card.getCollectionCard().getLevel();
                                String name = card.getMonsterDetails().getName();
                                ColorType cardColorType = card.getMonsterDetails().getColor();
                                boolean isGoldFoil = card.getCollectionCard().getGold();
                                Integer rarity = card.getMonsterDetails().getRarity();
                                MonsterType monsterType = card.getMonsterDetails().getType();
                                SimpleCard replacementCard = new SimpleCard(cardId, collectionId, level, name, cardColorType, isGoldFoil, rarity, monsterType);

                                Collections.replaceAll(newTeamCards, simpleCard, replacementCard);

                                Team newTeam = new Team(id, newTeamCards, colorType, 1000f, team.getSource() + "|" + SIMILAR_TEAMS);
                                newTeam.setId(newTeam.toSimpleString());

                                if (!alreadyRanTeams.contains(newTeam)) {
                                    logger.info("Replacing team: " + team.toSimpleString());
                                    logger.info("With team     : " + newTeam.toSimpleString());
                                    newTeams.add(newTeam);
                                }
                            }
                        }
                    }
                }
            }
        }

        return newTeams;
    }

    private Set<Team> generateSimilarTeams(List<Team> teams, Integer permutationCount) {

        Set<Team> newTeams = new HashSet<>();
        Set<Team> alreadyRanTeams = new HashSet<>();
        alreadyRanTeams.addAll(teams);

        for (Team team : teams) {
            for (int i = 0; i < permutationCount; i++) {
                if (team.getCards().size() > 3) {
                    String id = team.getId();
                    List<SimpleCard> frontCards = new ArrayList<>(team.getCards().subList(0, 2));
                    List<SimpleCard> backCards = new ArrayList<>(team.getCards().subList(2, team.getCards().size()));
                    Collections.shuffle(backCards);
                    List<SimpleCard> newTeamCards = new ArrayList<>();
                    newTeamCards.addAll(frontCards);
                    newTeamCards.addAll(backCards);

                    ColorType colorType = team.getColorType();
                    Float rank = 0f;

                    Team newTeam = new Team(id, newTeamCards, colorType, rank, team.getSource() + "|" + SIMILAR_TEAMS);
                    newTeam.setId(newTeam.toMinimalId());

//                    System.out.println("Already ran? " + alreadyRanTeams.contains(newTeam) + "  - " + newTeam);
                    if (!alreadyRanTeams.contains(newTeam)) {
                        newTeams.add(newTeam);
                    }
                }
            }
        }

        return newTeams;
    }

    private RankingReport rankTeams(List<Team> topTeams, List<Team> bottomTeams, TeamRequestContext teamRequestContext, long stopGamesAt, String teamsFor) {
        Set<GameRuleType> gameRules = teamRequestContext.getTeamRequest().getRuleset();
        RankingReport rankingReport = rankingService.getBestDeckFor(topTeams, bottomTeams, gameRules, teamRequestContext.getBattlesPerMatchup(), stopGamesAt, teamsFor);

        rankingReport.setTeamRequestContext(teamRequestContext);

        return rankingReport;
    }
}