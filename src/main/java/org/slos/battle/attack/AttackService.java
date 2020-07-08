package org.slos.battle.attack;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AttackCondition;
import org.slos.battle.abilities.attack.TargetSelectionService;
import org.slos.battle.abilities.rule.AccuracyRule;
import org.slos.battle.abilities.rule.AfterAttackRule;
import org.slos.battle.abilities.rule.AttackRule;
import org.slos.battle.abilities.rule.AttackRuleset;
import org.slos.battle.abilities.rule.attack.DamageRule;
import org.slos.battle.abilities.rule.attack.OnAttackRule;
import org.slos.battle.abilities.rule.hit.OnHitRule;
import org.slos.battle.abilities.rule.hit.OnKillingHitRule;
import org.slos.battle.decision.Choice;
import org.slos.battle.decision.ChoiceGate;
import org.slos.battle.decision.ChoiceGateFactory;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.monster.DamageType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttackService {
    private AttackDamageExecutionService attackDamageExecutionService = new AttackDamageExecutionService();
    private TargetSelectionService targetSelectionService = new TargetSelectionService();

    public AttackContext attack(MonsterBattleStats attacker, DamageType damageType, GameContext gameContext) {
        MonsterBattleStats target = targetSelectionService.getTargetFor(attacker, damageType, gameContext);

        if (target != null) {
            return checkHitAndExecuteAttack(attacker, damageType, target, gameContext, false);
        }

        return null;
    }

    public AttackContext attackSpecific(MonsterBattleStats attacker, DamageType damageType, MonsterBattleStats target, GameContext gameContext) {
        return attackSpecific(attacker, damageType, target, gameContext, false);
    }

    public AttackContext attackSpecific(MonsterBattleStats attacker, DamageType damageType, MonsterBattleStats target, GameContext gameContext, boolean forceAttack) {
        return checkHitAndExecuteAttack(attacker, damageType, target, gameContext, forceAttack);
    }

    private AttackContext checkHitAndExecuteAttack(MonsterBattleStats attacker, DamageType damageType, MonsterBattleStats target, GameContext gameContext, boolean forceAttack) {
        AttackContext attackContext = getAttackContextFor(attacker, damageType,  target);
        gameContext.log("Attack Context: %1$s  --  %2$s  --  %3$s", attackContext, gameContext.getBuffService().getBuffsAppliedTo(attacker), gameContext.getBuffService().getBuffsAppliedTo(target));

        if (attackerHit(attackContext, gameContext)) {
            if ((canAttack(attackContext, damageType, gameContext) || (forceAttack))) {
                gameContext.registerAttack(attackContext);
                executeAttack(attackContext, gameContext);
            }
        }

        return attackContext;
    }

    public AttackDamageExecutionService getAttackDamageExecutionService() {
        return attackDamageExecutionService;
    }

    private boolean attackerHit(AttackContext attackContext, GameContext gameContext) {
        if (gameContext.getGameRules().contains(GameRuleType.AIM_TRUE)) {
            if ((attackContext.getDamageType() == DamageType.RANGED) || (attackContext.getDamageType() == DamageType.ATTACK)) {
                return true;
            }
        }
        Integer accuracyModifier = figureAccuracyModifier(attackContext, gameContext);
        Integer accuracy = getAccuracy(attackContext, accuracyModifier, gameContext);

        if (accuracy == 20) {
            return true;
        }
        if (accuracy == 0) {
            return false;
        }

        gameContext.log("Hit Chance: " + accuracy);
        ChoiceGate<Boolean> didHit = getBooleanChoiceGate(gameContext, accuracy);
        Boolean didHitResult = didHit.getResult();
        gameContext.log("Did hit: %1$s", didHitResult);

        return didHitResult;
    }

    private int getAccuracy(AttackContext attackContext, Integer abilityAccuracyModifier, GameContext gameContext) {
        MonsterBattleStats attacker = attackContext.getAttacker();
        int attackerAccuracy = attacker.getBaseHitChance().getValue();
        int attackSpeedModifier = getAttackSpeedModifier(attackContext, gameContext);
        int accuracy = (attackerAccuracy + abilityAccuracyModifier + attackSpeedModifier);

        if (accuracy > 100) {
            accuracy = 100;
        } else if (accuracy < 0) {
            accuracy = 0;
        }

        return accuracy / 5;
    }

    private int getAttackSpeedModifier(AttackContext attackContext, GameContext gameContext) {
        MonsterBattleStats attacker = attackContext.getAttacker();
        MonsterBattleStats target = attackContext.getTarget();
        DamageType damageType = attackContext.getDamageType();

        int attackSpeedModifier = 0;
        if (!(damageType == DamageType.MAGIC)) {
            int attackerSpeed = attacker.getSpeed().getValue();
            int targetSpeed = target.getSpeed().getValue();
            int speedDifference = targetSpeed - attackerSpeed;

            attackSpeedModifier = speedDifference * 10;

            if (gameContext.hasRule(GameRuleType.REVERSE_SPEED)) {
                attackSpeedModifier = attackSpeedModifier * -1;
            }

            if (attackSpeedModifier < 0) {
                attackSpeedModifier = 0;
            }
        }

        return attackSpeedModifier * (-1);
    }

    private ChoiceGate<Boolean> getBooleanChoiceGate(GameContext gameContext, Integer accuracy) {

        ChoiceGateFactory.Configuration choiceGateConfiguration = new ChoiceGateFactory.Configuration<Boolean>()
                .setGateId("ATTACK")
                .setChoiceMode(gameContext.getChoiceStrategyMode())
                .addChoice(new Choice<>(1, accuracy, Boolean.valueOf(true)))
                .addChoice(new Choice<>(2, 20 - accuracy, Boolean.valueOf(false)));

        return (ChoiceGate<Boolean>) gameContext.getMasterChoiceContext().getChoiceGateFactory().buildGate(choiceGateConfiguration);
    }

    private Integer figureAccuracyModifier(AttackContext attackContext, GameContext gameContext) {
        Integer accuracyModifier = 0;

        List<Ability> attackerAccuracyAbilities = attackContext.getAttacker().getAbilities().stream()
                .filter(ability -> ability.containsClassification(AbilityClassification.ATTACKER_ACCURACY))
                .collect(Collectors.toList());

        for (Ability ability : attackerAccuracyAbilities) {
            AccuracyRule attackAccuracyRule = (AccuracyRule) ability.getEffect();
            accuracyModifier += attackAccuracyRule.execute(attackContext, gameContext);
        }

        List<Ability> targetAccuracyAbilities = attackContext.getTarget().getAbilities().stream()
                .filter(ability -> ability.containsClassification(AbilityClassification.TARGET_EVADE))
                .collect(Collectors.toList());

        for (Ability ability : targetAccuracyAbilities) {
            AccuracyRule attackAccuracyRule = (AccuracyRule) ability.getEffect();
            accuracyModifier += attackAccuracyRule.execute(attackContext, gameContext);
        }

        return accuracyModifier;
    }

    public AttackContext getAttackContextFor(MonsterBattleStats attacker, DamageType damageType, MonsterBattleStats target) {
        AttackRuleset attackRuleset = getAttackRuleset(attacker, target);

        AttackContext attackContext = new AttackContext(
                attacker,
                target,
                damageType,
                attacker.getDamageValue(damageType).getValue(),
                attackRuleset
        );

        return attackContext;
    }

    private void executeAttack(AttackContext attackContext, GameContext gameContext) {
        attackDamageExecutionService.attack(attackContext, gameContext);
    }

    public AttackRuleset getAttackRuleset(MonsterBattleStats attacker, MonsterBattleStats target) {
        List<DamageRule> damageRules = new ArrayList<>();
        List<OnAttackRule> onAttackRules = new ArrayList<>();
        List<OnHitRule> onHitRules = new ArrayList<>();
        List<OnKillingHitRule> onKillingHitRules = new ArrayList<>();
        List<AfterAttackRule> afterAttackRules = new ArrayList<>();

        if (attacker != null) {
            extractAndApplyAttackRules(attacker, true, damageRules, onAttackRules, null, onKillingHitRules, afterAttackRules);
        }
        if (target != null) {
            extractAndApplyAttackRules(target, false, damageRules, null, onHitRules, onKillingHitRules, afterAttackRules);
        }

        return new AttackRuleset(
                damageRules,
                onAttackRules,
                onHitRules,
                onKillingHitRules,
                afterAttackRules
        );
    }

    public void extractAndApplyAttackRules(MonsterBattleStats monsterBattleStats, boolean isFromTheAttacker, List<DamageRule> damageRules, List<OnAttackRule> onAttackRules, List<OnHitRule> onHitRules, List<OnKillingHitRule> onKillingHitRules, List<AfterAttackRule> afterAttackRules) {
        if ((monsterBattleStats == null) || (monsterBattleStats.getAbilities() == null)) {
            return;
        }

        List<Ability> onAttackAbilities = monsterBattleStats.getAbilities().stream()
                .filter(ability -> ability.containsClassification(AbilityClassification.ATTACK))
                .collect(Collectors.toList());

        extractAndAddAttackRulesFor(
                onAttackAbilities,
                isFromTheAttacker,
                damageRules,
                onAttackRules,
                onHitRules,
                onKillingHitRules,
                afterAttackRules);
    }

    //TODO: refactor out the damage rules
    private void extractAndAddAttackRulesFor(List<Ability> onAttackAbilities, boolean isFromTheAttacker, List<DamageRule> damageRules, List<OnAttackRule> onAttackRules, List<OnHitRule> onHitRules, List<OnKillingHitRule> onKillingHitRules, List<AfterAttackRule> afterAttackRules) {
        for (Ability ability : onAttackAbilities) {
            AttackRule attackRule = (AttackRule) ability.getEffect();

            switch(attackRule.getAttackRuleType()) {
                case ATTACKER_DAMAGE:
                    if (isFromTheAttacker) {
                        damageRules.add((DamageRule) attackRule);
                    }
                    break;
                case TARGET_DAMAGE:
                    if (!isFromTheAttacker) {
                        damageRules.add((DamageRule) attackRule);
                    }
                    break;
                case ON_ATTACK:
                    if (isFromTheAttacker) {
                        onAttackRules.add((OnAttackRule) attackRule);
                    }
                    break;
                case ON_HIT:
                    if (!isFromTheAttacker) {
                        onHitRules.add((OnHitRule) attackRule);
                    }
                    break;
                case AFTER_ATTACK:
                    afterAttackRules.add((AfterAttackRule) attackRule);
                    break;
                case ON_KILL:
                    onKillingHitRules.add((OnKillingHitRule) attackRule);
                    break;
            }
        }
    }



    private Boolean canAttack(AttackContext attackContext, DamageType damageType, GameContext gameContext) {
        MonsterBattleStats attacker = attackContext.getAttacker();
        MonsterBattleStats target = attackContext.getTarget();

        if (damageType == DamageType.NONE) {
            return false;
        }

        if ((damageType == DamageType.ATTACK) && (gameContext.hasRule(GameRuleType.MELEE_MAYHEM))) {
            return true;
        }

        if (damageType == DamageType.RANGED && (gameContext.hasRule(GameRuleType.CLOSE_RANGE))) {
            return true;
        }

        if (attacker.getAbilities().size() > 0) {
            Boolean canAttackByAbilities = canAttackByAbilities(gameContext, attacker, target);
            if (canAttackByAbilities != null) {
                return canAttackByAbilities;
            }
        }

        if (damageType == DamageType.MAGIC) {
            return true;
        }

        if (damageType == DamageType.RANGED) {
            return attacker.getPlacedIn().getLocation() != 1;
        }

        if (damageType == DamageType.ATTACK) {
            if (attacker.getPlacedIn().getLocation() == 1) {
                return true;
            }
            return false;
        }

        throw new IllegalStateException("Exception in determining if monster can attackContext: " + attacker);
    }

    private Boolean canAttackByAbilities(GameContext gameContext, MonsterBattleStats attacker, MonsterBattleStats target) {
        Boolean canAttackFromAbilities = null;

        Ability[] abilities = attacker.getAbilities().toArray(new Ability[0]);
        for (Ability ability : abilities) {
            if (ability.containsClassification(AbilityClassification.ATTACK_CONDITION)) {
                AttackCondition attackCondition = (AttackCondition) ability.getEffect();
                if (canAttackFromAbilities == null) {
                    canAttackFromAbilities = attackCondition.canAttack(attacker, target, gameContext);
                }
                else {
                    canAttackFromAbilities = (canAttackFromAbilities || attackCondition.canAttack(attacker, target, gameContext));
                }
            }
        }

        return canAttackFromAbilities;
    }
}
