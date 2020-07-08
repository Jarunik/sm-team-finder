package org.slos.battle.abilities.attribute;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.HealStartRule;
import org.slos.battle.abilities.rule.target.TargetFriendlyRule;
import org.slos.battle.abilities.rule.target.TargetMostDamagedArmorRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.turn.OnTurnAbility;
import org.slos.battle.monster.BattleAttributeType;

public class RepairAbility extends Ability implements OnTurnAbility {
    public RepairAbility() {
        super(AbilityType.REPAIR, AbilityClassification.ON_TURN_START);
    }

    @Override
    public AbilityEffect getEffect() {
        TargetRuleset repairRuleset = new TargetRuleset.Builder()
                .addRule(new TargetFriendlyRule())
                .addRule(new TargetMostDamagedArmorRule())
                .build();

        return new HealStartRule(BattleAttributeType.ARMOR, 2, repairRuleset);
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return TargetRuleset.SELF;
    }
}