package org.slos.services;

import org.slos.TeamRequestProcessStep;
import org.slos.domain.TeamRank;
import org.slos.domain.TeamRequestContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class TeamResultsCountFilter implements TeamRequestProcessStep {
    @Value("${defaults.limitResultCount:-1}") Integer limitResultCount;

    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        List<TeamRank> teamRanks = teamRequestContext.getResultTeamRanks();

        Integer maxCount = teamRanks.size();

        if (limitResultCount > 0) {
            maxCount = teamRanks.size() < limitResultCount ? teamRanks.size() : limitResultCount;
        }

        teamRequestContext.setResultTeamRanks(teamRanks.subList(0, maxCount));

        return teamRequestContext;
    }
}
