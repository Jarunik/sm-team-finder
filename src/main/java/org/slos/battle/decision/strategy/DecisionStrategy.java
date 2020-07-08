package org.slos.battle.decision.strategy;

import org.slos.battle.decision.Choice;

public interface DecisionStrategy<T> {
    Choice<T> getChoice();
}
