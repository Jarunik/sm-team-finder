package org.slos.splinterlands;

import org.slos.TeamRequestProcessStep;
import org.slos.domain.RealizedCollection;
import org.slos.domain.TeamRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class AddBackupCollection implements TeamRequestProcessStep {
    Logger logger = LoggerFactory.getLogger(AddBackupCollection.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        logger.info("Starting backup collection.");
        RealizedCollection playerCollection = teamRequestContext.getPlayerCollection();
        RealizedCollection opponentCollection = teamRequestContext.getOpponentCollection();
        RealizedCollection backupCollection = new RealizedCollection(playerCollection.getPlayer(), new ArrayList<>(playerCollection.getCards()));
        backupCollection.getCards().addAll(opponentCollection.getCards());

        teamRequestContext.setBackupOpponentCollection(backupCollection);
        logger.info("Finished backup collection.");

        return teamRequestContext;
    }
}
