package org.slos.services;

import org.slos.TeamRequestProcessStep;
import org.slos.domain.TeamRank;
import org.slos.domain.TeamRequestContext;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SuggestedTeamsFilter implements TeamRequestProcessStep {

    Logger logger = LoggerFactory.getLogger(SuggestedTeamsFilter.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        Set<ColorType> disallowedColors = teamRequestContext.getTeamRequest().getNotColors();

        List<TeamRank> filteredPlayerTeamRanks = filterForColor(disallowedColors, teamRequestContext.getPlayerSuggestedTeams());
        List<TeamRank> filteredOpponentTeamRanks = filterForColor(disallowedColors, teamRequestContext.getOpponentSuggestedTeams());

        filteredPlayerTeamRanks = filterEmptyMonsters(filteredPlayerTeamRanks);
        filteredOpponentTeamRanks = filterEmptyMonsters(filteredOpponentTeamRanks);

        teamRequestContext.setPlayerSuggestedTeams(filteredPlayerTeamRanks);
        teamRequestContext.setOpponentSuggestedTeams(filteredOpponentTeamRanks);

        logger.info("Team count from query after filtering: " + filteredPlayerTeamRanks.size());

        return teamRequestContext;
    }

    private List<TeamRank> filterEmptyMonsters(List<TeamRank> filteredPlayerTeamRanks) {
        return filteredPlayerTeamRanks.stream().filter(teamRank -> teamRank.getId().split("-").length > 1).collect(Collectors.toList());
    }

    private List<TeamRank> filterForColor(Set<ColorType> disallowedColors, List<TeamRank> playerSuggestedTeams) {
        return playerSuggestedTeams.stream().filter(teamRank -> !disallowedColors.contains(teamRank.getColor())).collect(Collectors.toList());
    }
}
