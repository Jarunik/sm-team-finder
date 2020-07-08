package org.slos.battle.abilities.target;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.AttackCondition;
import org.slos.battle.abilities.rule.target.SneakTargetRuleset;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.rule.target.TargetSelfRule;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.Collections;

public class SneakAbility extends Ability implements AttackCondition {

    public SneakAbility() {
        super(AbilityType.SNEAK, AbilityClassification.TARGET_CONDITION, AbilityClassification.ATTACK_CONDITION);
    }

    @Override
    public AbilityEffect getEffect() {
        return new SneakTargetRuleset();
    }

    @Override
    public boolean canAttack(MonsterBattleStats attacker, MonsterBattleStats target, GameContext gameContext) {
        return true;
    }

    @Override
    public TargetRuleset getTargetRuleset() {
        return new TargetRuleset(Collections.singletonList(new TargetSelfRule()));
    }
}