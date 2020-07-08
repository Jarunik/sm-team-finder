package org.slos.battle.abilities.rule.hit;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.attack.AttackContext;
import org.slos.battle.decision.Choice;
import org.slos.battle.decision.ChoiceGate;
import org.slos.battle.decision.ChoiceGateFactory;
import org.slos.battle.decision.strategy.SingletonChoiceGate;
import org.slos.battle.monster.MonsterBattleStats;

import java.util.ArrayList;
import java.util.List;

public class ApplyOnHitRule extends OnAttackRule {
    private Ability abilityToApply;
    private Integer chanceToApply;
    private TargetRuleset applyTo = null;

    public ApplyOnHitRule(Ability abilityToApply, Integer chanceToApply, TargetRuleset applyTo) {
        super();
        this.abilityToApply = abilityToApply;
        this.chanceToApply = chanceToApply;
        this.applyTo = applyTo;
    }

    public ApplyOnHitRule(Ability abilityToApply, Integer chanceToApply) {
        this(abilityToApply, chanceToApply, null);
    }

    @Override
    public Void execute(AttackContext attackContext, GameContext gameContext) {
        List<MonsterBattleStats> targets = new ArrayList<>();

        if (applyTo != null) {
            targets.addAll(applyTo.execute(attackContext.getTarget(), null, gameContext));
        }
        else {
            targets.add(TargetRuleset.SELF.execute(attackContext.getTarget(), null, gameContext).get(0));
        }

        ChoiceGate<Boolean> applyEffect = getAppliesChanceChoiceGate(gameContext);

        for (MonsterBattleStats target : targets) {
            if (applyEffect.getResult()) {
                gameContext.log("Applying On Hit: %1$s", abilityToApply);
                target.getAbilities().add(abilityToApply);
            }
        }

        return null;
    }

    private ChoiceGate<Boolean> getAppliesChanceChoiceGate(GameContext gameContext) {
        if (chanceToApply == 100) {
            return new SingletonChoiceGate<>(true);
        }

        ChoiceGateFactory.Configuration<Boolean> appliesPoisionChoices = new ChoiceGateFactory.Configuration<>();
        appliesPoisionChoices
                .setGateId(abilityToApply.getAbilityType().toString())
                .setChoiceMode(gameContext.getChoiceStrategyMode())
                .addChoice(new Choice<>(1,chanceToApply, true))
                .addChoice(new Choice<>(2,100 - chanceToApply, false));
        return (ChoiceGate<Boolean>) gameContext.getMasterChoiceContext().getChoiceGateFactory().buildGate(appliesPoisionChoices);
    }
}
