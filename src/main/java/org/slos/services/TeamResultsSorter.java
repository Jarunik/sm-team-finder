package org.slos.services;

import org.slos.Team;
import org.slos.TeamRequestProcessStep;
import org.slos.domain.RankingReport;
import org.slos.domain.TeamRank;
import org.slos.domain.TeamRequestContext;
import org.slos.ranking.TeamStats;
import org.slos.rating.ColorVsColorRatingResults;
import org.slos.rating.ColorVsColorRatingService;
import org.slos.splinterlands.RunSimilarTeams;
import org.slos.splinterlands.TeamInfoService;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TeamResultsSorter  implements TeamRequestProcessStep {

    private final Logger logger = LoggerFactory.getLogger(TeamResultsSorter.class);

    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        RankingReport rankingReport = teamRequestContext.getRankingReport();
        List<TeamRank> teamRanks = new ArrayList<>();

        //Some reason need to interact with the object, otherwise later team will return as null
//        System.out.println("rankingReport.getRanks().values().size(): " + rankingReport.getRanks().values().size());

        for (TeamStats teamStats : rankingReport.getRanks().values()) {
            teamRanks.add(adaptToTeamRank(teamStats));
        }

        ColorVsColorRatingResults colorVsColorRatingResults = ColorVsColorRatingService.rankColorVsColor(teamRequestContext.getColorStats(), teamRequestContext.getTeamRequest().getNotColors());
        Collections.sort(teamRanks, new TeamRankComparator2(colorVsColorRatingResults));
        teamRequestContext.setResultTeamRanks(teamRanks);

//        System.out.println("Team Color Stats: " + teamRequestContext.getColorStats());
//        logger.info("Opponent History: " + teamRequestContext.getOpponentHistory());
//        System.out.println("Color vs Color ratios: " + colorVsColorRatingResults);

//        System.out.println("Team ranks: " + teamRequestContext.getResultTeamRanks());
//        System.out.println("First rank: " + teamRequestContext.getResultTeamRanks().get(0));

        if ((teamRequestContext.getResultTeamRanks() != null) && (teamRequestContext.getResultTeamRanks().size() > 0) && (teamRequestContext.getResultTeamRanks().get(0) != null)) {
            logger.info("Current top team rank[" + teamRequestContext.getResultTeamRanks().get(0).getWinRatio() + "]: " + teamRequestContext.getResultTeamRanks().get(0));

//            teamRequestContext.getResultTeamRanks().stream().forEach(teamRank -> System.out.println(teamRank.getWins() + " - " + teamRank.getWinRatio() + " - " + teamRank.getRank() + " - " + teamRank.getColor()));
        }
        else {
            logger.info("No current top team possible. ");
        }

        return teamRequestContext;
    }

    private TeamRank adaptToTeamRank(TeamStats teamStats) {
        ColorType colorType = teamStats.getTeam().getColorType();
        String id = teamStats.getTeam().getId();
        Float rank = teamStats.getTeam().getRank();//Float.valueOf(teamStats.getTotalWins());
//        Float rank = Float.valueOf(teamStats.getTotalWins());

        Integer totalPlayed = teamStats.getTotalDraws() + teamStats.getTotalLosses() + teamStats.getTotalWins() + teamStats.getErrorCount();
        Integer wins = teamStats.getTotalWins();
        Integer losses = totalPlayed - wins - teamStats.getTotalDraws();
        Team team = teamStats.getTeam();

        TeamRank teamRank = new TeamRank(colorType, id, rank, totalPlayed, wins, losses, team);

        return teamRank;
    }
}

class TeamRankComparator2 implements Comparator<TeamRank> {
    private ColorVsColorRatingResults colorVsColorRatingResults;

    TeamRankComparator2() {colorVsColorRatingResults = null;}
    TeamRankComparator2(ColorVsColorRatingResults colorVsColorRatingResults) {
        this.colorVsColorRatingResults = colorVsColorRatingResults;
    }

    @Override
    public int compare(TeamRank o1, TeamRank o2) {
        Integer o1Wins = o1.getWins();
        Integer o2Wins = o2.getWins();

//        if (colorVsColorRatingResults != null) {
//            Map<ColorType, Float> colorScores = colorVsColorRatingResults.getColorMasterRatio();
//            ColorType bestColorType = null;
//            Float highestSoFar = 0f;
//
//            for (ColorType colorType : colorScores.keySet()) {
//                if (colorScores.get(colorType) > highestSoFar) {
//                    highestSoFar = colorScores.get(colorType);
//                    bestColorType = colorType;
//                }
//            }
//
//
//            if (o1.getColor().equals(bestColorType)) {
//                o1Wins = (int) (o1Wins * 1.1);
//            }
//            if (o2.getColor().equals(bestColorType)) {
//                o2Wins = (int) (o2Wins * 1.1);
//            }

//            if (o1.getColor().equals(ColorType.GOLD)) {
//                o1Wins = (int) (o1Wins * colorVsColorRatingResults.getColorMasterRatio().get(o1.getColor()));
//                o2Wins = (int) (o2Wins * colorVsColorRatingResults.getColorMasterRatio().get(o2.getColor()));
//            }
//        }

        if (!o1.getSource().equals(TeamInfoService.TEAM_INFO_SERVICE_SOURCE)) {
            o1Wins = (int)(o1Wins * .95);
        }
        if (!o2.getSource().equals(TeamInfoService.TEAM_INFO_SERVICE_SOURCE)) {
            o2Wins = (int)(o2Wins * .95);
        }

        int firstCompare = o2Wins.compareTo(o1Wins);

        if (firstCompare != 0) {
            return firstCompare;
        }

        Float o12ndCompare = o1.getRank();
        Float o22ndCompare = o2.getRank();

        try {
            if ((colorVsColorRatingResults != null) && (colorVsColorRatingResults.getColorMasterRatio() != null)) {
                o12ndCompare = (o1Wins * colorVsColorRatingResults.getColorMasterRatio().get(o1.getColor()));
                o22ndCompare = (o2Wins * colorVsColorRatingResults.getColorMasterRatio().get(o2.getColor()));
            }
        } catch (Exception e) {}

        int secondCompare =  o22ndCompare.compareTo(o12ndCompare);

        if (secondCompare != 0) {
            return secondCompare;
        }

        return o2.getRank().compareTo(o1.getRank());
    }

    public static void main(String... args) {
        List<TeamRank> test = new ArrayList<>();

        Team team2 = new Team("id", null, null, null, TeamInfoService.TEAM_INFO_SERVICE_SOURCE);
        Team team = new Team("id", null, null, null, RunSimilarTeams.SIMILAR_TEAMS);

        TeamRank teamRank4 = new TeamRank(ColorType.GOLD, "7th", 99f, 100, 0, 100, team2);
        TeamRank teamRank3 = new TeamRank(ColorType.GOLD, "2nd", 20f, 100, 50, 50, team2);
        TeamRank teamRank5 = new TeamRank(ColorType.GOLD, "5th", 25f, 100, 50, 50, team);
        TeamRank teamRank1 = new TeamRank(ColorType.GOLD, "6th", 15f, 100, 50, 50, team);
        TeamRank teamRank6 = new TeamRank(ColorType.GOLD, "4th", 1f, 100, 50, 50, team2);
        TeamRank teamRank7 = new TeamRank(ColorType.GOLD, "3rd", 10f, 100, 50, 50, team2);
        TeamRank teamRank2 = new TeamRank(ColorType.GOLD, "1st", 1f, 100, 100, 0, team2);

        test.add(teamRank1);
        test.add(teamRank2);
        test.add(teamRank3);
        test.add(teamRank4);
        test.add(teamRank5);
        test.add(teamRank6);
        test.add(teamRank7);

        test.stream().map(teamRank -> teamRank.getId() + "|" + teamRank.getSource()).forEach(id -> System.out.print(" - " + id));
        System.out.println();
        Collections.sort(test, new TeamRankComparator2());
        test.stream().map(teamRank -> teamRank.getId() + "|" + teamRank.getWins() + "|" + teamRank.getSource()).forEach(id -> System.out.print(" - " + id));
    }
}