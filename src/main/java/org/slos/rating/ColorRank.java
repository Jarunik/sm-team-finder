package org.slos.rating;

import org.slos.splinterlands.domain.monster.ColorType;

public class ColorRank {
    private ColorType colorType;
    private Float totalRank = 0f;
    private Integer totalTeamCount = 0;

    public ColorRank(ColorType colorType) {
        this.colorType = colorType;
    }

    public ColorType getColorType() {
        return colorType;
    }

    public void addTeamRank(Float rank) {
        totalTeamCount++;
        totalRank += rank;
    }

    public Float getColorAverage() {
        Float average = totalRank / totalTeamCount;

        if (average.equals(Float.NaN)) {
            return 0f;
        }

        return average;
    }

    @Override
    public String toString() {
        return "ColorRank{" +
                "colorType=" + colorType +
                ", averageRank=" + getColorAverage() +
                ", totalRank=" + totalRank +
                ", totalTeamCount=" + totalTeamCount +
                '}';
    }
}