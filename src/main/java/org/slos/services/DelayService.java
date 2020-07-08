package org.slos.services;

import org.slos.AppConfig;
import org.slos.TeamRequestProcessStep;
import org.slos.domain.TeamRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class DelayService implements TeamRequestProcessStep {
    @Autowired AppConfig appConfig;
    @Value("${defaults.delaySubmission:false}") boolean delayToMaxTime;

    private final Logger logger = LoggerFactory.getLogger(DelayService.class);

    @Override
    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        if (!teamRequestContext.getTeamRequest().getTournamentGame()) {
            Boolean implementDelay = delayToMaxTime;
            List<String> delayAgainst = appConfig.getUseMaxTimeAgainst();

            if (delayAgainst != null && delayAgainst.contains(teamRequestContext.getTeamRequest().getPlayerBottom())) {
                implementDelay = true;
            }

            if (implementDelay) {
                Long returnAt = teamRequestContext.getTeamRequestTimeout();
                Long timeToDelay = returnAt - System.currentTimeMillis();

                try {
                    logger.info("Delaying for " + timeToDelay + " against: " + teamRequestContext.getTeamRequest().getPlayerBottom());
                    Thread.sleep(timeToDelay);
                } catch (Exception e) {
                }
            }
        }

        return teamRequestContext;
    }
}
