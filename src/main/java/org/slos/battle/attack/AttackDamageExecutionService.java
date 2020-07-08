package org.slos.battle.attack;

import org.slos.battle.GameContext;
import org.slos.battle.StatChangeContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.rule.AttackRule;
import org.slos.battle.abilities.rule.OnBattleAttributeChangeRule;
import org.slos.battle.abilities.rule.attack.DamageRule;
import org.slos.battle.monster.BattleAttributeType;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.monster.DamageType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttackDamageExecutionService {
    public void attack(AttackContext attackContext, GameContext gameContext) {
//        Integer damage = figureActualAttackDamage(attackContext, gameContext);
//        AttackContext actualDamage = new AttackContext(attackContext.getAttacker(), attackContext.getTarget(), attackContext.getDamageType(), damage, attackContext.getAttackRuleset());

        applyDamage(attackContext, gameContext);

        executeAttackRules(attackContext, gameContext);
    }

    private void executeAttackRules(AttackContext actualDamage, GameContext gameContext) {
        MonsterBattleStats target = actualDamage.getTarget();
        MonsterBattleStats attacker = actualDamage.getAttacker();

        boolean targetDied = target.isDead();
        executePostAttackRules(actualDamage, gameContext);

        if ((target != null) && (targetDied)) {
            executeAttackRules(actualDamage.getAttackRuleset().getOnKillingHitRules(), actualDamage, gameContext);
        }

        executeAttackRules(actualDamage.getAttackRuleset().getAfterAttackRules(), actualDamage, gameContext);

        if ((attacker != null) && (attacker.isDead())) {
            gameContext.getDeathService().registerDeath(attacker, actualDamage);
        }
        if ((target != null) && (target.isDead())) {
            gameContext.getDeathService().registerDeath(target, actualDamage);
        }
    }

    private void executePostAttackRules(AttackContext attackContext, GameContext gameContext) {
        executeAttackRules(attackContext.getAttackRuleset().getOnAttackRules(), attackContext, gameContext);
        executeAttackRules(attackContext.getAttackRuleset().getOnHitRules(), attackContext, gameContext);
    }

    public void executeAttackRules(List<? extends AttackRule> rules, AttackContext attackContext, GameContext gameContext) {
        for (AttackRule attackRule : rules) {
            attackRule.execute(attackContext, gameContext);
        }
    }

    public void applyDamage(AttackContext attackContext, GameContext gameContext) {
        Integer damage = figureActualAttackDamage(attackContext, gameContext);
        AttackContext actualDamage = new AttackContext(attackContext.getAttacker(), attackContext.getTarget(), attackContext.getDamageType(), damage, attackContext.getAttackRuleset());

        MonsterBattleStats target = attackContext.getTarget();

        if (target == null) {
            return;
        }

        switch (attackContext.getDamageType()) {
            case DamageType.MAGIC:
                applyMagicDamage(actualDamage, gameContext);
                break;
            case DamageType.RANGED:
                applyRangedDamage(actualDamage, gameContext);
                break;
            case DamageType.ATTACK:
                applyAttackDamage(actualDamage, gameContext);
                break;
            case DamageType.SYSTEM:
                dealHealthDamageAndTriggerAttributeChangeAbilities(actualDamage, gameContext);
                break;
        }
    }

    private Integer figureActualAttackDamage(AttackContext attackContext, GameContext gameContext) {
        List<DamageRule> damageRules = new ArrayList<>();

        gameContext.getAttackService().extractAndApplyAttackRules(attackContext.getTarget(), false, damageRules, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        gameContext.getAttackService().extractAndApplyAttackRules(attackContext.getAttacker(), true, damageRules, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        Integer damage = attackContext.getDamageValue();

        for (DamageRule damageRule : damageRules) {
            damage = damageRule.figureDamage(attackContext, gameContext, damage);
        }

        return damage;
    }

    private void applyAttackDamage(AttackContext attackContext, GameContext gameContext) {
        applyDefaultDamage(attackContext, gameContext);
    }

    private void applyRangedDamage(AttackContext attackContext, GameContext gameContext) {
        applyDefaultDamage(attackContext, gameContext);
    }

    public void applyDefaultDamage(AttackContext attackContext, GameContext gameContext) {
        MonsterBattleStats target = attackContext.getTarget();

        if ((target.getArmor().getValue() > 0) && (!gameContext.getGameRules().contains(GameRuleType.UNPROTECTED))) {
            Integer amountOfDamage = attackContext.getDamageValue();

            Integer startArmor = target.getArmor().getValue();
            if (target.getArmor().getValue() > amountOfDamage) {
                target.getArmor().removeFromValue(amountOfDamage);
            }
            else {
                target.getArmor().removeFromValue(target.getArmor().getValue());
            }
            gameContext.log("Removed %1$s armor from: %2$s", startArmor - target.getArmor().getValue(), target);

            Integer endArmor = target.getArmor().getValue();
            if (endArmor <= 0) {
                endArmor = 0;
            }
            triggerAttributeChangeAbilities(attackContext, BattleAttributeType.HEALTH, (startArmor - endArmor), attackContext.getDamageType(), gameContext);

            if ((attackContext.getAttacker() != null) && (attackContext.getAttacker().containsAbility(AbilityType.PIERCING))) {
                if (startArmor < amountOfDamage) {
                    AttackContext pierce = new AttackContext(attackContext.getAttacker(), target, attackContext.getDamageType(), amountOfDamage - startArmor, attackContext.getAttackRuleset());
                    dealHealthDamageAndTriggerAttributeChangeAbilities(pierce, gameContext);
                }
            }
        }
        else {
            dealHealthDamageAndTriggerAttributeChangeAbilities(attackContext, gameContext);
        }
    }

    private void applyMagicDamage(AttackContext attackContext, GameContext gameContext) {
        if (gameContext.getGameRules().contains(GameRuleType.WEAK_MAGIC)) {
            applyDefaultDamage(attackContext, gameContext);
        }
        else {
            dealHealthDamageAndTriggerAttributeChangeAbilities(attackContext, gameContext);
        }
    }

    public void dealHealthDamageAndTriggerAttributeChangeAbilities(AttackContext attackContext, GameContext gameContext) {
        MonsterBattleStats target = attackContext.getTarget();
        Integer amount = attackContext.getDamageValue();
        Integer startHealth = target.getHealth().getValue();

        target.getHealth().removeFromValue(amount);

        gameContext.log("Removed %1$s health from: %2$s", amount, target);

        Integer endHealth = target.getHealth().getValue();
        if (endHealth <= 0) {
            endHealth = 0;
//            gameContext.getDeathService().registerDeath(target, attackContext); //TODO: This belong here?
        }
        triggerAttributeChangeAbilities(attackContext, BattleAttributeType.HEALTH, (startHealth - endHealth), attackContext.getDamageType(), gameContext);
    }

    private void triggerAttributeChangeAbilities(AttackContext attackContext, BattleAttributeType attributeType, Integer amount, DamageType damageType, GameContext gameContext) {
        MonsterBattleStats attacker = attackContext.getAttacker();
        MonsterBattleStats target = attackContext.getTarget();

        List<Ability> onBattleStatChangeAbilities =  target.getAbilities().stream()
                .filter(ability -> ability.containsClassification(AbilityClassification.ON_BATTLE_STAT_CHANGE))
                .collect(Collectors.toList());

        StatChangeContext statChangeContext = new StatChangeContext(attacker, target, attributeType, amount);
        for (Ability ability : onBattleStatChangeAbilities) {
            ((OnBattleAttributeChangeRule)ability.getEffect()).executeStatChangeRule(statChangeContext, damageType, gameContext);
        }
    }
}
