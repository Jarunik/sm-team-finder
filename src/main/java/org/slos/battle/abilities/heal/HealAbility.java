package org.slos.battle.abilities.heal;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.HealStartRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.turn.OnTurnAbility;
import org.slos.battle.monster.BattleAttributeType;

public class HealAbility extends Ability implements OnTurnAbility {
    public HealAbility() {
        super(AbilityType.HEAL, AbilityClassification.ON_TURN_START);
    }

    @Override
    public AbilityEffect getEffect() {
        return new HealStartRule(BattleAttributeType.HEALTH, TargetRuleset.SELF);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}