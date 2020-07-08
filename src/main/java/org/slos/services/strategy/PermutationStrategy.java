package org.slos.services.strategy;

import com.google.common.collect.Sets;
import org.slos.domain.RealizedCollection;
import org.slos.domain.RealizedCollectionCard;
import org.slos.domain.TeamRank;
import org.slos.rating.ColorPlacements;
import org.slos.rating.ColorVsColorRatingResults;
import org.slos.rating.PlacementRank;
import org.slos.rating.RatingContext;
import org.slos.rating.RatingContextFactory;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterDetailsList;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PermutationStrategy extends TeamBuildingStrategy {
    @Autowired RatingContextFactory ratingContextFactory;
    @Autowired
    MonsterDetailsList monsterDetailsList;
    private static final Float RANK = -100f;

    private final Logger logger = LoggerFactory.getLogger(PermutationStrategy.class);

    @Override
    public List<TeamRank> getTeamRanks(List<TeamRank> teamRankList, RealizedCollection realizedCollection, Integer manaLimit, Set<ColorType> notColors, ColorVsColorRatingResults colorVsColorRatingResults) {
        List<TeamRank> newTeamRanks = new ArrayList<>();
        RatingContext ratingContext = ratingContextFactory.buildFrom(teamRankList, realizedCollection);
        Map<ColorType, ColorPlacements> placementRanks = ratingContext.getPlacementRanks();

        ColorType colorType = colorVsColorRatingResults.getHighestColorMasterRatio();

        if (colorType == null) {
            if (!notColors.contains(ColorType.BLUE)) {
                colorType = ColorType.BLUE;
            }
            else if (!notColors.contains(ColorType.WHITE)) {
                colorType = ColorType.WHITE;
            }
            else if (!notColors.contains(ColorType.GREEN)) {
                colorType = ColorType.GREEN;
            }
            else if (!notColors.contains(ColorType.RED)) {
                colorType = ColorType.RED;
            }
            else if (!notColors.contains(ColorType.BLACK)) {
                colorType = ColorType.BLACK;
            }
            else {
                logger.info("No color for permutation formation.");
                return newTeamRanks;
            }
        }

        getPermutationsOfColor(realizedCollection, manaLimit, notColors, colorVsColorRatingResults, newTeamRanks, placementRanks, colorType);

        Collections.shuffle(newTeamRanks);

        return newTeamRanks;
    }

    public void getPermutationsOfColor(RealizedCollection realizedCollection, Integer manaLimit, Set<ColorType> notColors, ColorVsColorRatingResults colorVsColorRatingResults, List<TeamRank> newTeamRanks, Map<ColorType, ColorPlacements> placementRanks, ColorType colorType) {
        try {
            if (!notColors.contains(colorType)) {
                List<PlacementRank> summoners = placementRanks.get(colorType).placementRanksAtLocationSorted(0);

                ColorType secondaryColorType = null;
                if (colorType.equals(ColorType.GOLD)) {
                    Set<ColorType> notGold = new HashSet<>();
                    notGold.add(ColorType.GOLD);
                    secondaryColorType = colorVsColorRatingResults.getHighestColorMasterRatio(notGold);
                }

                logger.info("Finding permutations for colors: " + colorType + secondaryColorType != null ? " - " + secondaryColorType : "");
                Set<Integer> cardsOfColor = getCardsOfColor(realizedCollection, colorType, secondaryColorType);
                Set<RealizedCollectionCard> cards = getHighestCardsOf(realizedCollection, cardsOfColor);

                final ColorType summonerColorType = colorType;
                Set<RealizedCollectionCard> highestSummoners = cards.stream().filter(realizedCollectionCard -> (realizedCollectionCard.getMonsterBattleStats().getType().equals(MonsterType.SUMMONER))&&(realizedCollectionCard.getMonsterDetails().getColor().equals(summonerColorType))).collect(Collectors.toSet());
                Set<Integer> highestSummonersIds = highestSummoners.stream().map(realizedCollectionCard -> realizedCollectionCard.getMonsterDetails().getId()).collect(Collectors.toSet());
                Set<RealizedCollectionCard> highestMonsters = cards.stream().filter(realizedCollectionCard -> realizedCollectionCard.getMonsterBattleStats().getType().equals(MonsterType.MONSTER)).collect(Collectors.toSet());

                RealizedCollectionCard highestRatedSummoner = null;
                for (PlacementRank placementRank : summoners) {
                    if (highestSummonersIds.contains(placementRank.getId())) {
                        Optional<RealizedCollectionCard> possibleHighestSummoner = highestSummoners.stream().filter(realizedCollectionCard -> realizedCollectionCard.getMonsterDetails().getId().equals(placementRank.getId())).findFirst();
                        if (possibleHighestSummoner.isPresent()) {
                            highestRatedSummoner = possibleHighestSummoner.get();
                            break;
                        }
                    }
                }

                if ((highestRatedSummoner == null) && (highestSummoners.size() > 0)) {
                    highestRatedSummoner = new ArrayList<>(highestSummoners).get(0);
                }

                if (highestRatedSummoner != null) {

                    Integer summonerManaCost = highestRatedSummoner.getMonsterDetails().getMonsterStats().getMana()[0];
                    Set<Set<RealizedCollectionCard>>[] possibleCombinations = getCombinationsOf(highestMonsters, 6, manaLimit - summonerManaCost);

                    Set<Set<RealizedCollectionCard>> toProcess = possibleCombinations[0];

                    if (toProcess.size() < 5) {
                        toProcess.addAll(possibleCombinations[1]);
                    }

                    for (Set<RealizedCollectionCard> team : toProcess) {

                        List<RealizedCollectionCard> newTeam = new ArrayList<>();

                        newTeam.add(highestRatedSummoner);
                        newTeam.addAll(setTankInFront(new ArrayList<>(team)));

                        TeamRank createdTeamRank = adaptToTeamRankFromRealizedCollectionCard(realizedCollection, colorType, newTeam, this.getClass().getSimpleName(), RANK);
                        newTeamRanks.add(createdTeamRank);

                        if (newTeamRanks.size() > 200) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            logger.error("Exception making permutation team.", e);
        }
    }

    private List<RealizedCollectionCard> setTankInFront(List<RealizedCollectionCard> team) {
        Optional<RealizedCollectionCard> attackCardOptional = team.stream()
                .filter(realizedCollectionCard -> realizedCollectionCard.getMonsterBattleStats().isOfDamageType(DamageType.ATTACK))
                .sorted(new TankHealthComparator())
                .findFirst();

        if (attackCardOptional.isPresent()) {
            List<RealizedCollectionCard> withTank = new ArrayList<>();
            withTank.add(attackCardOptional.get());
            List<RealizedCollectionCard> teamWithoutTank = new ArrayList<>(team);
            teamWithoutTank.remove(attackCardOptional.get());
            withTank.addAll(teamWithoutTank);

            return withTank;
        }

        return team;
    }

    public Set<RealizedCollectionCard> getHighestCardsOf(RealizedCollection realizedCollection, Set<Integer> cardsOfColor) {
        Set<RealizedCollectionCard> highestCardOfColors = new HashSet<>();
        for (Integer id : cardsOfColor) {
            Optional<RealizedCollectionCard> card = realizedCollection.getHighestLevelCardOf(id);
            if (card.isPresent()) {
                highestCardOfColors.add(card.get());
            }
        }
        return highestCardOfColors;
    }

    public Set<Integer> getCardsOfColor(RealizedCollection realizedCollection, ColorType colorType, ColorType secondaryType) {
        Set<Integer> ofColorType = realizedCollection.getCards().stream().filter(realizedCollectionCard ->
            (realizedCollectionCard.getMonsterDetails().getColor().equals(colorType) ||
            realizedCollectionCard.getMonsterDetails().getColor().equals(ColorType.GRAY))
        ).map(realizedCollectionCard -> realizedCollectionCard.getCollectionCard().getMonsterId()).collect(Collectors.toSet());

        if (secondaryType != null) {
            Set<Integer> ofColorTypeSecondary = realizedCollection.getCards().stream().filter(realizedCollectionCard ->
                    (realizedCollectionCard.getMonsterDetails().getColor().equals(secondaryType))
            ).map(realizedCollectionCard -> realizedCollectionCard.getCollectionCard().getMonsterId()).collect(Collectors.toSet());

            ofColorType.addAll(ofColorTypeSecondary);
        }

        return ofColorType;
    }

    public Set<Set<RealizedCollectionCard>>[] getCombinationsOf(Set<RealizedCollectionCard> cards, int ofSize, int manaLimit) {
        Set<Set<RealizedCollectionCard>>[] results = new Set[2];
        Set<Set<RealizedCollectionCard>> fitWithinMana = new HashSet<>();
        Set<Set<RealizedCollectionCard>> fitBelowMana = new HashSet<>();

        int secondsToFindPermutations = 5;
        Long finishAt = System.currentTimeMillis() * 1000 * secondsToFindPermutations;

        outerLoop:
        for (int i = ofSize; i > 0 && i < cards.size(); i--) {
            Set<Set<RealizedCollectionCard>> combinations = Sets.combinations(cards, i);

            int totalIterations = 100000;
            for (Set<RealizedCollectionCard> team : combinations) {
                if (System.currentTimeMillis() > finishAt) {
                    break outerLoop;
                }
                Integer teamManaCount = team.stream().map(realizedCollectionCard -> realizedCollectionCard.getMonsterDetails().getMonsterStats().getMana()[0]).reduce(0, Integer::sum);

                if (teamManaCount.equals(manaLimit)) {
                    fitWithinMana.add(team);
                }
                else if (teamManaCount < manaLimit){
                    fitBelowMana.add(team);
                }

                if (totalIterations-- <= 0) {
                    break;
                }
            }

            if (fitWithinMana.size() > 15000) {
                break;
            }
        }

        results[0] = fitWithinMana;
        results[1] = fitBelowMana;

        logger.info("Permutations at mana: " + results[0].size());
        logger.info("Permutations below mana: " + results[1].size());

        return results;
    }
}

class TankHealthComparator implements Comparator<RealizedCollectionCard> {
    @Override
    public int compare(RealizedCollectionCard o1, RealizedCollectionCard o2) {
        return o2.getMonsterBattleStats().getHealth().getValue().compareTo(o1.getMonsterBattleStats().getHealth().getValue());
    }
}
