package org.slos.services.strategy;

import org.slos.TeamSuggestionToActualCardsService;
import org.slos.domain.RealizedCollection;
import org.slos.domain.TeamRank;
import org.slos.rating.ColorPlacements;
import org.slos.rating.ColorRank;
import org.slos.rating.ColorVsColorRatingResults;
import org.slos.rating.PlacementRank;
import org.slos.rating.RatingContext;
import org.slos.rating.RatingContextFactory;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.splinterlands.domain.monster.MonsterDetails;
import org.slos.splinterlands.domain.monster.MonsterDetailsList;
import org.slos.splinterlands.domain.monster.MonsterStats;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BestPlacementForColorStrategy extends TeamBuildingStrategy {
    @Autowired RatingContextFactory ratingContextFactory;
    @Autowired
    MonsterDetailsList monsterDetailsList;
    private static final Float RANK = 1000f;

    private final Logger logger = LoggerFactory.getLogger(TeamSuggestionToActualCardsService.class);

    @Override
    public List<TeamRank> getTeamRanks(List<TeamRank> teamRankList, RealizedCollection realizedCollection, Integer manaLimit, Set<ColorType> notColors, ColorVsColorRatingResults colorVsColorRatingResults) {
        RatingContext ratingContext = ratingContextFactory.buildFrom(teamRankList, realizedCollection);
        Map<ColorType, ColorPlacements> placementRanks = ratingContext.getPlacementRanks();
        List<ColorRank> sortedColorRanks = ratingContext.getSortedColorRanks();
        List<TeamRank> generatedTeams = new ArrayList<>();

        for (ColorRank colorRank : sortedColorRanks) {
            if ((colorRank.getColorType().equals(ColorType.GOLD)) || (colorRank.getColorType().equals(ColorType.GRAY))) {
                continue;
            }

            ColorType color = colorRank.getColorType();

            summonerLoop:
            for (int i = 0; i < 3 && i < placementRanks.get(color).placementRanksAtLocationSorted(0).size(); i++) {
                tankLoop:
                for (int j = 0; j < 2; j++) {
                    try {
                        ColorType colorType = colorRank.getColorType();
                        if ((notColors.contains(colorType)) || colorType.equals(ColorType.GRAY)) {
                            continue;
                        }
                        Integer spentMana = 0;

                        List<PlacementRank> selectedPlacements = new ArrayList<>();
                        Set<Integer> notMonsters = new HashSet<>();

                        if (placementRanks.get(color).placementRanksAtLocationSorted(0).get(i) != null) {
                            PlacementRank summoner = placementRanks.get(color).placementRanksAtLocationSorted(0).get(i);
                            MonsterStats summonerDetails = monsterDetailsList.getDetails(summoner.getId()).getMonsterStats();
                            spentMana += summonerDetails.getMana()[0];
                            selectedPlacements.add(summoner);
                            notMonsters.add(summoner.getId());

                            if (placementRanks.get(color).placementRanksAtLocationSorted(1).get(j) != null) {
                                PlacementRank tank = placementRanks.get(color).placementRanksAtLocationSorted(1).get(j);
                                spentMana += monsterDetailsList.getDetails(tank.getId()).getMonsterStats().getMana()[0];
                                selectedPlacements.add(tank);
                                notMonsters.add(tank.getId());


                                Set<Integer> notFirstMonster = new HashSet<>();
                                monsterLoop:
                                for (int k = 0; k < 2; k++) {

                                    Integer location = 2;
                                    do {
                                        Integer currentManaRemainint = manaLimit - spentMana;
                                        Optional<PlacementRank> placementRank = placementRanks.get(color).getHighestRatedForLocationExceptFor(location, notMonsters);

                                        if (placementRank.isPresent()) {
                                            PlacementRank prospectiveMonster = placementRank.get();
                                            MonsterDetails prospectiveDetails = monsterDetailsList.getDetails(prospectiveMonster.getId());
                                            boolean isMonster = prospectiveDetails.getType().equals(MonsterType.MONSTER);

                                            if (isMonster) {
                                                Integer manaCount = prospectiveDetails.getMonsterStats().getMana()[0];
                                                if (manaCount <= currentManaRemainint) {
                                                    if (selectedPlacements.size() < 7) {
                                                        selectedPlacements.add(prospectiveMonster);
                                                        spentMana += manaCount;
                                                    }
                                                    location++;
                                                }
                                            }
                                            if (location == 2) {
                                                if (notFirstMonster.contains(prospectiveMonster.getId())) {
                                                    continue;
                                                }
                                                else {
                                                    notFirstMonster.add(prospectiveMonster.getId());
                                                }
                                            }

                                            notMonsters.add(prospectiveMonster.getId());
                                        } else {
                                            break;
                                        }
                                    } while ((spentMana <= manaLimit) && (location < 7));

//                                    while (selectedPlacements.remove(null));
                                    int totalTemManaPre = 0;
                                    for (PlacementRank placementRank : selectedPlacements) {
                                        if (placementRank != null) {
                                            totalTemManaPre += monsterDetailsList.getDetails(placementRank.getId()).getMonsterStats().getMana()[0];
                                        }
                                    }

                                    Set<Integer> processedLocation  = new HashSet<>();
                                    processedLocation.add(0);
                                    processedLocation.add(1);

                                    setManaToMaxLoop:
                                    do {
                                        int lowestRatedPosition = getLowestRatedPosition(selectedPlacements, processedLocation);
                                        int manaNeededToSpend = manaLimit - spentMana;

                                        PlacementRank replacementPlacementRank = null;
                                        if (lowestRatedPosition == -1) {
                                            break setManaToMaxLoop;
                                        }

                                        getHighestPossibleManaLoop:
                                        for (int z = manaNeededToSpend; z > monsterDetailsList.getDetails(selectedPlacements.get(lowestRatedPosition).getId()).getMonsterStats().getMana()[0]; z--) {
                                            Optional<PlacementRank> placementRank = placementRanks.get(color).getHighestRatedForLocationExceptFor(lowestRatedPosition, notMonsters, manaNeededToSpend, monsterDetailsList);

                                            if (!placementRank.isPresent()) {
                                                continue;
                                            }
                                            replacementPlacementRank = placementRank.get();

                                            spentMana += monsterDetailsList.getDetails(replacementPlacementRank.getId()).getMonsterStats().getMana()[0] - monsterDetailsList.getDetails(selectedPlacements.get(lowestRatedPosition).getId()).getMonsterStats().getMana()[0];
                                        }
                                        selectedPlacements.set(lowestRatedPosition, replacementPlacementRank);

                                    } while (spentMana < manaLimit);

                                    int totalTeamMana = 0;
                                    for (PlacementRank placementRank : selectedPlacements) {
                                        if (placementRank != null) {
                                            totalTeamMana += monsterDetailsList.getDetails(placementRank.getId()).getMonsterStats().getMana()[0];
                                        }
                                    }

                                    if ((totalTeamMana == manaLimit) || (manaLimit == 99)) {
                                        while (selectedPlacements.remove(null));
                                        TeamRank createdTeamRank = adaptToTeamRankFromPlacementRank(realizedCollection, color, selectedPlacements, this.getClass().getSimpleName(), RANK);
                                        generatedTeams.add(createdTeamRank);
                                    }
                                }
                            }
                        }
                    } catch (RuntimeException e) {
                        logger.error("EXCEPTION GENERATING TEAM: " + e);
//                        throw new RuntimeException(e);//TODO Comment me out
                    }
                }
            }
        }
//        generatedTeams.stream().forEach(teamRank -> System.out.println(teamRank));
        return generatedTeams;
    }

    public int getLowestRatedPosition(List<PlacementRank> selectedPlacements, Set<Integer> processedLocation) {
        int lowestRatedPosition = -1;
        int lowestRating = 100000;
        if (selectedPlacements.size() > 7) {
            throw new RuntimeException("Boom: " + selectedPlacements.size());
        }
        for (int x = 2; x < selectedPlacements.size(); x++) {
            if ((selectedPlacements.get(x) != null) && (selectedPlacements.get(x).getRating() < lowestRating) && (!processedLocation.contains(x))) {
                lowestRatedPosition = x;
                processedLocation.add(x);
            }
        }
        return lowestRatedPosition;
    }
}
