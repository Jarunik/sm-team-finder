package org.slos.services;

import org.slos.TeamRequestProcessStep;
import org.slos.domain.TeamRank;
import org.slos.domain.TeamRequestContext;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class SpecificStratigySelector implements TeamRequestProcessStep {

    Logger logger = LoggerFactory.getLogger(SpecificStratigySelector.class);
    Float nextTeamRankDifferenceMaximum = .05f;

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {

        Set<String> applyTo = new HashSet<>();
        applyTo.add("jacekw");
        applyTo.add("vcdragon");

        if (applyTo.contains(teamRequestContext.getTeamRequest().getPlayerBottom())) {
            List<TeamRank> teamRanks = teamRequestContext.getResultTeamRanks();
            if (teamRanks.size() > 0) {
                TeamRank topTeam = teamRanks.get(0);
                ColorType topTeamColor = topTeam.getColor();

                if (topTeamColor.equals(ColorType.GOLD)) {
                    topTeamColor = topTeam.getTeam().getSecondaryColor();
                }

                final ColorType comparisonColor = topTeamColor;
                Optional<TeamRank> nextColorTeamOptional = teamRanks.stream()
                        .filter(teamRank ->
                                (teamRank.getTeam().getSecondaryColor() != comparisonColor) && (teamRank.getTeam().getSecondaryColor() != ColorType.GOLD))
                        .findFirst();

                if (nextColorTeamOptional.isPresent()) {
                    TeamRank nextColorTeam = nextColorTeamOptional.get();

                    float value = (nextColorTeam.getWinRatio() / topTeam.getWinRatio());
                    if (value > (1 - nextTeamRankDifferenceMaximum)) {

                        Random random = new Random();
//                        if (random.nextBoolean()) {
                        if (true) {
                            System.out.println(" !!! Randomly choosing alternative color !!! " + (nextColorTeam.getWinRatio() / topTeam.getWinRatio()) + " > " + (1 - nextTeamRankDifferenceMaximum) + " : " + topTeamColor + ((topTeam.getColor().equals(ColorType.GOLD)) ? "(G)":"") + "/" + nextColorTeam.getTeam().getSecondaryColor() + ((nextColorTeam.getColor().equals(ColorType.GOLD)) ? "(G)":""));
                            logger.info(" !!! Randomly choosing alternative color !!! " + (nextColorTeam.getWinRatio() / topTeam.getWinRatio()) + " > " + (1 - nextTeamRankDifferenceMaximum) + " : " + topTeamColor + "/" + nextColorTeam.getColor());
                            teamRanks.remove(nextColorTeam);
                            List<TeamRank> newList = new ArrayList<>();
                            newList.add(nextColorTeam);
                            newList.addAll(teamRanks);

                            teamRequestContext.setResultTeamRanks(newList);
                        }
                    }
                }
            }
        }

        return teamRequestContext;
    }
}
