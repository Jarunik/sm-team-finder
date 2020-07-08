package org.slos.services;

import org.slos.Team;
import org.slos.TeamRequestProcessStep;
import org.slos.domain.TeamRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeamCountFilter implements TeamRequestProcessStep {
    @Value("${defaults.topHandCount}") Integer playerTeamCount;
    @Value("${defaults.bottomHandCount}") Integer opponentTeamCount;

    Logger logger = LoggerFactory.getLogger(TeamCountFilter.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        List<Team> playerTeams = teamRequestContext.getPlayerTeams();
        List<Team> opponentTeams = teamRequestContext.getOpponentTeams();

        Set<String> teamIds = new HashSet<>();

        if (playerTeams.size() < playerTeamCount) {
            logger.info("Too few player teams: " + playerTeams.size());

            if ((teamRequestContext.getAdditionalPlayerTeams() != null) && (teamRequestContext.getAdditionalPlayerTeams().size() > 0)) {
                for (Team additionalTeam : teamRequestContext.getAdditionalPlayerTeams()) {
                    if (!teamIds.contains(additionalTeam.getId())) {
                        playerTeams.add(additionalTeam);
                        teamIds.add(additionalTeam.getId());
                    }
                }
                logger.info("Team size with additional teams: " + playerTeams.size());
            }
            else {
                logger.info("No backup teams to add.");
            }
        }

        if (opponentTeams.size() < opponentTeamCount) {
            logger.info("Too few opponent teams: " + opponentTeams.size());

            if ((teamRequestContext.getAdditionalPlayerTeams() != null) && (teamRequestContext.getAdditionalPlayerTeams().size() > 0)) {
                for (Team additionalTeam : teamRequestContext.getAdditionalPlayerTeams()) {
                    opponentTeams.add(additionalTeam);
                }
                logger.info("Team size with additional teams: " + playerTeams.size());
            }
            else {
                logger.info("No backup teams to add.");
            }
        }

        Integer playerTeamMax = playerTeams.size() < playerTeamCount ? playerTeams.size() : playerTeamCount;
        Integer opponetTeamMax = opponentTeams.size() < opponentTeamCount ? opponentTeams.size() : opponentTeamCount;

        teamRequestContext.setPlayerTeams(playerTeams.subList(0, playerTeamMax));
        teamRequestContext.setOpponentTeams(opponentTeams.subList(0, opponetTeamMax));

        return teamRequestContext;
    }
}
