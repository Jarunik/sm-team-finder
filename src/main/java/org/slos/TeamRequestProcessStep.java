package org.slos;

import org.slos.domain.TeamRequestContext;

public interface TeamRequestProcessStep {

    TeamRequestContext processStep(TeamRequestContext teamRequestContext);
}
