package org.slos.battle.abilities.rule.death;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.rule.OnDeathRule;
import org.slos.battle.abilities.rule.turn.OnTurnStartRule;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;

public class LastStandRule implements OnDeathRule, OnTurnStartRule {
    boolean alreadyTriggered = false;

    @Override
    public void executeOnDeathEffect(MonsterBattleStats deadGuy, MonsterBattleStats guyWithAbility, GameContext gameContext) {
        boolean activateLastStand = true;
        for (MonsterBattleStats monsterBattleStats : guyWithAbility.getPlacedIn().getBoardSection().peekMonsterBattleStats()) {
            if (!(monsterBattleStats.getId().equals(guyWithAbility.getId())) && (!monsterBattleStats.isDead())) {
                activateLastStand = false;
            }
        }

//        if (guyWithAbility.getPlacedIn().getBoardSection().peekMonsterBattleStats().size() == 1) {
        if ((!alreadyTriggered) && (activateLastStand)) {
            StringBuffer stringBuffer = new StringBuffer();

            int healthChange = (int) Math.ceil(guyWithAbility.getHealth().getBaseValue() / 2f);
            int speedChange = (int) Math.ceil(guyWithAbility.getSpeed().getValue() / 2f);

            stringBuffer.append("Last Stand Change[" + guyWithAbility.getId() + "]: " + healthChange + " health | " + speedChange + " speed | ");
            alreadyTriggered = true;

            guyWithAbility.getHealth().addToBuffValue(healthChange);
            guyWithAbility.getSpeed().addToBuffValue(speedChange);
            for (DamageType damageType : guyWithAbility.getDamageValues().keySet()) {
                int damageChange = (int) Math.ceil(guyWithAbility.getDamageValue(damageType).getValue() / 2f);
                guyWithAbility.getDamageValue(damageType).addToBuffValue(damageChange);

                stringBuffer.append(damageChange + " damage(" + damageType + ") ");
            }

            gameContext.log(stringBuffer.toString());
        }
    }

    @Override
    public boolean executeOnTurnStart(MonsterBattleStats monsterBattleStats, GameContext gameContext) {
        executeOnDeathEffect(null, monsterBattleStats, gameContext);

        return true;
    }
}