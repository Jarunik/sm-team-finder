package org.slos.services;

import org.slos.Team;
import org.slos.TeamRequestProcessStep;
import org.slos.domain.RankingReport;
import org.slos.domain.TeamRequestContext;
import org.slos.ranking.RankingService;
import org.slos.rating.ColorVsColorRatingResults;
import org.slos.rating.ColorVsColorRatingService;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SuggestedTeamsSimulationService implements TeamRequestProcessStep {
    @Autowired private RankingService rankingService;
    @Value("${defaults.maxAllotedTimeForRequest}") Integer maxAllotedTimeForRequest;

    private final Logger logger = LoggerFactory.getLogger(SuggestedTeamsSimulationService.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        Long teamRequestTimeout = teamRequestContext.getTeamRequestTimeout();
        Long timeToAllowForSecondPass = (((long)(maxAllotedTimeForRequest * .05f)) * 1000);
        teamRequestTimeout = teamRequestTimeout - timeToAllowForSecondPass;

        logger.info("Time to allow for 2nd pass: " + timeToAllowForSecondPass);

        List<Team> playerTeams = teamRequestContext.getPlayerTeams();
        List<Team> opponentTeams = teamRequestContext.getOpponentTeams();

        ColorVsColorRatingResults colorVsColorRatingResults = ColorVsColorRatingService.rankColorVsColor(teamRequestContext.getColorStats(), teamRequestContext.getTeamRequest().getNotColors());
        logger.info("Color stats -");
        logger.info("Master ratio: " + colorVsColorRatingResults.getColorMasterRatio());
        logger.info("Play ratio: " + colorVsColorRatingResults.getColorPercentageOfTotalGames());

        RankingReport rankingReport = rankTeams(playerTeams, opponentTeams, teamRequestContext, teamRequestTimeout, teamRequestContext.getTeamRequest().getPlayerTop());

        teamRequestContext.setRankingReport(rankingReport);

        return teamRequestContext;
    }

    private RankingReport rankTeams(List<Team> topTeams, List<Team> bottomTeams, TeamRequestContext teamRequestContext, long stopGamesAt, String teamsFor) {
        List<Float> topTeamRanks = topTeams.stream().map(team -> team.getRank()).collect(Collectors.toList());

        logStats(topTeams, bottomTeams, topTeamRanks);

        Set<GameRuleType> gameRules = teamRequestContext.getTeamRequest().getRuleset();

        RankingReport rankingReport = rankingService.getBestDeckFor(topTeams, bottomTeams, gameRules, teamRequestContext.getBattlesPerMatchup(), stopGamesAt, teamsFor);
        rankingReport.setTeamRequestContext(teamRequestContext);

        return rankingReport;
    }

    private void logStats(List<Team> topTeams, List<Team> bottomTeams, List<Float> topTeamRanks) {
        logColorStats(topTeams, "Top Teams");
        logColorStats(bottomTeams, "Bottom Teams");
        if (topTeamRanks.size() > 10) {
            logger.info("Running top team ranks of: " + topTeamRanks.subList(0, 10) + "...");
        }
        else {
            logger.info("Running top team ranks of: " + topTeamRanks);
        }
    }

    private void logColorStats(List<Team> teams, String identifier) {
        Map<ColorType, Integer> colorCountMap2 = new HashMap<>();
        teams.stream().forEach(team -> {
            if (colorCountMap2.get(team.getColorType()) == null) {
                colorCountMap2.put(team.getColorType(), 0);
            }
            colorCountMap2.put(team.getColorType(), colorCountMap2.get(team.getColorType()) + 1);
        });

        logger.info("Team stats[" + identifier + "]: " + colorCountMap2);
    }
}
