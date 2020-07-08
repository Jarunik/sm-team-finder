package org.slos.rating;

import java.util.Comparator;

public class PlacementRankComparator implements Comparator<PlacementRank> {
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