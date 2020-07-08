package org.slos.services;

import org.slos.Team;
import org.slos.domain.TeamRank;
import org.slos.domain.TeamRequestContext;
import org.slos.domain.TeamResponse;
import org.slos.rating.ColorVsColorRatingResults;
import org.slos.rating.ColorVsColorRatingService;
import org.slos.splinterlands.TeamInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.Transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamResponseTransformer {
    @Value("${defaults.limitResultCount}") Integer limitResultCount;

    private final Logger logger = LoggerFactory.getLogger(TeamResponseTransformer.class);

    @Transformer
    public TeamResponse transform(TeamRequestContext teamRequestContext) {
        List<TeamRank> teamRanks = teamRequestContext.getResultTeamRanks();
        Boolean returnCollectionIds = teamRequestContext.getTeamRequest().getSendCollectionTeam();

        if (teamRanks.size() == 0) {
            if (teamRequestContext.getPlayerTeams().size() > 0) {
            Team team = teamRequestContext.getPlayerTeams().get(0);
            TeamRank teamRank = new TeamRank(team.getColorType(), team.getId(), team.getRank(), 0, 0, 0, "", team);
            teamRanks = Collections.singletonList(teamRank);
            logger.info("Setting to player suggested due to no result from rank: " + team.getId());
            }
        }
        else if (teamRanks.get(0) != null) {
            logger.info("");
            //TODO: Add back in later
            if (!teamRanks.get(0).getSource().equals(TeamInfoService.TEAM_INFO_SERVICE_SOURCE)) {
                logger.info(" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                logger.info("                             Using team of source: " + teamRanks.get(0).getSource());
                logger.info(" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
                System.out.println(" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("       Using team of source[" + teamRequestContext.getTeamRequest().getPlayerTop() + "]: " + teamRanks.get(0).getSource());
                System.out.println(" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
            }
            logger.info("========= Returning: " + teamRanks.get(0).getTeam().toMinimalId() + " (" + teamRanks.get(0).getWins() + "/" + teamRanks.get(0).getTotalPlayed() + " - " + teamRanks.get(0).getRank() + ") Source: " + teamRanks.get(0).getSource() + "  - For request: " + teamRequestContext.getTeamRequest().toJson());
            logger.info("");
        }
        else {
            if ((teamRequestContext.getPlayerSuggestedTeams() != null) && (teamRequestContext.getPlayerSuggestedTeams().size() > 0) && (teamRequestContext.getPlayerSuggestedTeams().get(0) != null)) {
                teamRanks = new ArrayList<>(teamRequestContext.getPlayerSuggestedTeams().subList(0, 1));
                logger.info("========= Returning: Raw from query results - " + teamRanks.get(0).getId());
                System.out.println("========= Returning: Raw from query results - " + teamRanks.get(0).getId());
            }
            else {
                logger.info("========= Returning: No team found  - For request: " + teamRequestContext.getTeamRequest().toJson());
                System.out.println("========= Returning:  No team found  - For request: " + teamRequestContext.getTeamRequest().toJson());
            }
        }

        ColorVsColorRatingResults colorVsColorRatingResults = ColorVsColorRatingService.rankColorVsColor(teamRequestContext.getColorStats(), teamRequestContext.getTeamRequest().getNotColors());
        TeamResponse teamResponse = new TeamResponse(teamRanks, returnCollectionIds, colorVsColorRatingResults, limitResultCount);

        try {
            logger.info("Raw response: " + teamResponse.toString());
        } catch (Exception e) {}

        return teamResponse;
    }
}
