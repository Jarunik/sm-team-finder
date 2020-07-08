package org.slos.battle.phase;

import org.slos.battle.GameContext;
import org.slos.battle.GameState;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.buff.BuffService;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;

import java.util.ArrayList;
import java.util.List;

public class GameInitializationPhase implements GamePhase {
    private static final List<Ability> EMPTY = new ArrayList<>();

    @Override
    public void execute(GameContext gameContext) {
        gameContext.setGameState(GameState.INITALIZING_SETUP);

        checkAndApplyKeepYourDistance(gameContext);

        if (!gameContext.hasRule(GameRuleType.SILENCED_SUMMONERS)) {
            applySummonerAbilities(gameContext);
        }
        else {
            removeSummonerAbilities(gameContext);
        }

        if (!gameContext.hasRule(GameRuleType.BACK_TO_BASICS)) {
            applyGameRules(gameContext);
        }
        else {
            removeMonsterAbilities(gameContext);
        }
    }

    private void applyGameRules(GameContext gameContext) {
        List<MonsterBattleStats> monsters = gameContext.getAllMonsters();
        checkAndApplyKeepYourDistance(gameContext);
        checkAndApplyUpClosePersonal(gameContext);
        checkAndApplyBrokenArrows(gameContext);
        checkAndApplyLittleLeague(gameContext);

        applyAbilities(monsters, gameContext);
        checkAndApplySuperSneak(gameContext);
        checkAndApplySnipe(gameContext);
        checkAndApplyFogOfWar(gameContext);
        checkAndApplyArmoredUp(gameContext);
        checkAndApplyHeavyHitters(gameContext);
        checkAndApplyEqualizer(gameContext);
    }

    private void checkAndApplyArmoredUp(GameContext gameContext) {
        if (gameContext.getGameRules().contains(GameRuleType.ARMORED_UP)) {
            for(MonsterBattleStats monsterBattleStats : gameContext.getAllMonsters()) {
                monsterBattleStats.getArmor().addToBaseValue(2);
                monsterBattleStats.getArmor().addToValue(2);
            }
        }
    }

    private void checkAndApplyLittleLeague(GameContext gameContext) {
        if (gameContext.hasRule(GameRuleType.LITTLE_LEAGUE)) {
            List<MonsterBattleStats> monsterBattleStatsList = gameContext.getAllMonsters();
            monsterBattleStatsList.add(gameContext.getBoard().getBoardTop().peekFromLocation(0));
            monsterBattleStatsList.add(gameContext.getBoard().getBoardBottom().peekFromLocation(0));

            for (MonsterBattleStats monsterBattleStats : monsterBattleStatsList) {
                if ((monsterBattleStats.getType() == MonsterType.MONSTER) && (monsterBattleStats.getMana() > 4)) {
                    throw new IllegalStateException("Only monsters and summoners that cost 4 mana or less may be used in battle: " + monsterBattleStats);
                }
            }
        }
    }

    private void checkAndApplyBrokenArrows(GameContext gameContext) {
        if (gameContext.hasRule(GameRuleType.BROKEN_ARROWS)) {
            List<MonsterBattleStats> monsterBattleStatsList = gameContext.getAllMonsters();

            for (MonsterBattleStats monsterBattleStats : monsterBattleStatsList) {
                if ((monsterBattleStats.getType() == MonsterType.MONSTER) && (monsterBattleStats.isOfDamageType(DamageType.RANGED)) && (monsterBattleStats.getDamageValue(DamageType.RANGED).getValue() > 0)) {
                    System.err.println("Found ranged monster during Broken Arrows.");
//                    throw new IllegalStateException("Ranged monsters are not allowed during game rule Broken Arrows: " + monsterBattleStats);
                }
            }
        }
    }

    private void checkAndApplyUpClosePersonal(GameContext gameContext) {
        if (gameContext.hasRule(GameRuleType.UP_CLOSE_AND_PERSONAL)) {
            List<MonsterBattleStats> monsterBattleStatsList = gameContext.getAllMonsters();

            for (MonsterBattleStats monsterBattleStats : monsterBattleStatsList) {
                if ((monsterBattleStats.getType() == MonsterType.MONSTER) && (!monsterBattleStats.isOfDamageType(DamageType.ATTACK)) && (monsterBattleStats.getDamageValue(DamageType.ATTACK).getValue() > 0)) {
                    throw new IllegalStateException("Only attack monsters are allowed during game rule Keep Your Distance: " + monsterBattleStats);//TODO resolve if this is needed for bot
                }
            }
        }
    }

    private void checkAndApplyKeepYourDistance(GameContext gameContext) {
        if (gameContext.hasRule(GameRuleType.KEEP_YOUR_DISTANCE)) {
            List<MonsterBattleStats> monsterBattleStatsList = gameContext.getAllMonsters();

            for (MonsterBattleStats monsterBattleStats : monsterBattleStatsList) {
                if ((monsterBattleStats.getType() == MonsterType.MONSTER) && (monsterBattleStats.isOfDamageType(DamageType.ATTACK))) {
//                    throw new IllegalStateException("Attack monsters are not allowed during game rule Keep Your Distance: " + monsterBattleStats);//TODO resolve if this is neede for bot
                }
            }
        }
    }

    private void checkAndApplyFogOfWar(GameContext gameContext) {
        if (gameContext.getGameRules().contains(GameRuleType.FOG_OF_WAR)) {
            for(MonsterBattleStats monsterBattleStats : gameContext.getAllMonsters()) {
                Ability[] abilities = monsterBattleStats.getAbilities().toArray(new Ability[0]);
                for (Ability ability : abilities) {
                    if ((ability.getAbilityType() == AbilityType.SNIPE) || (ability.getAbilityType() == AbilityType.SNEAK)) {
                        monsterBattleStats.getAbilities().remove(ability);
                    }
                }
            }
        }
    }

    private void checkAndApplySnipe(GameContext gameContext) {
        if (gameContext.getGameRules().contains(GameRuleType.TARGET_PRACTICE)) {
            for(MonsterBattleStats monsterBattleStats : gameContext.getAllMonsters()) {
                if ((monsterBattleStats.isOfDamageType(DamageType.RANGED)) || ((monsterBattleStats.isOfDamageType(DamageType.MAGIC)))) {
                    if (!monsterBattleStats.containsAbility(AbilityType.SNIPE)) {
                        monsterBattleStats.addAbility(gameContext.getAbilityFactory().getAbility(AbilityType.SNIPE));
                    }
                }
            }
        }
    }

    private void checkAndApplyEqualizer(GameContext gameContext) {
        if (gameContext.getGameRules().contains(GameRuleType.EQUALIZER)) {
            Integer highestHealth = 0;

            for(MonsterBattleStats monsterBattleStats : gameContext.getAllMonsters()) {
                if (monsterBattleStats.getHealth().getValue() > highestHealth) {
                    highestHealth = monsterBattleStats.getHealth().getValue();
                }
            }

            for(MonsterBattleStats monsterBattleStats : gameContext.getAllMonsters()) {
                int healthToAdd = highestHealth - monsterBattleStats.getHealth().getValue();
                monsterBattleStats.getHealth().addToBaseValue(healthToAdd);
            }
        }
    }

    private void checkAndApplyHeavyHitters(GameContext gameContext) {
        if (gameContext.getGameRules().contains(GameRuleType.HEAVY_HITTERS)) {
            for(MonsterBattleStats monsterBattleStats : gameContext.getAllMonsters()) {
                if (!monsterBattleStats.containsAbility(AbilityType.KNOCK_OUT)) {
                    monsterBattleStats.addAbility(gameContext.getAbilityFactory().getAbility(AbilityType.KNOCK_OUT));
                }
            }
        }
    }

    private void checkAndApplySuperSneak(GameContext gameContext) {
        if (gameContext.getGameRules().contains(GameRuleType.SUPER_SNEAK)) {
            for(MonsterBattleStats monsterBattleStats : gameContext.getAllMonsters()) {
                if (monsterBattleStats.isOfDamageType(DamageType.ATTACK)) {
                    if (!monsterBattleStats.containsAbility(AbilityType.SNEAK)) {
                        monsterBattleStats.addAbility(gameContext.getAbilityFactory().getAbility(AbilityType.SNEAK));
                    }
                }
            }
        }
    }

    private void removeSummonerAbilities(GameContext gameContext) {
        gameContext.getBoard().getBoardTop().peekFromLocation(0).setAbilities(EMPTY);
        gameContext.getBoard().getBoardBottom().peekFromLocation(0).setAbilities(EMPTY);
    }

    private void removeMonsterAbilities(GameContext gameContext) {
        for (MonsterBattleStats monsterBattleStats : gameContext.getAllMonsters()) {
            monsterBattleStats.setAbilities(EMPTY);
        }
    }

    private void applySummonerAbilities(GameContext gameContext) {
        List<MonsterBattleStats> summoners = new ArrayList<>();
        summoners.add(gameContext.getBoard().getBoardTop().peekFromLocation(0));
        summoners.add(gameContext.getBoard().getBoardBottom().peekFromLocation(0));

        applyAbilities(summoners, gameContext);
    }

    private void applyAbilities(List<MonsterBattleStats> monsterBattleStatsList, GameContext gameContext) {
        BuffService buffService = gameContext.getBuffService();
        for (MonsterBattleStats monsterBattleStats : monsterBattleStatsList) {
            buffService.applyBuffsFor(monsterBattleStats, gameContext);
        }
    }
}
