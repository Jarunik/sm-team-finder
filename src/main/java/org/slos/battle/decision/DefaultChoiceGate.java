package org.slos.battle.decision;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slos.battle.decision.strategy.ChoiceStrategyMode;
import org.slos.battle.decision.strategy.WeightEnforcedStrategy;
import org.slos.battle.decision.strategy.WeightedRandomStrategy;
import org.slos.util.ToJson;

import java.util.List;

public class DefaultChoiceGate<Result> implements ChoiceGate<Result>, ToJson {
    private final ChoiceGateContext<Result> choiceGateContext;
    private final String gateId;
    private final ChoiceStrategyMode DEFAULT_CHOICE_MODE;
    @JsonIgnore private final MasterChoiceContext masterChoiceContext;
    private final WeightEnforcedStrategy<Result> weightEnforcedStrategy;
    private final WeightedRandomStrategy<Result> weightedRandomStrategy;

    public DefaultChoiceGate(String gateId, ChoiceStrategyMode choiceStrategyMode, MasterChoiceContext masterChoiceContext, boolean choicesAreFromPermutation, ChoiceGateContext<Result> choiceGateContext) {
        List<Choice<Result>> choices = choiceGateContext.getChoices();
        if ((choices == null) || (choices.size() == 0)) {
            throw new IllegalStateException("Choices must not be null or of size 0.  Use a SingletonChoiceGate instead.");
        }
        this.choiceGateContext = choiceGateContext;
        this.gateId = gateId;
        this.DEFAULT_CHOICE_MODE = choiceStrategyMode;
        this.masterChoiceContext = masterChoiceContext;
        this.weightEnforcedStrategy = new WeightEnforcedStrategy<>(choiceGateContext);
        this.weightedRandomStrategy = new WeightedRandomStrategy<>(choiceGateContext);
    }

    public void reset() {
        choiceGateContext.reset();
    }

    public ChoiceGateContext<Result> getChoiceGateContext() {
        return choiceGateContext;
    }

    public Result getResult() {
        Choice<Result> selectedChoice = selectChoice();
        Choice<Result> usingChoice = choiceGateContext.useChoice(selectedChoice); //TODO: Refactor this somehow?  The permutation lookup and swapping happens on the useChoice

        if (masterChoiceContext != null) {
            masterChoiceContext.registerChoice(this, usingChoice);
        }

        Result result = usingChoice.getResult();
        return result;
    }

    private Choice<Result> selectChoice() {
        Choice<Result> choice = null;

        if (choiceGateContext.getChoices().size() == 1) {
            return choiceGateContext.getChoices().get(0);
        }
        switch (DEFAULT_CHOICE_MODE) {
            default:
            case RANDOM_WEIGHTED:
                choice = weightedRandomStrategy.getChoice();
                break;
            case MOST_LIKELY:
                choice = getMostLikelyResult();
                break;
            case WEIGHT_ENFORCED:
                choice = weightEnforcedStrategy.getChoice();
                break;
            case LEAST_USED_CHOICE:
                choice = getMostLikelyUnusedResult();
                break;
            case RANDOM_IGNORE_WEIGHT:
                choice = getTotallyRandomResult();
                break;
        }

        return choice;
    }

    private Choice<Result> getMostLikelyResult() {
        List<Choice<Result>> choices = choiceGateContext.getChoices();

        Choice<Result> highestLikely = choices.get(0);

        for (Choice<Result> choice : choices) {
            if (highestLikely.getWeight() < choice.getWeight()){
                highestLikely = choice;
            }
        }

        return highestLikely;
    }

    private Choice<Result> getTotallyRandomResult() {
        throw new UnsupportedOperationException(ChoiceStrategyMode.RANDOM_WEIGHTED + " mode not yet supported.");
    }

    private Choice<Result> getMostLikelyUnusedResult() {
        if (masterChoiceContext == null) {
            throw new IllegalStateException("Must have a MasterChoiceContext in order for this mode: " + ChoiceStrategyMode.WEIGHT_ENFORCED);
        }

        throw new UnsupportedOperationException(ChoiceStrategyMode.RANDOM_WEIGHTED + " mode not yet supported.");
    }

    public String getGateId() {
        return gateId;
    }

    @Override
    public String toString() {
        return "DefaultChoiceGate{" +
                "choiceGateContext=" + choiceGateContext +
                ", gateId='" + gateId + '\'' +
                ", DEFAULT_CHOICE_MODE=" + DEFAULT_CHOICE_MODE +
                '}';
    }
}
