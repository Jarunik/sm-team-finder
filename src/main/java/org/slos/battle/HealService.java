package org.slos.battle;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.monster.BattleAttributeType;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.GameRuleType;

public class HealService {

    public void heal(StatChangeContext statChangeContext, GameContext gameContext) {
        if (!(statChangeContext.getBattleAttributeType() == BattleAttributeType.HEALTH)) {
            throw new IllegalStateException("Heal service is limited to the Health battle attribute.");
        }

        if (gameContext.getGameRules().contains(GameRuleType.HEALED_OUT)) {
            return;
        }

        MonsterBattleStats gettingHealed = statChangeContext.getTarget();

        boolean isAfflicted = false;
        for (Ability ability : gettingHealed.getAbilities()) {
            if (ability.getAbilityType() == AbilityType.AFFLICTED) {
                isAfflicted = true;
            }
        }

        if (!isAfflicted) {
            Integer toHealAmount = statChangeContext.getAmount();
            Integer differenceFromCurrentToBaseHealth = (gettingHealed.getHealth().getBaseValue() + gettingHealed.getHealth().getBuffBaseValue()) - gettingHealed.getHealth().getValue();

            if (toHealAmount > differenceFromCurrentToBaseHealth) {
                toHealAmount = differenceFromCurrentToBaseHealth;
            }

            gameContext.log("Healing [%1$s] amount [%2$s]", gettingHealed.getId(), toHealAmount);
            gettingHealed.getHealth().addToValue(toHealAmount);
        }
    }
}
