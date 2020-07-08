package org.slos.battle.abilities.heal;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.HealStartRule;
import org.slos.battle.abilities.rule.target.TargetFriendlyRule;
import org.slos.battle.abilities.rule.target.TargetMostDamagedRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.turn.OnTurnAbility;
import org.slos.battle.monster.BattleAttributeType;

public class TriageAbility extends Ability implements OnTurnAbility {
    public TriageAbility() {
        super(AbilityType.TRIAGE, AbilityClassification.ON_TURN_START);
    }

    @Override
    public AbilityEffect getEffect() {
        TargetRuleset mostDamagedArmorRuleset = new TargetRuleset.Builder()
                .addRule(new TargetFriendlyRule())
                .addRule(new TargetMostDamagedRule())
                .build();

        return new HealStartRule(BattleAttributeType.HEALTH, mostDamagedArmorRuleset);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}