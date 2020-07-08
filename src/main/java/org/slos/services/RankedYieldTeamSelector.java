package org.slos.services;

import org.slos.AppConfig;
import org.slos.Team;
import org.slos.TeamRequestProcessStep;
import org.slos.domain.TeamRequest;
import org.slos.domain.TeamRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;

public class RankedYieldTeamSelector implements TeamRequestProcessStep {

    @Autowired AppConfig appConfig;
    @Value("${defaults.rankedYieldListActive:false}") Boolean rankedYieldListActive;
    @Value("${defaults.tournamentYieldListActive:false}") Boolean tournamentYieldListActive;

    private final Logger logger = LoggerFactory.getLogger(RankedYieldTeamSelector.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        logger.info("Checking for yield.");
        if (!teamRequestContext.getTeamRequest().getTournamentGame()) {
            manageYielding(teamRequestContext, rankedYieldListActive, appConfig.getRankedYieldList());
        }
        else {
            manageYielding(teamRequestContext, tournamentYieldListActive, appConfig.getTournamentYieldList());
        }

        return teamRequestContext;
    }

    public void manageYielding(TeamRequestContext teamRequestContext, Boolean yield, List<String> yieldList) {
        TeamRequest teamRequest = teamRequestContext.getTeamRequest();
        Boolean isYieldMatch = checkForYieldMatch(teamRequest, yieldList);

        if (yield && isYieldMatch) {
            int topLoc = yieldList.indexOf(teamRequest.getPlayerTop());
            int botLoc = yieldList.indexOf(teamRequest.getPlayerBottom());

            if (topLoc < botLoc) {
                logger.info("Player should win via ranked yeld order.");
            }
            else {
                logger.info("Opponent should win via ranked yield order.");
                teamRequestContext.setShouldYield(true);
                List<Team> playerTeams = teamRequestContext.getPlayerTeams();
                int threeFourths = Double.valueOf(playerTeams.size() * .55).intValue();

                teamRequestContext.setPlayerTeams(Collections.singletonList(playerTeams.get(threeFourths)));
            }
        }
    }

    private Boolean checkForYieldMatch(TeamRequest teamRequest, List<String> yieldList) {

        if (yieldList == null) {
            return false;
        }

        if ((yieldList.contains(teamRequest.getPlayerTop()) && (yieldList.contains(teamRequest.getPlayerBottom())))) {
            logger.info("This is a ranked yielded match.");
            return true;
        }

        return false;
    }
}
