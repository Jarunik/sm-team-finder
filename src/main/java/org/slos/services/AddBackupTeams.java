package org.slos.services;

import org.slos.Team;
import org.slos.TeamRequestProcessStep;
import org.slos.TeamSuggestionToActualCardsService;
import org.slos.domain.RealizedCollection;
import org.slos.domain.TeamRank;
import org.slos.domain.TeamRequestContext;
import org.slos.splinterlands.domain.LevelLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AddBackupTeams implements TeamRequestProcessStep {
    @Autowired TeamSuggestionToActualCardsService teamSuggestionToActualCardsService;
    @Value("${defaults.topHandCount}") Integer playerTeamCount;
    @Value("${defaults.bottomHandCount}") Integer opponentTeamCount;

    Logger logger = LoggerFactory.getLogger(AddBackupTeams.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        logger.info("Original team count from collection: " + teamRequestContext.getPlayerTeams().size());
        Integer topCountMinimum = (playerTeamCount);
        Integer bottomCountMinimum = (int)(opponentTeamCount * .4);

        addPlayerBackupTeams(teamRequestContext, topCountMinimum);
        addOpponentBackupTeams(teamRequestContext, bottomCountMinimum);

        logger.info("Team count after backup added: " + teamRequestContext.getPlayerTeams().size());
        return teamRequestContext;
    }

    public void addPlayerBackupTeams(TeamRequestContext teamRequestContext, Integer minimumCount) {
        List<Team> playerTeams = teamRequestContext.getPlayerTeams();

        if (playerTeams.size() < minimumCount) {
            logger.info("Need backup teams for player.");
            LevelLimit levelLimit = teamRequestContext.getTeamRequest().getLevelLimit();
            RealizedCollection realizedBackupCollection = teamRequestContext.getPlayerCollection();
            List<TeamRank> backupTeamRanks = teamRequestContext.getPlayerBackupSuggestedTeams();
            List<Team> playerBackupTeams = teamSuggestionToActualCardsService.convertToPlayersActualCards(backupTeamRanks, realizedBackupCollection, levelLimit);

            Set<String> teamIds = teamRequestContext.getPlayerTeams().stream().map(team -> team.getId()).collect(Collectors.toSet());

            for (Team team : playerBackupTeams) {
                if (!teamIds.contains(team.getId())) {
                    playerTeams.add(team);
                    teamIds.add(team.getId());
                }
            }

            playerTeams.stream().forEach(team -> {
                team.setRank(team.getRank() / 10);
                team.setSource(team.getSource());
            });

            teamRequestContext.setPlayerTeams(playerTeams);
        }
        else {
            logger.info("Do not need backup teams, but adding top rated few.");

            LevelLimit levelLimit = teamRequestContext.getTeamRequest().getLevelLimit();
            RealizedCollection realizedBackupCollection = teamRequestContext.getPlayerCollection();
            List<Team> playerBackupTeams = teamSuggestionToActualCardsService.convertToPlayersActualCards(teamRequestContext.getPlayerBackupSuggestedTeams(), realizedBackupCollection, levelLimit);

            Set<String> teamIds = teamRequestContext.getPlayerTeams().stream().map(team -> team.getId()).collect(Collectors.toSet());

            int backupBestCount = (int)(playerTeamCount * .05);
            for (int i = 0; (i < backupBestCount) && (i < playerBackupTeams.size()); i++) {
                if (!teamIds.contains(playerBackupTeams.get(i).getId())) {
                    playerTeams.add(playerBackupTeams.get(i));
                    teamIds.add(playerBackupTeams.get(i).getId());
                }
            }

            playerTeams.stream().forEach(team -> {
                team.setRank(team.getRank() / 3);
                team.setSource(team.getSource());
            });

            teamRequestContext.setPlayerTeams(playerTeams);
        }
    }

    public void addOpponentBackupTeams(TeamRequestContext teamRequestContext, Integer minimumCount) {
        List<Team> opponentTeams = teamRequestContext.getOpponentTeams();
        Set<String> opponentTeamsUsed = opponentTeams.stream().map(team -> team.getId()).collect(Collectors.toSet());

        if (opponentTeams.size() < minimumCount) {
            LevelLimit levelLimit = teamRequestContext.getTeamRequest().getLevelLimit();
            RealizedCollection realizedBackupCollection = teamRequestContext.getBackupOpponentCollection();
            List<Team> opponentBackupTeams = teamSuggestionToActualCardsService.convertToPlayersActualCards(teamRequestContext.getPlayerSuggestedTeams(), realizedBackupCollection, levelLimit);

            for (Team team : opponentBackupTeams) {
                if (!opponentTeamsUsed.contains(team.getId())) {
                    opponentTeams.add(team);
                    opponentTeamsUsed.add(team.getId());
                }
            }

            teamRequestContext.setOpponentTeams(opponentTeams);

            if (opponentTeams.size() < 20) {
                for (Team team : teamRequestContext.getPlayerTeams()) {
                    if (!opponentTeamsUsed.contains(team.getId())) {
                        opponentTeams.add(team);
                        opponentTeamsUsed.add(team.getId());
                    }
                }
            }
        }
    }
}
