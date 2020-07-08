package org.slos.battle.abilities.rule.death;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.OnDeathRule;
import org.slos.battle.board.BoardSection;
import org.slos.battle.monster.MonsterBattleStats;

public class ResurrectRule implements OnDeathRule {
    private boolean hasConsumedRessurrect = false;

    @Override
    public void executeOnDeathEffect(MonsterBattleStats deadGuy, MonsterBattleStats guyWithAbility, GameContext gameContext) {
        gameContext.log("Checking for resurrect. " + hasConsumedRessurrect);
        BoardSection sectionOfResurrectHolder = guyWithAbility.getPlacedIn().getBoardSection();
        gameContext.log("--> " + sectionOfResurrectHolder);

        if (sectionOfResurrectHolder.containsMonsterBattleStats(deadGuy)) {
            if (!hasConsumedRessurrect) {
                gameContext.log("Resurrecting %1$s", deadGuy);
                deadGuy.getHealth().setValue(1);
                deadGuy.getHealth().setBuffValue(0);
                deadGuy.resetArmor();
//                gameContext.getBuffService().applyBuffsFor(deadGuy, gameContext);
                gameContext.getBuffService().reapplyBuffsOn(deadGuy, gameContext);
                gameContext.log("Done resurrecting %1$s", deadGuy.getId());

                hasConsumedRessurrect = true;
            }
        }
    }
}
