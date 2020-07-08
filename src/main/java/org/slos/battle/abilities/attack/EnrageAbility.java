package org.slos.battle.abilities.attack;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.OnBattleAttributeChangeRule;
import org.slos.battle.abilities.rule.hit.EnrageRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class EnrageAbility extends Ability implements AbilityEffect {
    private final EnragedState enragedState;

    public EnrageAbility() {
        super(AbilityType.ENRAGE, AbilityClassification.ON_BATTLE_STAT_CHANGE);
        enragedState = new EnragedState();
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    public OnBattleAttributeChangeRule getEffect() {
        return new EnrageRule(1, enragedState);
    }

    public class EnragedState {
        private Boolean isEnraged = false;

        public Boolean isEnraged() {
            return isEnraged;
        }

        public void setEnraged(Boolean enraged) {
            isEnraged = enraged;
        }

        @Override
        public String toString() {
            return "EnragedState{" +
                    "isEnraged=" + isEnraged +
                    '}';
        }
    }
}