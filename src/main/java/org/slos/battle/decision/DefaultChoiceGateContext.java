package org.slos.battle.decision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DefaultChoiceGateContext<Result> implements ChoiceGateContext<Result> {
    private Map<Integer, Choice<Result>> choicesMap = new HashMap<>();
    private Map<Integer, UUID> choiceKeyMap =  null;
    private Map<Integer, Long> timesUsed = null;


    public DefaultChoiceGateContext(List<Choice<Result>> choices) {
        for (Choice choice : choices) {
            choicesMap.put(choice.getId(), choice);
        }

        if (this.timesUsed == null) {
            timesUsed = new HashMap<>();
            choiceKeyMap = new HashMap<>();

            for (Choice choice : choicesMap.values()) {
                timesUsed.put(choice.getId(), 0l);
                choiceKeyMap.put(choice.getId(), UUID.randomUUID());
            }
        }
    }

    public Choice<Result> useChoice(Choice<Result> choice) {
        incrementChoiceUsedCount(choice);


        return choice;
    }

    public Map<Integer, Long> getTimesUsed() {
        return timesUsed;
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
        choicesMap = new HashMap<>();
    }

    @Override
    public String toString() {
        return "ChoiceGateContext{" +
                "choicesMap=" + choicesMap +
                ", choiceKeyMap=" + choiceKeyMap +
                ", timesUsed=" + timesUsed +
                '}';
    }
}
