package org.slos.battle.decision.strategy;

import org.slos.battle.decision.Choice;
import org.slos.battle.decision.ChoiceGateContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeightEnforcedStrategy<Result> implements DecisionStrategy<Result> {
    private final ChoiceGateContext<Result> choiceGateContext;
    private Long totalWeight;
    private Long timesCalledMod = 0l;
    private Long timesCalled = 0l;

    public WeightEnforcedStrategy(ChoiceGateContext<Result> choiceGateContext) {
        this.choiceGateContext = choiceGateContext;
        this.totalWeight = figureTotalWeight(choiceGateContext.getChoices());
    }

    private Long figureTotalWeight(List<Choice<Result>> choices) {
        Long figuringWeight = 0l;
        for (Choice<Result> choice : choices) {
            figuringWeight += choice.getWeight();
        }
        return figuringWeight;
    }

    @Override
    public Choice<Result> getChoice() {
        List<Choice<Result>> choices = choiceGateContext.getChoices();
        List<WeightedChoice> weightedChoices = new ArrayList<>();

        for (Choice choice : choices) {
            float activeWeight = figureActiveWeight(choice);
            weightedChoices.add(new WeightEnforcedStrategy.WeightedChoice(choice, (long)(activeWeight * 1000)));
        }

        List<WeightedChoice> candidates = new ArrayList<>();
        long highestWeight = 0;

        for (WeightEnforcedStrategy.WeightedChoice weightedChoice : weightedChoices) {

            if (weightedChoice.getAcviteWeight() >= highestWeight) {
                if (highestWeight != weightedChoice.getAcviteWeight()) {
                    candidates.clear();
                }
                highestWeight = weightedChoice.getAcviteWeight();
                candidates.add(weightedChoice);
            }
        }

        if (candidates.size() > 1) {
            Collections.shuffle(candidates);
        }

        if (candidates.size() == 0) {
            System.err.println("Choices: " + choiceGateContext.getChoices());
            System.err.println("Previously Called: " + choiceGateContext.getTimesUsed());
            System.err.println("Mod Gate Called: " + timesCalled);
            System.err.println("Total Weight: " + totalWeight);
            System.err.println("Remaing Choices Before Reset: " + (totalWeight - timesCalled));

            throw new IllegalStateException("Must be at least one candidate.");
        }

        timesCalled++;
        return candidates.get(0).getChoice();
    }

    private float figureActiveWeight(Choice choice) {
        float activeWeight;
        if (timesCalledMod-- - totalWeight <= 0) {
            activeWeight = choice.getWeight();
            timesCalledMod = totalWeight;
        }
        else {
            try {
                float choicesUpForSelection = choice.getWeight() - (choiceGateContext.getPreviousUsedCount(choice) % choice.getWeight());
                float totalAvailable = (totalWeight - timesCalled);
                activeWeight = choicesUpForSelection / totalAvailable;
            } catch (Exception e) {
                displayErrorInfo(choice);
                throw e;
            }
        }

        if (activeWeight < 0) {
            displayErrorInfo(choice);

            throw new IllegalStateException("The Active Weight of a choice can not be lower than 0.  Check formula in code.");
        }
        return activeWeight;
    }

    private void displayErrorInfo(Choice choice) {
        System.err.println("Choices: " + choiceGateContext.getChoices());
        System.err.println("Previously Called: " + choiceGateContext.getTimesUsed());
        System.err.println("This Choice: " + choice);
        System.err.println("Mod Gate Called: " + timesCalled);
        System.err.println("Times Choiced Called: " + choiceGateContext.getPreviousUsedCount(choice));
        System.err.println("Total Weight: " + totalWeight);
        System.err.println("Remaing Choices Before Reset: " + (totalWeight - timesCalled));
    }

    class WeightedChoice<Result> {
        Choice<Result> choice;
        Long acviteWeight;

        public WeightedChoice(Choice<Result> choice, Long acviteWeight) {
            this.choice = choice;
            this.acviteWeight = acviteWeight;
        }

        public Choice<Result> getChoice() {
            return choice;
        }

        public Long getAcviteWeight() {
            return acviteWeight;
        }

        @Override
        public String toString() {
            return "WeightedChoice{" +
                    "choice=" + choice +
                    ", acviteWeight=" + acviteWeight +
                    '}';
        }
    }
}
