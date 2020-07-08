package org.slos;

import org.slos.domain.RealizedCollection;
import org.slos.domain.RealizedCollectionCard;
import org.slos.domain.TeamRank;
import org.slos.splinterlands.collection.LevelComparator;
import org.slos.splinterlands.domain.LevelLimit;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.splinterlands.domain.monster.MonsterDetails;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeamSuggestionToActualCardsService {
    @Autowired private MonsterBattleStatsService monsterBattleStatsService;
    Logger logger = LoggerFactory.getLogger(TeamSuggestionToActualCardsService.class);

    public List<Team> convertToPlayersActualCards(List<TeamRank> toConvert, RealizedCollection collection, LevelLimit levelLimit) {
        List<Team> teams = new ArrayList<>();

        if (toConvert != null) {
            for (int i = 0; i < toConvert.size(); i++) {
                TeamRank teamRank = toConvert.get(i);
                Team convertedTeam = convertToPlayersActualCards(teamRank.getId(), teamRank, collection, levelLimit);

                if (convertedTeam != null) {
                    teams.add(convertedTeam);
                }
            }
        }

        return teams;
    }

    public Team convertToPlayersActualCards(String teamId, TeamRank toConvert, RealizedCollection collection, LevelLimit levelLimit) {
        List<RealizedCollectionCard> playerCards = collection.getCards();
        List<SimpleCard> actualTeam = new ArrayList<>();
        RealizedCollectionCard summonerCollectionCard = null;
        ColorType teamColor = null;
        Float teamRank = toConvert.getRank();

        List<Integer> cardIDs = convertTeam(toConvert.getId());

        for (int i = 0; i < cardIDs.size(); i++) {
            final int ii = i;

            Optional<RealizedCollectionCard> potentialMaxCard = playerCards.stream()
                    .filter(collectionCard -> collectionCard.getCollectionCard().getMonsterId().equals(cardIDs.get(ii)))
                    .max(new LevelComparator());

            if (potentialMaxCard.isPresent()) {
                RealizedCollectionCard card = potentialMaxCard.get();

                MonsterDetails monsterDetails = monsterBattleStatsService.getMonsterDetals(card.getCollectionCard().getCardDetailId());
                MonsterType monsterType = monsterDetails.getType();

                if (monsterType == MonsterType.SUMMONER) {
                    summonerCollectionCard = card;
                    teamColor = monsterDetails.getColor();
                }

                Integer cardLevel = getMaxLevelForSummoner( card, summonerCollectionCard, monsterDetails.getType(), levelLimit);
                SimpleCard simpleCard = new SimpleCard(card.getCollectionCard().getCardDetailId(), card.getCollectionCard().getUid(), cardLevel, monsterDetails.getName(), monsterDetails.getColor(), card.getCollectionCard().getGold(), monsterDetails.getRarity(), monsterDetails.getType());

                actualTeam.add(simpleCard);
            }
            else {
                return null;
            }
        }

        return new Team(teamId, actualTeam, teamColor, teamRank, toConvert.getSource());
    }

    private int getMaxLevelForSummoner(RealizedCollectionCard monster, RealizedCollectionCard summoner, MonsterType monsterType, LevelLimit levelLimit) {
        MonsterDetails summonerDetails = monsterBattleStatsService.getMonsterDetals(summoner.getCollectionCard().getCardDetailId());
        MonsterDetails monsterDetails = monsterBattleStatsService.getMonsterDetals(monster.getCollectionCard().getCardDetailId());

        Integer summonerRarity = summonerDetails.getRarity();
        Integer summonerLevel = summoner.getCollectionCard().getLevel();
        Integer monsterRarity = monsterDetails.getRarity();
        Integer monsterLevel = monster.getCollectionCard().getLevel();
        Integer maxLevel = 1;

        return getMaxLevelForSummoner(summonerRarity, summonerLevel, monsterRarity, monsterLevel, maxLevel, monsterType, levelLimit);
    }

    public List<Integer> convertTeam(String team) {
        List<String> teamParsed = Arrays.asList(team.split("-"));
        List<Integer> cards = new LinkedList<>();

        for (int i = 0; i < teamParsed.size(); i++) {
            int cardId = Integer.parseInt(teamParsed.get(i));
            cards.add(cardId);
        }

        return cards;
    }

    private int getMaxLevelForSummoner(Integer summonerRarity, Integer summonerLevel, Integer monsterRarity, Integer monsterLevel, Integer maxLevel, MonsterType monsterType, LevelLimit levelLimit) {
        int [][] lookupListCommon = new int[][] {
                {},
                {1, 1, 1, 1},
                {2, 2, 1, 1},
                {3, 2, 2, 1},
                {4, 3, 2, 2},
                {5, 4, 3, 2},
                {6, 5, 4, 2},
                {7, 6, 4, 3},
                {8, 6, 5, 3},
                {9, 7, 5, 4},
                {10, 8, 6, 4}
        };

        int [][] lookupListRare = new int[][] {
                {},
                {1, 1, 1, 1},
                {3, 2, 2, 1},
                {4, 3, 2, 2},
                {5, 4, 3, 2},
                {6, 5, 4, 3},
                {8, 6, 5, 3},
                {9, 7, 5, 4},
                {10, 8, 6, 4}
        };

        int [][] lookupListEpic = new int[][] {
                {},
                {2, 1, 1, 1},
                {3, 3, 2, 1},
                {5, 4, 3, 2},
                {7, 5, 4, 3},
                {8, 7, 5, 3},
                {10, 8, 6, 4}
        };

        int [][] lookupListLeg = new int[][] {
                {},
                {3, 2, 2, 1},
                {5, 4, 3, 2},
                {8, 6, 5, 3},
                {10, 8, 6, 4},
        };

        int[][][] levelLookup = new int[][][] {
                null,
                lookupListCommon,
                lookupListRare,
                lookupListEpic,
                lookupListLeg
        };

        if (levelLimit != null) {
            int summonerMaxWithLevelLimit = LevelLimitMap.getMaxLevelFor(levelLimit, summonerRarity);
            if (summonerLevel > summonerMaxWithLevelLimit) {
                summonerLevel = summonerMaxWithLevelLimit;
            }
        }

        if (monsterType == MonsterType.MONSTER) {
            try {
                maxLevel = levelLookup[summonerRarity][summonerLevel][monsterRarity-1];
            } catch (Exception e) {
                logger.error("Monster Rarity: " + summonerRarity);
                logger.error("Monster Level: " + (summonerLevel - 1));
                logger.error("Monster rarity: " + (monsterRarity-1));
                throw e;
            }
        }
        else {
            maxLevel = levelLookup[2][6][summonerRarity-1];
        }


        if (monsterLevel <= maxLevel) {
            return monsterLevel;
        }
        else {
            return maxLevel;
        }
    }
}
