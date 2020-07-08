package org.slos.battle.decision;

import org.slos.util.ToJson;

public interface ChoiceGate<T> extends ToJson {

    T getResult();
    ChoiceGateContext getChoiceGateContext();
    String getGateId();
    void reset();
}
