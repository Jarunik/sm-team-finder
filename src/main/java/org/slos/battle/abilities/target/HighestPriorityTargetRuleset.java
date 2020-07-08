package org.slos.battle.abilities.target;

import java.util.Comparator;

public class HighestPriorityTargetRuleset implements Comparator<TargetRulesetAbility> {

    @Override
    public int compare(TargetRulesetAbility o1, TargetRulesetAbility o2) {

        if ((o1 == null) && (o2 == null)) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }


        int o1Priority = o1.getTargetPriority();
        int o2Priority = o2.getTargetPriority();

        if (o1Priority == o2Priority) {
            return 0;
        }

        if (o1.getTargetPriority() > o2.getTargetPriority()) {
            return 1;
        }

        return -1;
    }
}
