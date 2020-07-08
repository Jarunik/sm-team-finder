package org.slos.battle.abilities.attack;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.rule.target.TargetEnemyRule;
import org.slos.battle.abilities.rule.target.TargetFirstRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.target.HighestPriorityTargetRuleset;
import org.slos.battle.abilities.target.TargetRulesetAbility;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TargetSelectionService {

    public MonsterBattleStats getTargetFor(MonsterBattleStats attacker, DamageType damageType, GameContext gameContext) {
        TargetRuleset targetRuleset = null;

        if (attacker.getPlacedIn().getLocation() == 1) {
            targetRuleset = getDfaultAttackRuleset();
        }
        else {
            Optional<TargetRulesetAbility> highestPriorityTargetRuleset = attacker.getAbilities().stream()
                    .filter(ability -> ability.containsClassification(AbilityClassification.TARGET_CONDITION))
                    .map(ability -> (TargetRulesetAbility) ability.getEffect())
                    .filter(Objects::nonNull)
                    .max(new HighestPriorityTargetRuleset());

            if (highestPriorityTargetRuleset.isPresent()) {
                TargetRulesetAbility targetRulesetByAbility = highestPriorityTargetRuleset.get();
                TargetRuleset targetRuleset1 = targetRulesetByAbility.getTargetRuleset();

                gameContext.log("Target Ruleset: %1$s", targetRuleset1);

                List<MonsterBattleStats> target = targetRuleset1.execute(attacker, null, gameContext);

                gameContext.log("Targets: %1$s", target);

                if ((target != null) && (target.size() > 0)) {
                    gameContext.log("Found target: %1$s", target.get(0));
                    return target.get(0);
                }
            }
        }

        if (targetRuleset == null) {
            targetRuleset = getDfaultAttackRuleset();
        }

        List<MonsterBattleStats> targets = targetRuleset.execute(attacker, null, gameContext);

        if ((targets == null) || (targets.size() == 0)) {
            return null;
        }
        return targets.get(0);
    }

    private TargetRuleset getDfaultAttackRuleset() {
        return new TargetRuleset.Builder()
                .addRule(new TargetEnemyRule())
                .addRule(new TargetFirstRule())
                .build();
    }
}
