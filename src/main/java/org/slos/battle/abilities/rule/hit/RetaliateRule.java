package org.slos.battle.abilities.rule.hit;

import org.slos.battle.GameContext;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.attack.AttackService;
import org.slos.battle.decision.Choice;
import org.slos.battle.decision.ChoiceGate;
import org.slos.battle.decision.ChoiceGateFactory;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;

public class RetaliateRule extends OnHitRule {

    @Override
    public Void execute(AttackContext attackContext, GameContext gameContext) {
        if (attackContext.getDamageType() == DamageType.ATTACK) {

            ChoiceGateFactory.Configuration configuration = new ChoiceGateFactory.Configuration()
                    .setGateId("RETALIATE")
                    .addChoice(new Choice(1, 1, true))
                    .addChoice(new Choice(2, 1, false))
                    .setChoiceMode(gameContext.getChoiceStrategyMode());

            ChoiceGate<Boolean> shouldRetaliate = new ChoiceGateFactory().buildGate(configuration);
            MonsterBattleStats attacker = attackContext.getAttacker();

            if ((shouldRetaliate.getResult()) && (!attacker.isDead())) {
                gameContext.log("Retaliating: %1$s on %2$s", attackContext.getTarget().getId(), attackContext.getAttacker().getId());

                MonsterBattleStats target = attackContext.getTarget();
                AttackService attackService = gameContext.getAttackService();

                attackService.attackSpecific(target, DamageType.ATTACK, attacker, gameContext, true);
            }
            else {
                gameContext.log("Not retaliating: %1$s", attackContext.getTarget().getId());
            }
        }

        return null;
    }
}
