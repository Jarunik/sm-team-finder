package org.slos.rating;

import org.slos.services.ReflectiveTeamBuilderService;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.splinterlands.domain.monster.MonsterDetailsList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ColorPlacements {
    private ColorType colorType;
    private PlacementRank[][] placementRanks = new PlacementRank[7][ReflectiveTeamBuilderService.MAX_MONSTER_ID];
    private ColorPlacementComparator colorPlacementComparator = new ColorPlacementComparator();

    public ColorPlacements(ColorType colorType) {
        this.colorType = colorType;
    }

    public void addToPlacement(Integer id, Integer placement, Float rank) {
        PlacementRank placementRank = placementRanks[placement][id];

        if (placementRank == null) {
            placementRank = new PlacementRank(id, placement);
        }

        placementRank.addRating(rank);

        placementRanks[placement][id] = placementRank;
    }

    public List<PlacementRank> getSortedPlacemtnRanks() {
        List<PlacementRank> placementRanksSorted = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            placementRanksSorted.addAll(placementRanksAtLocation(i));
        }

        Collections.sort(placementRanksSorted, colorPlacementComparator);

        return placementRanksSorted;
    }

    public Optional<PlacementRank> getHighestRatedExceptFor(Set<Integer> notList) {
        for (PlacementRank placementRank : getSortedPlacemtnRanks()) {
            if (!notList.contains(placementRank.getId())) {
                return Optional.of(placementRank);
            }
        }

        return Optional.empty();
    }

    public Optional<PlacementRank>  getHighestRatedForLocationExceptFor(Integer location, Set<Integer> notList, Integer atManaCost, MonsterDetailsList monsterDetailsList) {
        Set<Integer> notListEnhanced = new HashSet<>(notList);
        PlacementRank prospectivePlacementRank = null;
        do {
            Optional<PlacementRank> maybeIt = getHighestRatedForLocationExceptFor(location, notListEnhanced);

            if (maybeIt.isPresent()) {
                prospectivePlacementRank = maybeIt.get();
            }
            else {
                return Optional.empty();
            }
            notListEnhanced.add(prospectivePlacementRank.getId());
        } while ((prospectivePlacementRank != null) && (monsterDetailsList.getDetails(prospectivePlacementRank.getId()).getMonsterStats().getMana()[0] != atManaCost));

        return Optional.empty();
    }

    public Optional<PlacementRank>  getHighestRatedForLocationExceptFor(Integer location, Set<Integer> notList) {
        for (PlacementRank placementRank : placementRanksAtLocationSorted(location)) {
            if (!notList.contains(placementRank.getId())) {
                return Optional.of(placementRank);
            }
        }

        return Optional.empty();
    }

    public List<PlacementRank> placementRanksAtLocationSorted(Integer location) {
        List<PlacementRank> placementRanksAtLocationSorted = placementRanksAtLocation(location);
        Collections.sort(placementRanksAtLocationSorted, colorPlacementComparator);

        return placementRanksAtLocationSorted;
    }

    public List<PlacementRank> placementRanksAtLocation(Integer location) {
        List<PlacementRank> placementRanksAtLocation = new ArrayList<>();

        for (PlacementRank placementRank : placementRanks[location]) {
            if (placementRank != null) {
                placementRanksAtLocation.add(placementRank);
            }
        }

        return placementRanksAtLocation;
    }

    @Override
    public String toString() {
        return "ColorPlacements{" +
                "colorType=" + colorType +
                ", placementRanks=" + Arrays.toString(placementRanks) +
                '}';
    }
}

class ColorPlacementComparator implements Comparator<PlacementRank> {
    @Override
    public int compare(PlacementRank o1, PlacementRank o2) {
        if (o1.getRating().equals(o2.getRating())) {
            return 0;
        }
        if (o1.getRating() < o2.getRating()) {
            return 1;
        }
        else {
            return -1;
        }
    }
}