package org.slos.battle.abilities.rule;

import org.slos.battle.GameContext;
import org.slos.battle.HealService;
import org.slos.battle.StatChangeContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.rule.target.TargetRuleset;
import org.slos.battle.abilities.rule.turn.OnTurnStartRule;
import org.slos.battle.monster.BattleAttributeType;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;

import java.util.List;
import java.util.stream.Collectors;

public class HealStartRule implements OnTurnStartRule {
    private final BattleAttributeType attributeType;
    private final Integer healAmount;
    private final TargetRuleset targetRuleset;
    private HealService healService = new HealService();

    public HealStartRule(BattleAttributeType attribute, Integer healAmount, TargetRuleset targetRuleset) {
        this.attributeType = attribute;
        this.healAmount = healAmount;
        this.targetRuleset = targetRuleset;
    }

    public HealStartRule(BattleAttributeType attribute, TargetRuleset targetRuleset) {
        this(attribute, null, targetRuleset);
    }

    @Override
    public boolean executeOnTurnStart(MonsterBattleStats healer, GameContext gameContext) {
        gameContext.log("Heal");
        List<MonsterBattleStats> targets = targetRuleset.execute(healer, gameContext);

        if ((targets != null) && (targets.size() > 0)) {
            for (MonsterBattleStats monsterBattleStats : targets) {
                if (monsterBattleStats != null) {
                    switch (attributeType) {
                        case HEALTH:
                            applyHeal(healer, gameContext, monsterBattleStats);
                            break;
                        case ARMOR:
                            monsterBattleStats.getArmor().addToValue(healAmount);
                            break;
                        case ATTACK:
                        case RANGED:
                        case MAGIC:
                            monsterBattleStats.getDamageValue(attributeType).addToValue(healAmount);
                        case SPEED:
                            monsterBattleStats.getSpeed().addToValue(healAmount);
                            break;
                        case HIT_CHANCE:
                            monsterBattleStats.getBaseHitChance().addToValue(healAmount);
                            break;
                        default:
                            throw new IllegalStateException("Cant heal for: " + attributeType);
                    }

                    List<Ability> abilities  = monsterBattleStats.getAbilities().stream()
                            .filter(ability -> ability.containsClassification(AbilityClassification.ON_BATTLE_STAT_CHANGE))
                            .collect(Collectors.toList());

                    for (Ability ability : abilities) {
                        StatChangeContext statChangeContext = new StatChangeContext(healer, monsterBattleStats, BattleAttributeType.HEALTH, healAmount);
                        ((OnBattleAttributeChangeRule)ability.getEffect()).executeStatChangeRule(statChangeContext, DamageType.NONE, gameContext);
                    }
                }
            }
        }
        return true;
    }

    private void applyHeal(MonsterBattleStats healer, GameContext gameContext, MonsterBattleStats monsterBattleStats) {
        if (healAmount != null) {
            StatChangeContext statChangeContext = new StatChangeContext(healer, monsterBattleStats, BattleAttributeType.HEALTH, healAmount);
            healService.heal(statChangeContext, gameContext);
        }
        else {
            Integer defaultHealAmount = (monsterBattleStats.getHealth().getBaseValue() + monsterBattleStats.getHealth().getBuffBaseValue()) / 3;

            if (defaultHealAmount < 0) {
                defaultHealAmount = 0;
            }
            else if (defaultHealAmount < 2) {
                defaultHealAmount = 2;
            }

            StatChangeContext statChangeContext = new StatChangeContext(healer, monsterBattleStats, BattleAttributeType.HEALTH, defaultHealAmount);
            healService.heal(statChangeContext, gameContext);
        }
    }
}
