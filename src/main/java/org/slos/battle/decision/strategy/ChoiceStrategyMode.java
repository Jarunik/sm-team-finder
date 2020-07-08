package org.slos.battle.decision.strategy;

public enum ChoiceStrategyMode {
    WEIGHT_ENFORCED(true),
    LEAST_USED_CHOICE(true),
    MOST_LIKELY(false),
    RANDOM_WEIGHTED(false),
    RANDOM_IGNORE_WEIGHT(false),
    FIRST_CONFIGURED(false);

    private boolean requiresMasterContext;
    ChoiceStrategyMode(boolean requiresMasterContext) {
        this.requiresMasterContext = requiresMasterContext;
    }

    public boolean requiresMasterContext() {
        return requiresMasterContext;
    }
}
