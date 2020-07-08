package org.slos.battle.abilities.attack;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.AttackRule;
import org.slos.battle.abilities.rule.attack.TrampleRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class TrampleAbility extends Ability implements AbilityEffect {
    private TrampleRule rule = new TrampleRule();

    public TrampleAbility() {
        super(AbilityType.TRAMPLE, AbilityClassification.ATTACK, AbilityClassification.ON_TURN_START, AbilityClassification.ON_TURN_END);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }

    @Override
    @JsonIgnore
    public AttackRule getEffect() {
        return rule;
    }
}
