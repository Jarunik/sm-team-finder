package org.slos.services;

import org.slos.Team;
import org.slos.TeamRequestProcessStep;
import org.slos.domain.TeamRequestContext;
import org.slos.rating.ColorVsColorRatingResults;
import org.slos.rating.ColorVsColorRatingService;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SetTeamsBasedOnColorPerformance implements TeamRequestProcessStep {
    @Value("${defaults.topHandCount}") Integer playerTeamCount;

    Logger logger = LoggerFactory.getLogger(SetTeamsBasedOnColorPerformance.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        List<Team> playerTeams = teamRequestContext.getPlayerTeams();
        ColorVsColorRatingResults colorVsColorRatingResults = ColorVsColorRatingService.rankColorVsColor(teamRequestContext.getColorStats(), teamRequestContext.getTeamRequest().getNotColors());
        Map<ColorType, Float> masterRatios = colorVsColorRatingResults.getColorMasterRatio();
        Float masterTotal = 0f;

        for (ColorType colorType : ColorType.values()) {
            if (!teamRequestContext.getTeamRequest().getNotColors().contains(colorType)) {
                if (masterRatios.get(colorType) != null) {
                    masterTotal += masterRatios.get(colorType);
                }
            }
        }

        Set<Team> colorWeightedTeams = new HashSet<>();
        for (ColorType colorType : ColorType.values()) {
            if (!teamRequestContext.getTeamRequest().getNotColors().contains(colorType)) {
                if (masterRatios.get(colorType) != null) {
                    Integer colorCountTotal = ((int)(masterRatios.get(colorType) / masterTotal * playerTeamCount));
                    colorWeightedTeams.addAll(teamsOfColorForCount(playerTeams, colorType, colorCountTotal));
                }
            }
        }

        if (colorWeightedTeams.size() < playerTeamCount) {
            for (int i = 0; (i < playerTeamCount) && (colorWeightedTeams.size() < playerTeamCount) && (playerTeams.size() < i); i++) {
                colorWeightedTeams.add(playerTeams.get(i));
            }
        }

        teamRequestContext.setPlayerTeams(new ArrayList<>(colorWeightedTeams));

        return teamRequestContext;
    }

    private List<Team> teamsOfColorForCount(List<Team> teams, ColorType colorType, Integer count) {
        List<Team> teamOfColor = teams.stream().filter(team -> team.getColorType().equals(colorType)).collect(Collectors.toList());

        Integer available = teamOfColor.size();
        if (available >= count) {
            return teamOfColor.subList(0, count);
        }

        return teamOfColor;
    }
}
