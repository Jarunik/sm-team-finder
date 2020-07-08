package org.slos.battle.abilities.rule.turn;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.AbilityEffect;
import org.slos.battle.attack.AttackContext;

public interface OnTurnEndRule extends AbilityEffect {
    void executeOnTurnEnd(AttackContext attackContext, GameContext gameContext);
}
