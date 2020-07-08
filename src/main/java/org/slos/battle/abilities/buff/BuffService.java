package org.slos.battle.abilities.buff;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityClassification;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.attribute.summoner.SummonerAbility;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuffService {
    private BuffFactory buffFactory = new BuffFactory();
    Map<MonsterBattleStats, List<BuffApplication>> buffsAppliedTo = new HashMap<>();
    Map<MonsterBattleStats, List<BuffApplication>> buffsAppliedBy = new HashMap<>();

    public Buff getBuffForAbility(Ability ability) {
        return buffFactory.getFromAbility(ability);
    }

    public void applyBuff(Buff buff, MonsterBattleStats applyingBuff, MonsterBattleStats applyBuffTo, GameContext gameContext) {
        BuffApplication buffApplication = new BuffApplication(buff, applyingBuff, applyBuffTo);
        applyBuff(buffApplication, gameContext);
    }

    public void removeBuff(BuffApplication buffApplication, GameContext gameContext) {
        if (buffApplication.getBuff().getBuffEffectType() == BuffEffectType.BATTLE_ATTRIBUTE) {
            removeBattleStatBuffEffect((BattleAttributeBuff) buffApplication.getBuff(), buffApplication.getAppliedTo(), gameContext);
        }

//        gameContext.log("Buffs 00> " + buffsAppliedTo.get(buffApplication.getAppliedTo()));
        buffsAppliedTo.get(buffApplication.getAppliedTo()).remove(buffApplication);
    }

    public void reapplyBuffsOn(MonsterBattleStats monsterBattleStats, GameContext gameContext) {
        List<MonsterBattleStats> allMonsters = new ArrayList<>();

        allMonsters.addAll(gameContext.getBoard().getBoardBottom().peekMonsterBattleStats());
        allMonsters.addAll(gameContext.getBoard().getBoardTop().peekMonsterBattleStats());
        allMonsters.add(gameContext.getBoard().getBoardTop().peekFromLocation(0));
        allMonsters.add(gameContext.getBoard().getBoardBottom().peekFromLocation(0));

        for (MonsterBattleStats monsterOrSummoner : allMonsters) {
            if (!monsterBattleStats.equals(monsterOrSummoner)) {
                for (Ability ability : monsterOrSummoner.getAbilities()) {
                    if (ability.getTargetRuleset().execute(monsterOrSummoner, gameContext).contains(monsterBattleStats)) {
                        if (ability.containsClassification(AbilityClassification.BUFF)) {
                            Buff buff = getBuffForAbility(ability);
                            applyBuff(buff, monsterOrSummoner, monsterBattleStats, gameContext);
                        }
                    }
                }
            }
        }

        applyBuffsFor(monsterBattleStats, gameContext);
    }

    public void applyBuffsFor(MonsterBattleStats monsterBattleStats, GameContext gameContext) {

        for (Ability ability : new ArrayList<>(monsterBattleStats.getAbilities())) {
            gameContext.log("Applying buff ability: " + ability);

            if (monsterBattleStats.getType().equals(MonsterType.SUMMONER) && ability.containsClassification(AbilityClassification.SUMMONER_APPLIED)) {
                SummonerAbility summonerAbility = (SummonerAbility) ability;

                List<MonsterBattleStats> applyBuffTo = summonerAbility.summonerAppliesAbilityTo().execute(monsterBattleStats, gameContext);
                Ability summonerAppliedAbility = summonerAbility.summonerAppliedAbility();

                applyAbility(summonerAppliedAbility, applyBuffTo, gameContext);
            }
            else if (ability.containsClassification(AbilityClassification.BUFF)) {
                Buff buff = getBuffForAbility(ability);
                List<MonsterBattleStats> applyBuffTo = ability.getTargetRuleset().execute(monsterBattleStats, gameContext);
                applyBuff(buff, monsterBattleStats, applyBuffTo, gameContext);
            }
        }
    }

    public void applyAbility(Ability ability, List<MonsterBattleStats> applyBuffTo, GameContext gameContext) {
        if (ability == null) {
            return;
        }

        for (MonsterBattleStats applyTo : applyBuffTo) {
            if (applyTo.getType() != MonsterType.SUMMONER) {
                applyTo.getAbilities().add(ability);
            }
        }
    }

    public void applyBuff(Buff buff, MonsterBattleStats applyingBuff, List<MonsterBattleStats> applyBuffTo, GameContext gameContext) {
        if (buff == null) {
            return;
        }

        for (MonsterBattleStats applyTo : applyBuffTo) {
            if (applyTo.getType() != MonsterType.SUMMONER) {
                applyBuff(buff, applyingBuff, applyTo, gameContext);
            }
        }
    }

    private void applyBattleStatBuff(BattleAttributeBuff buff, MonsterBattleStats applyTo, GameContext gameContext) {
        applyBattleStatBuff(buff, applyTo, 1, gameContext);
    }

    private void removeBattleStatBuffEffect(BattleAttributeBuff buff, MonsterBattleStats applyTo, GameContext gameContext) {
        if (!applyTo.isDead()) {
            BattleAttributeBuff battleAttributeBuff = buff;
            int buffQuantity = battleAttributeBuff.getBuffQuantity();

            switch (battleAttributeBuff.getBattleAttributeType()) {
                case HEALTH:
                    applyTo.getHealth().removeFromBuffValue(buffQuantity);
                    break;
                case ARMOR:
                    applyTo.getArmor().removeFromBuffValue(buffQuantity);
                    break;
                case SPEED:
                    applyTo.getSpeed().removeFromBuffValue(buffQuantity);
                    break;
                case HIT_CHANCE:
                    applyTo.getBaseHitChance().removeFromBuffValue(buffQuantity);
                    break;
                case ATTACK:
                    applyTo.getDamageValue(DamageType.ATTACK).removeFromBuffValue(buffQuantity);
                    break;
                case RANGED:
                    applyTo.getDamageValue(DamageType.RANGED).removeFromBuffValue(buffQuantity);
                    break;
                case MAGIC:
                    applyTo.getDamageValue(DamageType.MAGIC).removeFromBuffValue(buffQuantity);
                    break;
            }
        }
    }

    private void applyBattleStatBuff(BattleAttributeBuff buff, MonsterBattleStats applyTo, Integer posNeg, GameContext gameContext) {
        BattleAttributeBuff battleAttributeBuff = buff;
        int buffQuantity = battleAttributeBuff.getBuffQuantity() * posNeg;

        switch (battleAttributeBuff.getBattleAttributeType()) {
            case HEALTH:
                applyTo.getHealth().addToBuffValue(buffQuantity);
                break;
            case ARMOR:
                applyTo.getArmor().addToBuffValue(buffQuantity);
                break;
            case SPEED:
                applyTo.getSpeed().addToBuffValue(buffQuantity);
                break;
            case HIT_CHANCE:
                applyTo.getBaseHitChance().addToBuffValue(buffQuantity);
                break;
            case ATTACK:
                if (applyTo.isOfDamageType(DamageType.ATTACK)) {
                    applyTo.getDamageValue(DamageType.ATTACK).addToBuffValue(buffQuantity);
                }
                break;
            case RANGED:
                if (applyTo.isOfDamageType(DamageType.RANGED)) {
                    applyTo.getDamageValue(DamageType.RANGED).addToBuffValue(buffQuantity);
                }
                break;
            case MAGIC:
                if (applyTo.isOfDamageType(DamageType.MAGIC)) {
                    applyTo.getDamageValue(DamageType.MAGIC).addToBuffValue(buffQuantity);
                }
                break;
        }
    }

    public void monsterDied(MonsterBattleStats monsterBattleStats, GameContext gameContext) {
        List<BuffApplication> buffApplicationsToRemove = new ArrayList<>();
        List<BuffApplication> buffsAppliedByDeadGuy = buffsAppliedBy.get(monsterBattleStats);
        List<BuffApplication> buffApplicationsOnDeadGuy = buffsAppliedTo.get(monsterBattleStats);

        if (buffsAppliedByDeadGuy != null) {
            buffApplicationsToRemove.addAll(buffsAppliedByDeadGuy);
        }
        if (buffApplicationsOnDeadGuy != null) {
            buffApplicationsToRemove.addAll(buffApplicationsOnDeadGuy);
        }

        gameContext.log("Removing buffs: %1$s", buffApplicationsToRemove);
        if (buffApplicationsToRemove != null) {
            BuffApplication[] buffApplicationsToRemoveArray = buffApplicationsToRemove.toArray(new BuffApplication[buffApplicationsToRemove.size()]);
            for (int i = 0; i < buffApplicationsToRemove.size(); i++) {
                BuffApplication buffApplication = buffApplicationsToRemoveArray[i];
                MonsterBattleStats monsterBattleStats1 = buffApplication.getAppliedTo();
                gameContext.log("Removing buff[%1$s]: %2$s", monsterBattleStats1.getId(), buffApplication.getBuff());

                monsterBattleStats1.getAbilities().remove(buffApplication.getBuff());

                if (buffApplication.getBuff().getBuffEffectType() == BuffEffectType.BATTLE_ATTRIBUTE) {
                    removeBattleStatBuffEffect((BattleAttributeBuff)buffApplication.getBuff(), buffApplication.getAppliedTo(), gameContext);
                }

                removeBuffApplication(buffApplication);
            }
        }

        Ability[] abilities = monsterBattleStats.getAbilities().toArray(new Ability[monsterBattleStats.getAbilities().size()]);
        for (Ability ability : abilities) {
            if (ability.getAbilityType().equals(AbilityType.STUNNED)) {
                monsterBattleStats.getAbilities().remove(ability);
            }
            if (ability.getAbilityType().equals(AbilityType.POISONED)) {
                monsterBattleStats.getAbilities().remove(ability);
            }
            if (ability.getAbilityType().equals(AbilityType.AFFLICTED)) {
                monsterBattleStats.getAbilities().remove(ability);
            }
        }
    }

    private void applyBuff(BuffApplication buffApplication, GameContext gameContext) {
        gameContext.log("Applying buff: %1$s", buffApplication);
        if (buffApplication.getBuff().getBuffEffectType() == BuffEffectType.BATTLE_ATTRIBUTE) {
            applyBattleStatBuff((BattleAttributeBuff)buffApplication.getBuff(), buffApplication.getAppliedTo(), gameContext);
        }
        else {
            buffApplication.getAppliedTo().getAbilities().add((Ability) buffApplication.getBuff());//TODO: refactor away type casting, make Ability an interface and then break apart the abstract class portion
        }

        applyBuffToList(buffApplication, buffApplication.getAppliedTo(), buffsAppliedTo, gameContext);
        applyBuffToList(buffApplication, buffApplication.getAppliedBy(), buffsAppliedBy, gameContext);
    }

    private void applyBuffToList(BuffApplication buffApplication, MonsterBattleStats key, Map<MonsterBattleStats, List<BuffApplication>> list, GameContext gameContext) {
        if (!list.containsKey(key)) {
            List<BuffApplication> activeBuffs = new ArrayList<>();
            activeBuffs.add(buffApplication);
            list.put(key, activeBuffs);
        } else {
            list.get(key).add(buffApplication);
        }
    }

    public List<BuffApplication> getBuffsAppliedTo(MonsterBattleStats monsterBattleStats) {
        return buffsAppliedTo.get(monsterBattleStats);
    }

    private void removeBuffApplication(BuffApplication buffApplication) {
        if (buffsAppliedTo.get(buffApplication) != null) {
            buffsAppliedTo.get(buffApplication).remove(buffApplication);
        }

        if (buffsAppliedBy.get(buffApplication.getAppliedTo()) != null) {
            buffsAppliedBy.get(buffApplication.getAppliedTo()).remove(buffApplication);
        }
    }

    public List<BuffApplication> getBuffsAppliedBy(MonsterBattleStats monsterBattleStats) {
        return buffsAppliedBy.get(monsterBattleStats);
    }
}
