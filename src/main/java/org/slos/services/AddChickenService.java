package org.slos.services;

import org.slos.TeamRequestProcessStep;
import org.slos.domain.RealizedCollection;
import org.slos.domain.TeamRank;
import org.slos.domain.TeamRequestContext;
import org.slos.splinterlands.collection.CollectionCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddChickenService implements TeamRequestProcessStep {
    private static final String CHICKEN_LAST_SOURCE = "ChickenLast";

    Logger logger = LoggerFactory.getLogger(AddChickenService.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {

        List<TeamRank> teamSuggestions =  teamRequestContext.getPlayerSuggestedTeams();

        RealizedCollection playerCollection = teamRequestContext.getPlayerCollection();
//        List<Team> playerTeams = teamRequestContext.getPlayerTeams();

        if (playerCollection.hasCard(131)) {
            List<TeamRank> teamsWithChicken = addChickenLastIfNoChicken(teamSuggestions, playerCollection);
            teamRequestContext.setPlayerSuggestedTeams(teamsWithChicken);
        }

        return teamRequestContext;
    }

    private List<TeamRank> addChickenLastIfNoChicken(List<TeamRank> goodTeams, RealizedCollection realizedCollection) {
        List<TeamRank> cardsWithChickenLast = new ArrayList<>();
        Optional<CollectionCard> chickenOptional = realizedCollection.getCards().stream().filter(realizedCollectionCard -> realizedCollectionCard.getMonsterDetails().getId() == 131).findFirst().map(realizedCollectionCard -> realizedCollectionCard.getCollectionCard());

        for (TeamRank teamRank : goodTeams) {
            String[] cards = teamRank.getId().split("-");

            if ((teamRank.hasCard(131)) || (!teamRank.hasCard(131) && cards.length >= 7)) {
                cardsWithChickenLast.add(teamRank);
                continue;
            }

            if (chickenOptional.isPresent()) {

                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i < cards.length; i++) {
                    if ((cards[i] != null) && (!cards[i].equals("131"))) {
                        stringBuilder.append(cards[i] + "-");
                    }
                }
                stringBuilder.append("131");

                cardsWithChickenLast.add(new TeamRank(teamRank.getColor(), stringBuilder.toString(), teamRank.getRank(), teamRank.getTotalPlayed(), teamRank.getWins(), teamRank.getLosses(), teamRank.getSource() + "|" + CHICKEN_LAST_SOURCE, null));
            }
        }

        return cardsWithChickenLast;
    }
}
