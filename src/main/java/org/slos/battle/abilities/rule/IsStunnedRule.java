package org.slos.battle.abilities.rule;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.turn.OnTurnStartRule;
import org.slos.battle.monster.MonsterBattleStats;

public class IsStunnedRule implements OnTurnStartRule {

    @Override
    public boolean executeOnTurnStart(MonsterBattleStats monsterBattleStats, GameContext gameContext) {
        Ability[] abilities = monsterBattleStats.getAbilities().toArray(new Ability[0]);

        for (Ability ability : abilities) {
            if (ability.getAbilityType() == AbilityType.STUNNED) {
                monsterBattleStats.getAbilities().remove(ability);
            }
        }
        return false;
    }
}
