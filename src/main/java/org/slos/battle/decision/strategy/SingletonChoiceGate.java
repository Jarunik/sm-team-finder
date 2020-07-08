package org.slos.battle.decision.strategy;

import org.slos.battle.decision.Choice;
import org.slos.battle.decision.ChoiceGate;
import org.slos.battle.decision.ChoiceGateContext;
import org.slos.battle.decision.DefaultChoiceGateContext;

import java.util.Collections;
import java.util.List;

public class SingletonChoiceGate<Result> implements ChoiceGate<Result> {
    private final ChoiceGateContext<Result> choiceGateContext;

    public SingletonChoiceGate(Result result) {
        choiceGateContext = new DefaultChoiceGateContext(Collections.singletonList(new Choice<>(1, 1, result)));
    }

    public SingletonChoiceGate(List<Result> resultAsList) {
        choiceGateContext = new DefaultChoiceGateContext(Collections.singletonList(new Choice<>(1, 1, resultAsList)));
    }

    @Override
    public Result getResult() {
        return choiceGateContext.getChoices().get(0).getResult();
    }

    @Override
    public ChoiceGateContext getChoiceGateContext() {
        return choiceGateContext;
    }

    @Override
    public String getGateId() {
        return "SingletonChoiceGate[" + choiceGateContext.getChoices().get(0).getId() + "]";
    }

    @Override
    public void reset() {
    }

    @Override
    public String toString() {
        return "SingletonChoiceGate{" +
                "choiceGateContext=" + choiceGateContext +
                '}';
    }
}
