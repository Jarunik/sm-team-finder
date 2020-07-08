package org.slos.battle.abilities.heal;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.CleanseTurnStartRule;
import org.slos.battle.abilities.rule.target.TargetFirstRule;
import org.slos.battle.abilities.rule.target.TargetFriendlyRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.turn.OnTurnAbility;

public class CleanseAbility extends Ability implements OnTurnAbility {
    public CleanseAbility() {
        super(AbilityType.CLEANSE, AbilityClassification.ON_TURN_START);
    }

    @Override
    public AbilityEffect getEffect() {
        TargetRuleset tankRuleset = new TargetRuleset.Builder()
                .addRule(new TargetFriendlyRule())
                .addRule(new TargetFirstRule()).build();

        return new CleanseTurnStartRule(tankRuleset);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}