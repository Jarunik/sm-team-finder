package org.slos.battle.decision;

import org.slos.util.ToJson;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ChoiceGateContext<Result> extends ToJson {
    Choice<Result> useChoice(Choice<Result> choice);
    Map<Integer, Long> getTimesUsed();
    List<Choice<Result>> getChoices();
    Long getPreviousUsedCount(Choice<Result> choice);
    UUID getUUIDForChoice(Choice<Result> choice); //TODO: Rework this for the choice id, or must be totally unique?
    void reset();
    void setChoices(List<Choice> choices);
}
