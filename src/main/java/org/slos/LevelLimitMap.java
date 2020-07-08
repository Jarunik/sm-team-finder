package org.slos;

import org.slos.splinterlands.domain.LevelLimit;

public class LevelLimitMap {

    public static int getMaxLevelFor(LevelLimit levelLimit, int rarity) {
        return limits[levelLimit.getId()][rarity-1];
    }

    public static final int[][] limits = {
            {1, 1, 1, 1}, //Novice
            {3, 2, 2, 1}, //Bronze
            {5, 4, 3, 2}, //Silver
            {8, 6, 5, 3}, //Gold
            {10, 8, 6, 4}, //Diamond
            {10, 8, 6, 4}  //Champion
    };
}
