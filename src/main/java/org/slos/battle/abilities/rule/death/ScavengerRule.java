package org.slos.battle.abilities.rule.death;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.OnDeathRule;
import org.slos.battle.monster.MonsterBattleStats;

public class ScavengerRule implements OnDeathRule {

    @Override
    public void executeOnDeathEffect(MonsterBattleStats deadGuy, MonsterBattleStats guyWithAbility, GameContext gameContext) {
        gameContext.log("Scavenger applying on: %1$s", guyWithAbility.getId());

        if (deadGuy != guyWithAbility) {
            guyWithAbility.getHealth().addToBaseValue(1);
//            guyWithAbility.getHealth().addToValue(1);
        }
    }
}
