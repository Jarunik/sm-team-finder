package org.slos.rating;

import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.util.ToJson;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ColorVsColorRatingResults implements ToJson {
    private Set<ColorType> colors;
    private Integer totalGames;
    private Map<ColorType, Integer> totalGamesOfColor;
    private Map<ColorType, Float> colorTotalRatioScore;
    private Map<String, Integer> colorVsColorWinCount;
    private Map<String, Float> colorVsColorRatioScore;
    private Map<ColorType, Float> colorPercentageOfTotalGames;
    private Map<ColorType, Float> colorMasterRatio;

    public ColorVsColorRatingResults() {}
    public ColorVsColorRatingResults(Set<ColorType> colors, Integer totalGames, Map<ColorType, Integer> totalGamesOfColor, Map<ColorType, Float> colorTotalRatioScore, Map<String, Integer> colorVsColorWinCount, Map<String, Float> colorVsColorRatioScore, Map<ColorType, Float> colorPercentageOfTotalGames, Map<ColorType, Float> colorMasterRatio) {
        this.colors = colors;
        this.totalGames = totalGames;
        this.totalGamesOfColor = totalGamesOfColor;
        this.colorTotalRatioScore = colorTotalRatioScore;
        this.colorVsColorWinCount = colorVsColorWinCount;
        this.colorVsColorRatioScore = colorVsColorRatioScore;
        this.colorPercentageOfTotalGames = colorPercentageOfTotalGames;
        this.colorMasterRatio = colorMasterRatio;
    }

    public ColorType getHighestColorMasterRatio() {
        return getHighestColorMasterRatio(new HashSet<>());
    }

    public ColorType getHighestColorMasterRatio(Set<ColorType> notColors) {
        Float highestRating = 0f;
        ColorType highestColorType = null;

        for (ColorType colorType : getColorMasterRatio().keySet()) {
            if ((!notColors.contains(colorType)) && (getColorMasterRatio().get(colorType) > highestRating)) {
                highestColorType = colorType;
                highestRating = getColorMasterRatio().get(colorType);
            }
        }

        return highestColorType;
    }

    public Set<ColorType> getColors() {
        return colors;
    }

    public void setColors(Set<ColorType> colors) {
        this.colors = colors;
    }

    public Integer getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(Integer totalGames) {
        this.totalGames = totalGames;
    }

    public Map<ColorType, Integer> getTotalGamesOfColor() {
        return totalGamesOfColor;
    }

    public void setTotalGamesOfColor(Map<ColorType, Integer> totalGamesOfColor) {
        this.totalGamesOfColor = totalGamesOfColor;
    }

    public Map<ColorType, Float> getColorTotalRatioScore() {
        return colorTotalRatioScore;
    }

    public void setColorTotalRatioScore(Map<ColorType, Float> colorTotalRatioScore) {
        this.colorTotalRatioScore = colorTotalRatioScore;
    }

    public Map<String, Integer> getColorVsColorWinCount() {
        return colorVsColorWinCount;
    }

    public void setColorVsColorWinCount(Map<String, Integer> colorVsColorWinCount) {
        this.colorVsColorWinCount = colorVsColorWinCount;
    }

    public Map<String, Float> getColorVsColorRatioScore() {
        return colorVsColorRatioScore;
    }

    public void setColorVsColorRatioScore(Map<String, Float> colorVsColorRatioScore) {
        this.colorVsColorRatioScore = colorVsColorRatioScore;
    }

    public Map<ColorType, Float> getColorPercentageOfTotalGames() {
        return colorPercentageOfTotalGames;
    }

    public void setColorPercentageOfTotalGames(Map<ColorType, Float> colorPercentageOfTotalGames) {
        this.colorPercentageOfTotalGames = colorPercentageOfTotalGames;
    }

    public Map<ColorType, Float> getColorMasterRatio() {
        return colorMasterRatio;
    }

    public void setColorMasterRatio(Map<ColorType, Float> colorMasterRatio) {
        this.colorMasterRatio = colorMasterRatio;
    }

    @Override
    public String toString() {
        return toJson();
    }
}
