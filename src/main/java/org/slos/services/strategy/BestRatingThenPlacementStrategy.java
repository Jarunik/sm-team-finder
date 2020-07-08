package org.slos.services.strategy;

import org.slos.domain.RealizedCollection;
import org.slos.domain.TeamRank;
import org.slos.rating.ColorPlacements;
import org.slos.rating.ColorRank;
import org.slos.rating.ColorVsColorRatingResults;
import org.slos.rating.PlacementRank;
import org.slos.rating.PlacementRankComparator;
import org.slos.rating.RatingContext;
import org.slos.rating.RatingContextFactory;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.splinterlands.domain.monster.MonsterDetailsList;
import org.slos.splinterlands.domain.monster.MonsterStats;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class BestRatingThenPlacementStrategy extends TeamBuildingStrategy {
    @Autowired RatingContextFactory ratingContextFactory;
    @Autowired
    MonsterDetailsList monsterDetailsList;
    private static final Float RANK = 1000f;

    @Override
    public List<TeamRank> getTeamRanks(List<TeamRank> teamRankList, RealizedCollection realizedCollection, Integer manaLimit, Set<ColorType> notColors, ColorVsColorRatingResults colorVsColorRatingResults) {
        RatingContext ratingContext = ratingContextFactory.buildFrom(teamRankList, realizedCollection);
        Map<ColorType, ColorPlacements> placementRanks = ratingContext.getPlacementRanks();
        List<ColorRank> sortedColorRanks = ratingContext.getSortedColorRanks();
        List<TeamRank> generatedTeams = new ArrayList<>();

        for (ColorRank colorRank : sortedColorRanks) {
            ColorType colorType = colorRank.getColorType();

            if ((!notColors.contains(colorType)) && (!colorType.equals(ColorType.GOLD)) && (colorType.equals(ColorType.GRAY))) {

                Optional<TeamRank> newTeam = buildTeamForColor(colorType, realizedCollection, manaLimit, placementRanks);

                if (newTeam.isPresent()) {
                    generatedTeams.add(newTeam.get());
                }
            }
        }
        return generatedTeams;
    }

    public Optional<TeamRank> buildTeamForColor(ColorType colorType, RealizedCollection realizedCollection, Integer manaLimit, Map<ColorType, ColorPlacements> placementRanks) {
        try {
            Integer spentMana = 0;

            List<PlacementRank> selectedPlacements = new ArrayList<>();
            Set<Integer> notMonsters = new HashSet<>();

            List<PlacementRank> placementRanksForColor = placementRanks.get(colorType).placementRanksAtLocationSorted(0);
            if (placementRanksForColor.size() > 0) {
                PlacementRank summoner = placementRanksForColor.get(0);
                MonsterStats summonerDetails = monsterDetailsList.getDetails(summoner.getId()).getMonsterStats();
                spentMana += summonerDetails.getMana()[0];
                selectedPlacements.add(summoner);
                notMonsters.add(summoner.getId());

                PlacementRank tank = placementRanks.get(colorType).placementRanksAtLocationSorted(1).get(0);
                spentMana += monsterDetailsList.getDetails(tank.getId()).getMonsterStats().getMana()[0];
                selectedPlacements.add(tank);
                notMonsters.add(tank.getId());
                PlacementRank[] placements = new PlacementRank[7];

                do {
                    Integer currentManaRemaining = manaLimit - spentMana;
                    Set<PlacementRank> potentialPlacements = new HashSet<>();

                    addPotentialPlacement(currentManaRemaining, placementRanks.get(colorType), notMonsters, placements, potentialPlacements, 3);
                    addPotentialPlacement(currentManaRemaining, placementRanks.get(colorType), notMonsters, placements, potentialPlacements, 4);
                    addPotentialPlacement(currentManaRemaining, placementRanks.get(colorType), notMonsters, placements, potentialPlacements, 5);
                    addPotentialPlacement(currentManaRemaining, placementRanks.get(colorType), notMonsters, placements, potentialPlacements, 6);

                    Optional<PlacementRank> maxOptional = potentialPlacements.stream().max(new PlacementRankComparator());

                    if (maxOptional.isPresent()) {
                        PlacementRank placementRank = maxOptional.get();

                        int location = placementRank.getPlacement();
                        MonsterStats details = monsterDetailsList.getDetails(placementRank.getId()).getMonsterStats();
                        spentMana += details.getMana()[0];
                        placements[location] = placementRank;
                        notMonsters.add(placementRank.getId());
                        selectedPlacements.add(placementRank);
                    } else {
                        break;
                    }

                } while ((spentMana < manaLimit) && (selectedPlacements.size() < 7));

                TeamRank createdTeamRank = adaptToTeamRankFromPlacementRank(realizedCollection, colorType, selectedPlacements, "BestRatingThenPlacementStrategy", RANK);
                return Optional.of(createdTeamRank);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void addPotentialPlacement(Integer manaRemaining, ColorPlacements colorPlacements, Set<Integer> notMonsters, PlacementRank[] placements, Set<PlacementRank> potentialPlacements, Integer loc) {
        if (placements[loc] == null) {
            PlacementRank result = null;
            do {
                Set<Integer> notMonstersForLocation = new HashSet<>(notMonsters);
                Optional<PlacementRank> placementRankOptional3 = colorPlacements.getHighestRatedForLocationExceptFor(loc, notMonstersForLocation);
                if (placementRankOptional3.isPresent()) {
                    PlacementRank potentialPlacement = placementRankOptional3.get();

                    Integer manaCost = monsterDetailsList.getDetails(potentialPlacement.getId()).getMonsterStats().getMana()[0];
                    if (manaCost <= manaRemaining) {
                        potentialPlacements.add(placementRankOptional3.get());
                        result = null;
                    } else {
                        notMonstersForLocation.add(potentialPlacement.getId());
                    }
                } else {
                    result = null;
                }
            } while (result != null);
        }
    }
}
