package org.slos.battle.abilities.rule;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.buff.BattleAttributeBuff;
import org.slos.battle.abilities.buff.BuffApplication;
import org.slos.battle.abilities.buff.BuffEffectType;
import org.slos.battle.abilities.buff.BuffService;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.rule.turn.OnTurnStartRule;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.List;

public class CleanseTurnStartRule implements OnTurnStartRule {
    private final TargetRuleset targetRuleset;

    public CleanseTurnStartRule(TargetRuleset targetRuleset) {
        this.targetRuleset = targetRuleset;
    }

    @Override
    public boolean executeOnTurnStart(MonsterBattleStats monsterBattleStats, GameContext gameContext) {
        List<MonsterBattleStats> targets = targetRuleset.execute(monsterBattleStats, gameContext);

        if ((targets != null) && (targets.size() > 0)) {
            MonsterBattleStats tank = targets.get(0);
            gameContext.log("Checking for cleanse application on: " + tank);

            Ability[] abilities = tank.getAbilities().toArray(new Ability[0]);
            for (Ability ability : abilities) {
                switch (ability.getAbilityType()) {
                    case STUNNED:
                    case POISON:
                    case AFFLICTED:
                        tank.getAbilities().remove(ability);
                        gameContext.log("Cleansed " + tank.getId() + " of: " + ability);
                        break;
                    default:
                        break;
                }
            }

            BuffService buffService = gameContext.getBuffService();
            List<BuffApplication> buffsOnTank = buffService.getBuffsAppliedTo(tank);

            if (buffsOnTank != null) {
                BuffApplication[] buffsOnTankArray = buffsOnTank.toArray(new BuffApplication[buffsOnTank.size()]);
                for (BuffApplication buffApplication : buffsOnTankArray) {
                    if (buffApplication.getBuff().getBuffEffectType() == BuffEffectType.BATTLE_ATTRIBUTE) {
                        BattleAttributeBuff battleAttributeBuff = (BattleAttributeBuff) buffApplication.getBuff();

                        if (battleAttributeBuff.getBuffQuantity() < 0) {
                            buffService.removeBuff(buffApplication, gameContext);
                            gameContext.log("Removed buff application: " + buffApplication);
                        }
                    }
                }
            }
        }
        else {
            throw new IllegalStateException("There is no tank for this Cleanse action.");
        }

        return true;
    }
}
