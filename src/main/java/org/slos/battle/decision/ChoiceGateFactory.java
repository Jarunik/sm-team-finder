package org.slos.battle.decision;

import org.slos.battle.decision.strategy.ChoiceStrategyMode;
import org.slos.battle.decision.strategy.SingletonChoiceGate;
import org.slos.util.PermutationMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ChoiceGateFactory {
    private final MasterChoiceContext masterChoiceContext;

    private final Logger logger = LoggerFactory.getLogger(MasterChoiceContext.class);

    public ChoiceGateFactory(MasterChoiceContext masterChoiceContext) {
        this.masterChoiceContext = masterChoiceContext;
    }

    public ChoiceGateFactory() {
        this(null);
    }

    public <Result> ChoiceGate<Result> buildGate(Configuration<Result> configuration) {
        verifyMasterContext(configuration);

        return build(configuration);
    }

    private void verifyMasterContext(Configuration configuration) {
        if ((configuration.getConfiguredChoiceStrategyMode().requiresMasterContext())  && (masterChoiceContext == null))
        {
            throw new IllegalStateException("Using mode " + configuration.getConfiguredChoiceStrategyMode() + " requires a MasterChoiceContext to be configured.");
        }
    }

    private <Result> ChoiceGate<Result> build(Configuration<Result> configuration) {
        try {
            if (configuration.gateId == null) {
                throw new IllegalStateException("A ChoiceGate must have a gate ID.");
            }

            ChoiceGate<Result> checkForSingletonGateUse = getSingletonChoiceGateIfChoicesEmpty(configuration);
            if (checkForSingletonGateUse != null) {
                return checkForSingletonGateUse;
            }

            switch (configuration.getConfiguredChoiceStrategyMode()) {
                case ChoiceStrategyMode.WEIGHT_ENFORCED:
                case LEAST_USED_CHOICE:
                    ChoiceGate alreadyRegisteredChoicegate = getChoiceGateFromContext(configuration);
                    if (alreadyRegisteredChoicegate != null) {
                        if (!configuration.choicesArePermutation) {
                            alreadyRegisteredChoicegate.getChoiceGateContext().setChoices(configuration.choices);
                        }
                        else {
                            List<Choice> resultPermutations = populateChoices(configuration.choicesAsPermutation);
                            alreadyRegisteredChoicegate.getChoiceGateContext().setChoices(resultPermutations);
                            ((ChoiceGateContextAsPermutation)alreadyRegisteredChoicegate.getChoiceGateContext()).setPermutationChoices(configuration.choicesAsPermutation); //TODO: Refactor away this typecast
                        }
                        return alreadyRegisteredChoicegate;
                    }
                    break;
                default:
                    break;
            }

            ChoiceStrategyMode useChoiceStrategyMode = configuration.getConfiguredChoiceStrategyMode();
            ChoiceGate newChoiceGate = getChoiceGate(configuration, useChoiceStrategyMode);

            if (useChoiceStrategyMode.requiresMasterContext()) {
                masterChoiceContext.registerChoiceGate(newChoiceGate);
            }

            return newChoiceGate;
        }
        catch (Exception e) {
            logger.error("Failed building gate for configuration: " + configuration);
            throw e;
        }
    }

    private List<Choice> populateChoices(List choicesAsPermutation) {
        List<Choice> listChoice = new ArrayList<>();

        int sizeOfPermutationCount = PermutationMap.getPermutationMap(choicesAsPermutation.size()).length;
        for (Integer i = 0; i < sizeOfPermutationCount; i++) {
            Choice permutationChoice = new Choice<>(i, 1, null);
            listChoice.add(permutationChoice);
        }

        return listChoice;
    }

    private <Result> ChoiceGate getChoiceGate(Configuration<Result> configuration, ChoiceStrategyMode useChoiceStrategyMode) {
        ChoiceGateContext newChoiceGateContext;
        if (!configuration.choicesArePermutation) {
            newChoiceGateContext = new DefaultChoiceGateContext(configuration.getChoices());
        }
        else {
            newChoiceGateContext = new ChoiceGateContextAsPermutation(configuration.getChoicesAsPermutation());
        }

        ChoiceGate newChoiceGate = new DefaultChoiceGate(configuration.gateId, useChoiceStrategyMode, masterChoiceContext, configuration.choicesArePermutation, newChoiceGateContext);
        return newChoiceGate;
    }

    private <Result> ChoiceGate<Result> getSingletonChoiceGateIfChoicesEmpty(Configuration<Result> configuration) {

        if ((configuration.configuredChoiceStrategyMode == ChoiceStrategyMode.FIRST_CONFIGURED)  ||
        (((!configuration.choicesArePermutation) && (configuration.getChoices().size() < 2)) ||
           ((configuration.choicesArePermutation) && (configuration.getChoicesAsPermutation().size() < 2)))) {
            if (((!configuration.choicesArePermutation) && (configuration.getChoices().size() ==0 )) ||
                ((configuration.choicesArePermutation) && (configuration.getChoicesAsPermutation().size() == 0))) {
                return new SingletonChoiceGate<>(configuration.defaultResult);
            }
            if (!configuration.choicesArePermutation) {
                return new SingletonChoiceGate<>(configuration.getChoices().get(0).getResult());
            }
            else {
                return new SingletonChoiceGate<>(configuration.getChoicesAsPermutation());
            }
        }

        return null;
    }

    private ChoiceGate getChoiceGateFromContext(Configuration configuration) {
        ChoiceGate choiceGateToReturn;
        choiceGateToReturn = masterChoiceContext.getChoiceGateInstance(masterChoiceContext.getLatestDecisionHash());

        if (choiceGateToReturn != null) {
            return choiceGateToReturn;
        }
        return null;
    }

    public static class Configuration<Result> {
        private List<Choice<Result>> choices = new ArrayList<>();
        private List<Result> choicesAsPermutation;
        private String gateId = null;
        public static final ChoiceStrategyMode DEFAULT_CHOICE_MODE = ChoiceStrategyMode.MOST_LIKELY;
        private ChoiceStrategyMode configuredChoiceStrategyMode = null;
        private boolean choicesArePermutation = false;
        private Result defaultResult;

        public  Configuration() {}

        protected List<Choice<Result>> getChoices() {
            return choices;
        }

        public Configuration<Result> addChoice(Choice<Result> choice) {
            if (choicesArePermutation) {
                throw new IllegalStateException("Can not add individual choices when selected to be returned as a permutation.  Instead, put all choices into a list and call method 'returnPermutationOf'");
            }
            if (!(choice.getWeight() <= 0)) {
                choices.add(choice);
            }
            return this;
        }

        public Configuration<Result> returnIfEmptyOrNull(Result result) {
            defaultResult = result;
            return this;
        }

        public Configuration setGateId(String gateId) {
            if (this.gateId != null) {
                throw new IllegalStateException("A DefaultChoiceGate may only have one ID");
            }

            this.gateId = gateId;
            return this;
        }

        public Configuration setChoiceMode(ChoiceStrategyMode choiceStrategyMode) {
            this.configuredChoiceStrategyMode = choiceStrategyMode;
            return this;
        }

        public List<Result> getChoicesAsPermutation() {
            return choicesAsPermutation;
        }

        public Configuration returnPermutationOf(List<Result> results) {
            if (choices.size() > 0) {
                throw new IllegalStateException("Can not add individual choices when selected to be returned as a permutation.  Instead, put all choices into a list and call method 'returnPermutationOf'");
            }

            choicesAsPermutation = results;
            choicesArePermutation = true;

            return this;
        }

        public ChoiceStrategyMode getConfiguredChoiceStrategyMode() {
            return (configuredChoiceStrategyMode == null) ? DEFAULT_CHOICE_MODE : configuredChoiceStrategyMode;
        }

        @Override
        public String toString() {
            return "Configuration{" +
                    "choices=" + choices +
                    ", gateId='" + gateId + '\'' +
                    ", configuredChoiceStrategyMode=" + configuredChoiceStrategyMode +
                    ", choicesArePermutation=" + choicesArePermutation +
                    ", defaultResult=" + defaultResult +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ChoiceGateFactory{" +
                "masterChoiceContext=" + masterChoiceContext +
                '}';
    }
}
