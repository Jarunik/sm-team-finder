package org.slos.rating;

import org.slos.domain.RealizedCollection;
import org.slos.domain.TeamRank;
import org.slos.splinterlands.domain.monster.ColorType;

import java.util.List;
import java.util.Map;

public class RatingContextFactory {
    public static final Integer MAX_MONSTER_ID = 500;

    public RatingContext buildFrom(List<TeamRank> teamRankList, RealizedCollection realizedCollection) {
        RatingContext ratingContext = new RatingContext();
        Map<ColorType, ColorPlacements> placementRanks = ratingContext.getPlacementRanks();

        Map<ColorType, ColorRank> colorRanks = ratingContext.getColorRanks();
        Map<Integer, MonsterRank> monsterRanks = ratingContext.getMonsterRanks();

        for (ColorType colorType : ColorType.values()) {
            ColorPlacements colorPlacements = new ColorPlacements(colorType);
            placementRanks.put(colorType, colorPlacements);
            ColorRank colorRank = new ColorRank(colorType);
            colorRanks.put(colorType, colorRank);
        }

        for (int i = 0; i < MAX_MONSTER_ID; i++) {
            monsterRanks.put(i, new MonsterRank(i));
        }

        for (TeamRank teamRank : teamRankList) {
            String[] ids = teamRank.getId().split("-");


            for (int i = 0; i < ids.length; i++) {
                int intId = Integer.parseInt(ids[i]);

                if (realizedCollection.hasCard(intId)) {
                    ColorType teamColor = teamRank.getColor();
                    ColorPlacements colorPlacements = placementRanks.get(teamColor);
                    colorPlacements.addToPlacement(intId, i, teamRank.getRank());
                }

                MonsterRank monsterRank = monsterRanks.get(intId);
                monsterRank.addTeamRank(teamRank.getRank());
            }

            ColorRank colorRank = colorRanks.get(teamRank.getColor());
            colorRank.addTeamRank(teamRank.getRank());
        }

        return ratingContext;
    }
}
