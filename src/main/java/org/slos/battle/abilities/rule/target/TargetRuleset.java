package org.slos.battle.abilities.rule.target;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TargetRuleset implements AbilityEffect {
    private List<TargetRule> ruleList;
    public static final TargetRuleset SELF;
    public static final TargetRuleset FRIENDLY;
    public static final TargetRuleset ENEMY;

    static {
        SELF = new TargetRuleset(Collections.singletonList(new TargetSelfRule()));
        FRIENDLY = new TargetRuleset(Collections.singletonList(new TargetFriendlyRule()));
        ENEMY = new TargetRuleset(Collections.singletonList(new TargetEnemyRule()));
    }

    public TargetRuleset(List<TargetRule> ruleList) {
        this.ruleList = ruleList;
    }

    public List<MonsterBattleStats> execute(MonsterBattleStats targeter, GameContext gameContext) {
        return execute(targeter, null, gameContext);
    }

    public List<MonsterBattleStats> execute(MonsterBattleStats targeter, List<MonsterBattleStats> suspects, GameContext gameContext) {

        List<MonsterBattleStats> processingSuspects = new ArrayList<>();
        if (suspects != null) {
            processingSuspects.addAll(suspects);
        }

        for (TargetRule targetRule : ruleList) {
            processingSuspects = targetRule.selectTargets(targeter, processingSuspects, gameContext);
        }

        return processingSuspects;
    }

    @Override
    public String toString() {
        return "TargetRuleset{" +
                "ruleList=" + ruleList +
                '}';
    }

    public static class Builder {
        private List<TargetRule> ruleList = new ArrayList<>();

        public Builder() {}

        public Builder addRule(TargetRule targetRule) {
            ruleList.add(targetRule);
            return this;
        }

        public TargetRuleset build() {
            return new TargetRuleset(ruleList);
        }
    }
}
