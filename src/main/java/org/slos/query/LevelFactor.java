package org.slos.query;

import java.util.HashSet;
import java.util.Set;

public class LevelFactor {
    private final static Set<Integer>[] summonerLevels = new Set[5];
    private final static Set<Integer> common = new HashSet<>();
    private final static Set<Integer> rare = new HashSet<>();
    private final static Set<Integer> epic = new HashSet<>();
    private final static Set<Integer> legendary = new HashSet<>();
    private final static Integer[] rarityMultiplier = new Integer[] {null, 12, 15, 20, 30};

    static {
        common.add(114);

        rare.add(5);
        rare.add(167);
        rare.add(16);
        rare.add(178);
        rare.add(27);
        rare.add(189);
        rare.add(38);
        rare.add(156);
        rare.add(49);
        rare.add(145);
        rare.add(78);

        epic.add(70);
        epic.add(71);
        epic.add(72);
        epic.add(73);
        epic.add(74);
        epic.add(88);

        legendary.add(110);
        legendary.add(111);
        legendary.add(112);
        legendary.add(113);
        legendary.add(109);
        legendary.add(56);
        legendary.add(130);
        legendary.add(200);
        legendary.add(205);

        summonerLevels[0] = null;
        summonerLevels[1] = common;
        summonerLevels[2] = rare;
        summonerLevels[3] = epic;
        summonerLevels[4] = legendary;
    }

    public static void main(String... args) {
        System.out.println(getDiffValue(130, 3, 130, 4));
    }

    public static Integer getBaseWeight(Integer rarity, Integer level) {
        return rarityMultiplier[rarity] * level;
    }

    public static Float getDiffValue(Integer summonerOneId, Integer summonerOneLevel, Integer summonerTwoId, Integer summonerTwoLevel) {

        Integer baseSummonerOneValue = equalizedSummonerLevel(summonerOneId);
        Integer baseSummonerTwoValue = equalizedSummonerLevel(summonerTwoId);

        Float explodedWeight = (float)(baseSummonerTwoValue * summonerTwoLevel) - (baseSummonerOneValue * summonerOneLevel);
        return ((explodedWeight) / 120) * 10;
    }

    public static Integer equalizedSummonerLevel(Integer summonderId) {

        for (int i = 1; i < 5; i++) {
            if (summonerLevels[i].contains(summonderId)) {
                return rarityMultiplier[i];
            }
        }

        return 0;
    }
}