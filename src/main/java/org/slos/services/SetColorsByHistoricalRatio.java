package org.slos.services;

import org.slos.Team;
import org.slos.TeamRequestProcessStep;
import org.slos.domain.TeamRequestContext;
import org.slos.rating.ColorVsColorRatingResults;
import org.slos.rating.ColorVsColorRatingService;
import org.slos.splinterlands.domain.monster.ColorType;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SetColorsByHistoricalRatio implements TeamRequestProcessStep {
    @Value("${defaults.bottomHandCount}") Integer opponentTeamCount;

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        List<Team> opponentTeams =  teamRequestContext.getOpponentTeams();

        if (opponentTeams.size() < opponentTeamCount) {
            return teamRequestContext;
        }

        Set<ColorType> notColors = teamRequestContext.getTeamRequest().getNotColors();
        ColorVsColorRatingResults colorVsColorRatingResults = ColorVsColorRatingService.rankColorVsColor(teamRequestContext.getColorStats(), notColors);
        List<Team> teamsOfColorLimit = new ArrayList<>();
        for (ColorType colorType : ColorType.values()) {
            if ((!notColors.contains(colorType)) && (!colorType.equals(ColorType.GRAY))) {
                Integer neededOfColor = (int)(opponentTeamCount * colorVsColorRatingResults.getColorPercentageOfTotalGames().get(colorType));
                List<Team> teamsOfColor = teamsOfColorForCount(opponentTeams, colorType, neededOfColor);
                teamsOfColorLimit.addAll(teamsOfColor);
            }
        }

        if (teamsOfColorLimit.size() >= (opponentTeamCount * .9)) {
            teamRequestContext.setOpponentTeams(teamsOfColorLimit);
        }

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
