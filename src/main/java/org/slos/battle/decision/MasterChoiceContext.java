package org.slos.battle.decision;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slos.util.ToJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterChoiceContext implements ToJson {
    private Map<Long, ChoiceGate> choiceTree;
    private Map<String, List<ChoiceGate>> choiceGatesById = new HashMap<>();
    private long latestDecisionHash;
    @JsonIgnore
    private final ChoiceGateFactory choiceGateFactory;
    public static final long START_HASH = -1l;
    private Long totalChoicesMade = 0l;

    public void reset() {
        latestDecisionHash = START_HASH;

        for(ChoiceGate choiceGate : choiceTree.values()) {
            choiceGate.reset();
        }
    }

    public MasterChoiceContext() {
        this.choiceGateFactory = new ChoiceGateFactory(this);
        choiceTree = new HashMap<>();
        latestDecisionHash = START_HASH;
    }

    public Long getTotalChoicesMade() {
        return totalChoicesMade;
    }

    public Long getHash(ChoiceGate choiceGate, Choice result) {
        return (choiceGate.getGateId().hashCode() * 7) + (latestDecisionHash * 5 ) + (choiceGate.getChoiceGateContext().getUUIDForChoice(result).hashCode() * 7);
    }

    protected void registerChoiceGate(ChoiceGate choiceGate) {
        choiceTree.put(latestDecisionHash, choiceGate);

        List<ChoiceGate> choiceGates = choiceGatesById.get(choiceGate.getGateId());
        if (choiceGates == null) {
            choiceGates = new ArrayList<>();
            choiceGatesById.put(choiceGate.getGateId(), choiceGates);
        }

        choiceGates.add(choiceGate);
    }

    public Map<String, List<ChoiceGate>> getChoiceGatesById() {
        return choiceGatesById;
    }

    public void registerChoice(DefaultChoiceGate choiceGate, Choice choice) {
        totalChoicesMade++;
        latestDecisionHash = getHash(choiceGate, choice);
    }

    public ChoiceGateFactory getChoiceGateFactory() {
        return choiceGateFactory;
    }

    public ChoiceGate getChoiceGateInstance(Long hash) {
        return choiceTree.get(hash);
    }

    public void registerChoiceGate(DefaultChoiceGate choiceGate, Choice result) {
        Long hash = getHash(choiceGate, result);
        choiceTree.put(hash, choiceGate);
    }

    public Long getLatestDecisionHash() {
        return latestDecisionHash;
    }

    public int getGateTotal() {
        return choiceTree.size();
    }

    @Override
    public String toString() {
        return "MasterChoiceContext{" +
                "choiceTree=" + choiceTree.size() +
                ", latestDecisionHash=" + latestDecisionHash +
                ", totalChoicesMade=" + totalChoicesMade +
                '}';
    }
}
