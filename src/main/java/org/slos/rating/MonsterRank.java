package org.slos.rating;

public class MonsterRank {
    private Integer id;
    private Float totalRank = 0f;
    private Integer totalTeamCount = 0;

    public MonsterRank(Integer id) {
        this.id = id;
    }

    public void addTeamRank(Float rank) {
        totalTeamCount++;
        totalRank += rank;
    }

    public Float getMonsterAverage() {
        return totalRank / (totalTeamCount + 2);
    }

    @Override
    public String toString() {
        return "MonsterRank{" +
                "id=" + id +
                ", ratingAverage=" + getMonsterAverage() +
                ", totalRank=" + totalRank +
                ", totalTeamCount=" + totalTeamCount +
                '}';
    }
}