package org.slos.services.strategy;

import org.slos.Team;
import org.slos.domain.RealizedCollection;
import org.slos.domain.RealizedCollectionCard;
import org.slos.domain.TeamRank;
import org.slos.rating.ColorVsColorRatingResults;
import org.slos.rating.PlacementRank;
import org.slos.splinterlands.domain.monster.ColorType;

import java.util.List;
import java.util.Set;

public abstract class TeamBuildingStrategy {
    public abstract List<TeamRank> getTeamRanks(List<TeamRank> teamRankList, RealizedCollection realizedCollection, Integer manaLimit, Set<ColorType> notColors, ColorVsColorRatingResults colorVsColorRatingResults);

    protected String convertToIdFromPlacementRank(List<PlacementRank> placements, boolean tryToAddChicken) {
        StringBuffer teamId = new StringBuffer();
        Boolean useChicken = tryToAddChicken;

        for (int i = 0; i < placements.size(); i++) {
            teamId.append(placements.get(i).getId());
            if (placements.get(i).getId().equals(131)) {
                useChicken = false;
            }

            if (i+1 < placements.size()) {
                teamId.append("-");
            }
        }

        if (useChicken && placements.size() < 7) {
            teamId.append("-131");
        }

        String team = teamId.toString();

        return team;
    }

    protected String convertToIdFromRealizedCollectionCard(List<RealizedCollectionCard> cards, boolean tryToAddChicken) {
        StringBuffer teamId = new StringBuffer();
        Boolean useChicken = tryToAddChicken;

        for (int i = 0; i < cards.size(); i++) {
            teamId.append(cards.get(i).getMonsterDetails().getId());
            if (cards.get(i).getMonsterDetails().getId().equals(131)) {
                useChicken = false;
            }

            if (i+1 < cards.size()) {
                teamId.append("-");
            }
        }

        if (useChicken && cards.size() < 7) {
            teamId.append("-131");
        }

        String team = teamId.toString();

        return team;
    }

    protected TeamRank adaptToTeamRankFromPlacementRank(RealizedCollection realizedCollection, ColorType color, List<PlacementRank> selectedPlacements, String source, Float rank) {
//        Boolean tryToAddChicken = realizedCollection.hasCard(131);
        Boolean tryToAddChicken = false;
        String id = convertToIdFromPlacementRank(selectedPlacements, tryToAddChicken);
        Integer totalPlayed = 0;
        Integer wins = 0;
        Integer losses = 0;
        Team team = null;

        return new TeamRank(color, id, rank, totalPlayed, wins, losses, source, team);
    }

    protected TeamRank adaptToTeamRankFromRealizedCollectionCard(RealizedCollection realizedCollection, ColorType color, List<RealizedCollectionCard> selectedCards, String source, Float rank) {
//        Boolean tryToAddChicken = realizedCollection.hasCard(131);
        Boolean tryToAddChicken = false;
        String id = convertToIdFromRealizedCollectionCard(selectedCards, tryToAddChicken);
        Integer totalPlayed = 0;
        Integer wins = 0;
        Integer losses = 0;
        Team team = null;

        return new TeamRank(color, id, rank, totalPlayed, wins, losses, source, team);
    }
}
