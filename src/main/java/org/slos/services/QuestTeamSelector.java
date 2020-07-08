package org.slos.services;

import org.slos.TeamRequestProcessStep;
import org.slos.domain.TeamRank;
import org.slos.domain.TeamRequestContext;
import org.slos.splinterlands.domain.QuestMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class QuestTeamSelector implements TeamRequestProcessStep {
    @Value("${defaults.questComparisonThreshold}") private Float questComparisonThreshold;

    private final Logger logger = LoggerFactory.getLogger(QuestTeamSelector.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        if ((teamRequestContext.getResultTeamRanks() == null) || (teamRequestContext.getResultTeamRanks().size() == 0) || (teamRequestContext.getResultTeamRanks().get(0) == null)) {
            return teamRequestContext;
        }

        QuestMode questMode = teamRequestContext.getTeamRequest().getQuestMode();

        if (questMode != null) {
            List<TeamRank> resultingTeams = teamRequestContext.getResultTeamRanks();

            if (questMode.color() != null) {
                Optional<TeamRank> bestQuestTeam = resultingTeams.stream().filter(teamRank -> teamRank.getColor().equals(questMode.color())).findFirst();

                if (bestQuestTeam.isPresent()) {
                    Integer bestTeamWins = bestQuestTeam.get().getWins();
                    Integer topTeamWins = teamRequestContext.getResultTeamRanks().get(0).getWins();

                    Float comparison = ((float)bestTeamWins / (float)topTeamWins);

                    if (comparison > (questComparisonThreshold) ) {
                        logger.info("Selecting quest team: " + bestQuestTeam.get() + "  --  instead of: " + topTeamWins);
                        teamRequestContext.setResultTeamRanks(Collections.singletonList(bestQuestTeam.get()));
                    }
                }
            }
        }

        return teamRequestContext;
    }
}
