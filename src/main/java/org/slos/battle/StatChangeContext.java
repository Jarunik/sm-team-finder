package org.slos.battle;

import org.slos.battle.monster.BattleAttributeType;
import org.slos.battle.monster.MonsterBattleStats;

public class StatChangeContext {
    private MonsterBattleStats initiator;
    private MonsterBattleStats target;
    private BattleAttributeType battleAttributeType;
    private Integer amount;

    public StatChangeContext(MonsterBattleStats initiator, MonsterBattleStats target, BattleAttributeType battleAttributeType, Integer amount) {
        this.initiator = initiator;
        this.target = target;
        this.battleAttributeType = battleAttributeType;
        this.amount = amount;
    }

    public MonsterBattleStats getInitiator() {
        return initiator;
    }

    public MonsterBattleStats getTarget() {
        return target;
    }

    public Integer getAmount() {
        return amount;
    }

    public BattleAttributeType getBattleAttributeType() {
        return battleAttributeType;
    }

    @Override
    public String toString() {
        return "StatChangeContext{" +
                "initiator=" + initiator +
                ", target=" + target +
                ", battleAttributeType=" + battleAttributeType +
                ", amount=" + amount +
                '}';
    }
}
