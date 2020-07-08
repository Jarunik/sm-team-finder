package org.slos.services;

import org.slos.Team;
import org.slos.TeamRequestProcessStep;
import org.slos.domain.TeamRequestContext;

import java.util.Collections;
import java.util.Comparator;

public class TeamByRankSorter implements TeamRequestProcessStep {
    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        Collections.sort(teamRequestContext.getPlayerTeams(), new TeamRankComparator());
        Collections.sort(teamRequestContext.getOpponentTeams(), new TeamRankComparator());

        return teamRequestContext;
    }
}

class TeamRankComparator implements Comparator<Team> {

    @Override
    public int compare(Team o1, Team o2) {
        if ((o1.getRank() == null) || (o2.getRank() == null)) {
            return 0;
        }

        return o2.getRank().compareTo(o1.getRank());
    }
}
