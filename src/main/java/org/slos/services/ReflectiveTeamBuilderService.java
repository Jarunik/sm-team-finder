package org.slos.services;

import org.slos.Team;
import org.slos.TeamRequestProcessStep;
import org.slos.TeamSuggestionToActualCardsService;
import org.slos.domain.RealizedCollection;
import org.slos.domain.TeamRank;
import org.slos.domain.TeamRequestContext;
import org.slos.rating.ColorVsColorRatingResults;
import org.slos.rating.ColorVsColorRatingService;
import org.slos.rating.RatingContextFactory;
import org.slos.services.strategy.TeamBuildingStrategy;
import org.slos.splinterlands.domain.LevelLimit;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.splinterlands.domain.monster.MonsterDetailsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReflectiveTeamBuilderService implements TeamRequestProcessStep {
    @Autowired
    MonsterDetailsList monsterDetailsList;
    @Autowired TeamSuggestionToActualCardsService teamSuggestionToActualCardsService;
    @Autowired RatingContextFactory ratingContextFactory;
    @Autowired List<TeamBuildingStrategy> teamBuildingStrategies;
    @Value("${defaults.topHandCount}") Integer playerTeamCount;
    public static final Integer MAX_MONSTER_ID = 1000;

    Logger logger = LoggerFactory.getLogger(ReflectiveTeamBuilderService.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
//        if (teamRequestContext.getPlayerSuggestedTeams().size() == 0) {
//            return teamRequestContext;
//        }

        ColorVsColorRatingResults colorVsColorRatingResults = ColorVsColorRatingService.rankColorVsColor(teamRequestContext.getColorStats(), teamRequestContext.getTeamRequest().getNotColors());
        List<TeamRank> generatedTeams = buildTeams(teamRequestContext.getPlayerSuggestedTeams(), teamRequestContext.getPlayerCollection(), teamRequestContext.getTeamRequest().getMana(), teamRequestContext.getTeamRequest().getNotColors(), colorVsColorRatingResults);
        Collections.shuffle(generatedTeams);

        LevelLimit levelLimit = teamRequestContext.getTeamRequest().getLevelLimit();
        RealizedCollection realizedBackupCollection = teamRequestContext.getPlayerCollection();
        List<Team> playerBackupTeams = teamSuggestionToActualCardsService.convertToPlayersActualCards(generatedTeams, realizedBackupCollection, levelLimit);

        teamRequestContext.addAdditionalSuggestedTeams(new ArrayList<>(playerBackupTeams));

        return teamRequestContext;
    }

    public List<TeamRank> buildTeams(List<TeamRank> teamRankList, RealizedCollection realizedCollection, Integer manaLimit, Set<ColorType> notColors, ColorVsColorRatingResults colorVsColorRatingResults) {
        List<TeamRank> generatedTeams = new ArrayList<>();

        for (TeamBuildingStrategy teamBuildingStrategy : teamBuildingStrategies) {
            generatedTeams.addAll(teamBuildingStrategy.getTeamRanks(teamRankList, realizedCollection, manaLimit, notColors, colorVsColorRatingResults));
        }

        return generatedTeams;
    }
}

