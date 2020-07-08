package org.slos.rating;

import org.slos.splinterlands.domain.monster.ColorType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ColorVsColorRatingService {

    public static ColorVsColorRatingResults rankColorVsColor(Map<String, Integer> colorVsColorStats, Set<ColorType> notColors) {
        Integer totalGames = 0;
        Map<ColorType, Integer> totalGamesOfColor = new HashMap<>();
        Map<ColorType, Float> colorTotalRatioScore = new HashMap<>();
        Map<String, Integer> colorVsColorWinCount = new HashMap<>(colorVsColorStats);
        Map<String, Float> colorVsColorRatios = new HashMap<>();
        Map<ColorType, Float> colorPercentageOfTotalGames = new HashMap<>();

        Set<String> processedColorVsColors = new HashSet<>();
        Set<ColorType> processedColors = new HashSet<>();

        for (String key : colorVsColorStats.keySet()) {
            if (!processedColorVsColors.contains(key)) {
                String[] colors = key.split("\\|");

                if ((!notColors.contains(ColorType.forColor(colors[0]))) && (!notColors.contains(ColorType.forColor(colors[1])))) {

                    String colorOneKey = key;
                    String colorTwoKey = colors[1] + "|" + colors[0];
                    ColorType colorTypeOne = ColorType.forColor(colors[0]);
                    ColorType colorTypeTwo = ColorType.forColor(colors[1]);

                    processedColors.add(colorTypeOne);
                    processedColors.add(colorTypeTwo);

                    Float colorOneRatio = 1f;
                    Float colorTwoRatio = 1f;

                    if ((colorVsColorStats.get(colorOneKey) != null) && (colorVsColorStats.get(colorTwoKey) != null)) {
                        colorOneRatio = (float) (colorVsColorStats.get(colorOneKey) + 1) / ((float) colorVsColorStats.get(colorTwoKey) + 1);
                        colorTwoRatio = (float) (colorVsColorStats.get(colorTwoKey) + 1) / ((float) colorVsColorStats.get(colorOneKey) + 1);
                    }

                    Integer colorOneTotalWin = colorVsColorStats.get(colorOneKey);
                    Integer colorTwoTotalWin = colorVsColorStats.get(colorTwoKey);

                    if (colorOneTotalWin == null) {
                        colorOneTotalWin = 0;
                    }
                    if (colorTwoTotalWin == null) {
                        colorTwoTotalWin = 0;
                    }

                    colorVsColorRatios.put(colorOneKey, colorOneRatio);
                    colorVsColorRatios.put(colorTwoKey, colorTwoRatio);

                    if (colorTotalRatioScore.get(colorTypeOne) == null) {
                        colorTotalRatioScore.put(colorTypeOne, 0f);
                    }
                    colorTotalRatioScore.put(colorTypeOne, colorTotalRatioScore.get(colorTypeOne) + colorOneRatio);

                    if (totalGamesOfColor.get(colorTypeOne) == null) {
                        totalGamesOfColor.put(colorTypeOne, 0);
                    }
                    totalGamesOfColor.put(colorTypeOne, totalGamesOfColor.get(colorTypeOne) + colorOneTotalWin);

                    totalGames += colorOneTotalWin;

                    if (!colorTypeOne.equals(colorTypeTwo)) {
                        if (totalGamesOfColor.get(colorTypeTwo) == null) {
                            totalGamesOfColor.put(colorTypeTwo, 0);
                        }
                        totalGamesOfColor.put(colorTypeTwo, totalGamesOfColor.get(colorTypeTwo) + colorTwoTotalWin);

                        if (colorTotalRatioScore.get(colorTypeTwo) == null) {
                            colorTotalRatioScore.put(colorTypeTwo, 0f);
                        }
                        colorTotalRatioScore.put(colorTypeTwo, colorTotalRatioScore.get(colorTypeTwo) + colorTwoRatio);

                        totalGames += colorTwoTotalWin;
                    }

                    processedColorVsColors.add(colorOneKey);
                    processedColorVsColors.add(colorTwoKey);
                }
            }
        }

        for (ColorType colorType : processedColors) {
            Float colorPercentageOfTotalGamesValue = (float)totalGamesOfColor.get(colorType) / (float)totalGames;
            colorPercentageOfTotalGames.put(colorType, colorPercentageOfTotalGamesValue);

            colorTotalRatioScore.put(colorType, colorTotalRatioScore.get(colorType) * colorPercentageOfTotalGamesValue);
        }

        Map<ColorType, Float> colorMasterRatio = getMasterRatio(processedColors, colorPercentageOfTotalGames, colorVsColorRatios);

        return new ColorVsColorRatingResults(processedColors, totalGames, totalGamesOfColor, colorTotalRatioScore, colorVsColorWinCount, colorVsColorRatios, colorPercentageOfTotalGames, colorMasterRatio);
    }

    private static Map<ColorType, Float> getMasterRatio(Set<ColorType> processedColors, Map<ColorType, Float> colorPercentageOfTotalGames, Map<String, Float> colorVsColorRatios) {
        Map<ColorType, Float> masterRatio = new HashMap<>();


        for (ColorType colorTypeFirst : processedColors) {
            String colorIdFirst = colorTypeFirst.id();
            masterRatio.put(colorTypeFirst, 0f);

            for (ColorType colorTypeSecond : processedColors) {
                String colorIdSecond = colorTypeSecond.id();

                String lookupId = colorIdFirst + "|" + colorIdSecond;
                Float winRatioVsSecondColor = colorVsColorRatios.get(lookupId);
                Float winPlayPercentageSecondColor = colorPercentageOfTotalGames.get(colorTypeSecond);

                if ((winRatioVsSecondColor != null) && (winPlayPercentageSecondColor != null)) {
                    Float masterValueForSecondColor = winPlayPercentageSecondColor * winRatioVsSecondColor;
                    masterRatio.put(colorTypeFirst, masterRatio.get(colorTypeFirst) + masterValueForSecondColor);
                }
            }
        }

        return masterRatio;
    }
}
