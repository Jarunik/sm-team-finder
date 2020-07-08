package org.slos.battle.abilities.buff;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.attribute.BuffAbility;

public class BuffFactory {

    public Buff getFromAbility(Ability ability) {

        if (ability.containsClassification(AbilityClassification.BUFF)) {
            return ((BuffAbility)ability).getBuffEffect();
        }

        return null;
    }
}
