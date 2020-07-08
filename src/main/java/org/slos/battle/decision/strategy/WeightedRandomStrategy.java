package org.slos.battle.decision.strategy;

import org.slos.battle.decision.Choice;
import org.slos.battle.decision.ChoiceGateContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeightedRandomStrategy<Result> implements DecisionStrategy<Result> {
    private final ChoiceGateContext<Result> choiceGateContext;

    public WeightedRandomStrategy(ChoiceGateContext<Result> choiceGateContext) {
        this.choiceGateContext = choiceGateContext;
    }

    @Override
    public Choice<Result> getChoice() {
        List<Choice<Result>> possibleChoices = choiceGateContext.getChoices();

        List<Choice<Result>> choicesDistributed = new ArrayList<>();
        for (Choice<Result> choice : possibleChoices) {
            long choiceWeight = choice.getWeight();
            for (int i = 0; i < choiceWeight; i++) {
                choicesDistributed.add(choice);
            }
        }
        Collections.shuffle(choicesDistributed);

        Integer ab = Integer.valueOf(1);
        ab = test(ab + 1);

        return choicesDistributed.get(0);
    }

    public Integer test(Object a) {
        return (Integer)a;
    }
}
