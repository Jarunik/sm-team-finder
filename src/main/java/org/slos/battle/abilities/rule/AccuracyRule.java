package org.slos.battle.abilities.rule;

public abstract class AccuracyRule extends AttackRule<Integer> {
    public AccuracyRule() {
        super(AttackRuleType.ACCURACY);
    }

//    abstract Integer modifyAccuracyValue(MonsterBattleStats attacker, MonsterBattleStats target, GameContext gameContext);
}
