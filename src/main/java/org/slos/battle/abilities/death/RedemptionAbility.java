package org.slos.battle.abilities.death;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.death.RedemptionRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;

public class RedemptionAbility extends Ability implements AbilityEffect {
    private RedemptionRule redemptionRule = new RedemptionRule();

    public RedemptionAbility() {
        super(AbilityType.REDEMPTION, AbilityClassification.ON_DEATH);
    }

    @Override
    public AbilityEffect getEffect() {
        return redemptionRule;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}
