package org.slos.rating;

import org.slos.splinterlands.domain.monster.ColorType;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RatingContext {
    private Map<ColorType, ColorPlacements> placementRanks = new HashMap<>();
    private Map<ColorType, ColorRank> colorRanks = new HashMap<>();
    private Map<Integer, MonsterRank> monsterRanks = new HashMap<>();

    public Map<ColorType, ColorPlacements> getPlacementRanks() {
        return placementRanks;
    }

    public Map<ColorType, ColorRank> getColorRanks() {
        return colorRanks;
    }

    public Map<Integer, MonsterRank> getMonsterRanks() {
        return monsterRanks;
    }

    public List<ColorRank> getSortedColorRanks() {
        List<ColorRank> colorRanksSorted = colorRanks.values().stream().sorted(new ColorRankComparator()).collect(Collectors.toList());

        return colorRanksSorted;
    }

    @Override
    public String toString() {
        return "RatingContext{" +
                "placementRanks=" + placementRanks +
                ", colorRanks=" + colorRanks +
                ", monsterRanks=" + monsterRanks +
                '}';
    }
}

class ColorRankComparator implements Comparator<ColorRank> {
    @Override
    public int compare(ColorRank o1, ColorRank o2) {
        if (o1.getColorAverage().equals(o2.getColorAverage())) {
            return 0;
        }
        if (o1.getColorAverage() < o2.getColorAverage()) {
            return 1;
        }
        else {
            return -1;
        }
    }
}