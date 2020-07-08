package org.slos.rating;

public class PlacementRank {
    private Integer placement;
    private Integer id;
    private Float rank = 0f;
    private Integer usedCount = 0;

    public PlacementRank(Integer id, Integer placement) {
        this.placement = placement;
        this.id = id;
    }

    public void addRating(Float rating) {
        rank += rating;
        usedCount++;
    }

    public Integer getPlacement() {
        return placement;
    }

    public Integer getId() {
        return id;
    }

    public Float getRating() {
        Float rating = rank / (usedCount + 2);

        if (rating.equals(Float.NaN)) {
            return 0f;
        }

        return rating;
    }

    @Override
    public String toString() {
        return "PlacementRank{" +
                "placement=" + placement +
                ", id=" + id +
                ", rank=" + rank +
                ", usedCount=" + usedCount +
                '}';
    }
}
