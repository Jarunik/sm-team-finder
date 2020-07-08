package org.slos.battle.abilities.buff;

import org.slos.battle.monster.MonsterBattleStats;

public class BuffApplication {
    private Buff buff;
    private MonsterBattleStats appliedBy;
    private MonsterBattleStats appliedTo;

    public BuffApplication(Buff buff, MonsterBattleStats appliedBy, MonsterBattleStats appliedTo) {
        this.buff = buff;
        this.appliedBy = appliedBy;
        this.appliedTo = appliedTo;
    }

    public Buff getBuff() {
        return buff;
    }

    public MonsterBattleStats getAppliedBy() {
        return appliedBy;
    }

    public MonsterBattleStats getAppliedTo() {
        return appliedTo;
    }

    @Override
    public String toString() {
        return "BuffApplication{" +
                "buff=" + buff +
                ", appliedBy=" + appliedBy +
                ", appliedTo=" + appliedTo +
                '}';
    }
}
