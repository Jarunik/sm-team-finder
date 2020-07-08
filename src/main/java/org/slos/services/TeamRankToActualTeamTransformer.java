package org.slos.services;

import org.slos.Team;
import org.slos.TeamRequestProcessStep;
import org.slos.TeamSuggestionToActualCardsService;
import org.slos.domain.RealizedCollection;
import org.slos.domain.TeamRank;
import org.slos.domain.TeamRequestContext;
import org.slos.splinterlands.domain.LevelLimit;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TeamRankToActualTeamTransformer implements TeamRequestProcessStep {
    @Autowired TeamSuggestionToActualCardsService teamSuggestionToActualCardsService;

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        LevelLimit levelLimit = teamRequestContext.getTeamRequest().getLevelLimit();

        RealizedCollection realizedPlayerCollection = teamRequestContext.getPlayerCollection();
        List<TeamRank> playerRanks = teamRequestContext.getPlayerSuggestedTeams();
        List<Team> playerTeams = teamSuggestionToActualCardsService.convertToPlayersActualCards(playerRanks, realizedPlayerCollection, levelLimit);

        RealizedCollection realizedOpponentCollection = teamRequestContext.getOpponentCollection();
        List<TeamRank> opponentRanks = teamRequestContext.getOpponentSuggestedTeams();
        List<Team> opponentTeams = teamSuggestionToActualCardsService.convertToPlayersActualCards(opponentRanks, realizedOpponentCollection, levelLimit);

        teamRequestContext.setPlayerTeams(playerTeams);
        teamRequestContext.setOpponentTeams(opponentTeams);

        return teamRequestContext;
    }
}
