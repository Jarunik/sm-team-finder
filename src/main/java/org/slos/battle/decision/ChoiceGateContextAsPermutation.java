package org.slos.battle.decision;

import org.slos.util.PermutationMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChoiceGateContextAsPermutation<Result> implements ChoiceGateContext<Result> {
    private Map<Integer, Choice<Result>> choicesMap = new HashMap<>();
    private List<Result> permutationChoices;
    private Map<Integer, UUID> choiceKeyMap = null;
    private Map<Integer, Long> timesUsed = null;


    public ChoiceGateContextAsPermutation(List<Result> choicesAsPermutation) {
        this.permutationChoices = choicesAsPermutation;

        populateChoices(choicesAsPermutation);

        if (this.timesUsed == null) {
            timesUsed = new HashMap<>();
            choiceKeyMap = new HashMap<>();

            for (Choice choice : choicesMap.values()) {
                timesUsed.put(choice.getId(), 0l);
                choiceKeyMap.put(choice.getId(), UUID.randomUUID());
            }
        }
    }

    private void populateChoices(List<Result> choicesAsPermutation) {
        this.permutationChoices = choicesAsPermutation;
        int sizeOfPermutationCount = PermutationMap.getPermutationMap(choicesAsPermutation.size()).length;
        for (Integer i = 0; i < sizeOfPermutationCount; i++) {
            Choice<Result> permutationChoice = new Choice<>(i, 1, null);
            choicesMap.put(i, permutationChoice);
        }
    }

    public Choice<Result> useChoice(Choice<Result> choice) {
        incrementChoiceUsedCount(choice);

        List<Result> permutation = PermutationMap.getPermutation(permutationChoices, choice.getId());
        Choice permutationChoice = new Choice<>(choice.getId(), choice.getWeight(), permutation);

        return permutationChoice;
    }

    public Map<Integer, Long> getTimesUsed() {
        return timesUsed;
    }

    public void setPermutationChoices(List<Result> permutationChoices) {
        this.permutationChoices = permutationChoices;
    }

    @Override
    public void setChoices(List<Choice> choices) {
        validateChoices(choices);

        for (Choice choice : choices) {
            choicesMap.put(choice.getId(), choice);
        }
    }

    private void validateChoices(List<Choice> choices) {
        for (Choice choice : choices) {
            if (timesUsed.get(choice.getId()) == null) {
                throw new IllegalStateException("Invalid choices.");
            }
        }
    }

    public List<Choice<Result>> getChoices() {
        return new ArrayList<>(choicesMap.values());
    }

    public Long getPreviousUsedCount(Choice<Result> choice) {
        return timesUsed.get(choice.getId());
    }

    public UUID getUUIDForChoice(Choice<Result> choice) { //TODO: Rework this for the choice id, or must be totally unique?
        return choiceKeyMap.get(choice.getId());
    }

    private void incrementChoiceUsedCount(Choice<Result> choice) {
        try {
            timesUsed.put(choice.getId(), timesUsed.get(choice.getId()) + 1);
        } catch (Exception e) {
            System.err.println("Exception putting choice in timesUsed map: " + choice);
            throw e;
        }
    }

    public void reset() {
        permutationChoices = null;
        choicesMap = new HashMap<>();
    }

    @Override
    public String toString() {
        return "ChoiceGateContext{" +
                "choicesMap=" + choicesMap +
                ", permutationChoices=" + permutationChoices +
                ", choiceKeyMap=" + choiceKeyMap +
                ", timesUsed=" + timesUsed +
                '}';
    }
}