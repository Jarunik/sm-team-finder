package org.slos;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityFactory;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.buff.BuffService;
import org.slos.battle.attack.AttackService;
import org.slos.battle.board.Board;
import org.slos.battle.decision.strategy.ChoiceStrategyMode;
import org.slos.battle.monster.BattleAttribute;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.battle.phase.GameInitializationPhase;
import org.slos.battle.phase.RoundPhase;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameRulesTests implements TestHelper {
    private AbilityFactory DEFAULT_ABILITY_FACTORY = new AbilityFactory();
    private Board DEFAULT_BOARD = null;
    private GameContext DEFAULT_GAME_CONTEXT = null;
    private AttackService DEFAULT_ATTACK_SERVICE = new AttackService();

    @BeforeEach
    public void setup() {
        DEFAULT_BOARD = getDefaultBoard();
        Set<GameRuleType> gameRules = new HashSet<>();
        DEFAULT_GAME_CONTEXT = new GameContext(DEFAULT_BOARD, gameRules);
    }

    @Test
    public void itShouldDisableAllMonsterAbilitiesOnBackToBasics() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.BACK_TO_BASICS);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);

        List<Ability> abilities1 =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.WEAKEN));
        List<Ability> abilities2 =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TANK_HEAL));
        List<Ability> abilities3 =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.DOUBLE_STRIKE));
        List<Ability> abilities4 =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.AFFLICTION));
        List<Ability> abilities5 =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.STUN));
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, abilities1);
        MonsterBattleStats monsterBattleStatsTop2= new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, abilities2);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 3, 1, abilities3);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 4, 1, abilities4);
        MonsterBattleStats monsterBattleStatsTop5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 5, 1, abilities5);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop5);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();

        gameInitializationPhase.execute(gameContext);

        assertEquals(0, monsterBattleStatsTop1.getAbilities().size());
        assertEquals(0, monsterBattleStatsTop2.getAbilities().size());
        assertEquals(0, monsterBattleStatsTop3.getAbilities().size());
        assertEquals(0, monsterBattleStatsTop4.getAbilities().size());
        assertEquals(0, monsterBattleStatsTop5.getAbilities().size());
    }

    @Test
    public void itShouldDisableAllSummonerAbilitiesSilencedSummoners() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.SILENCED_SUMMONERS);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        List<Ability> summonerAbilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getSummonerAbility(AbilityType.SUMMONER_ARMOR, 1));
        MonsterBattleStats monsterBattleStatsTopSummoner = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 0, 0, summonerAbilities);
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(0, monsterBattleStatsTopSummoner);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(gameContext);

        BuffService buffService = gameContext.getBuffService();
        assertEquals(Integer.valueOf(0), monsterBattleStatsTop.getArmor().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsTop1.getArmor().getValue());
        assertEquals(null, buffService.getBuffsAppliedTo(monsterBattleStatsTop));
        assertEquals(null, buffService.getBuffsAppliedTo(monsterBattleStatsTop1));
    }

    @Test
    public void itShouldAddSneakToAllMeleeOnSuperSneak() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.SUPER_SNEAK);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2= new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 3, 1, null);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 4, 1, null);
        MonsterBattleStats monsterBattleStatsTop5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 1, 0, 5, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop5);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();

        gameInitializationPhase.execute(gameContext);

        assertFalse(monsterBattleStatsTop1.getAbilities().contains(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNEAK)));
        System.out.println(monsterBattleStatsTop2);
        assertTrue(monsterBattleStatsTop2.getAbilities().contains(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNEAK)));
        assertFalse(monsterBattleStatsTop3.getAbilities().contains(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNEAK)));
        assertTrue(monsterBattleStatsTop4.getAbilities().contains(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNEAK)));
        assertFalse(monsterBattleStatsTop5.getAbilities().contains(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNEAK)));
    }

    @Test
    public void magicShouldRemoveFromArmorFirstWhenWeakMagic() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.WEAK_MAGIC);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        gameContext.setChoiceStrategyMode(ChoiceStrategyMode.FIRST_CONFIGURED);
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 10, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 10, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(gameContext);
        gameContext.newRound();
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();

        gameInitializationPhase.execute(gameContext);
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, gameContext);

        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getArmor().getValue());
    }

    @Test
    public void itShouldIgnoreAmorWhenUnprotected() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.UNPROTECTED);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        gameContext.setChoiceStrategyMode(ChoiceStrategyMode.FIRST_CONFIGURED);
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 10, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 10, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(gameContext);
        gameContext.newRound();
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();

        gameInitializationPhase.execute(gameContext);
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, gameContext);

        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getArmor().getValue());
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldAddSnipeToAllRangedAndMagicOnTargetPractice() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.TARGET_PRACTICE);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2= new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 3, 1, null);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 4, 1, null);
        MonsterBattleStats monsterBattleStatsTop5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 1, 0, 5, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop5);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();

        gameInitializationPhase.execute(gameContext);

        assertTrue(monsterBattleStatsTop1.getAbilities().contains(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNIPE)));
        assertFalse(monsterBattleStatsTop2.getAbilities().contains(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNIPE)));
        assertTrue(monsterBattleStatsTop3.getAbilities().contains(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNIPE)));
        assertFalse(monsterBattleStatsTop4.getAbilities().contains(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNIPE)));
        assertFalse(monsterBattleStatsTop5.getAbilities().contains(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNIPE)));
    }

    @Test
    public void itShouldRemoveAllSneakWithFogOfWar() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.FOG_OF_WAR);
        List<Ability> abilities = new ArrayList<>();
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNEAK));
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNIPE));
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsTop2= new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, abilities);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 3, 1, abilities);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 4, 1, null);
        MonsterBattleStats monsterBattleStatsTop5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 1, 0, 5, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop5);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();

        gameInitializationPhase.execute(gameContext);

        System.out.println(monsterBattleStatsTop1);
        assertFalse(monsterBattleStatsTop1.containsAbility(AbilityType.SNEAK));
        assertFalse(monsterBattleStatsTop2.containsAbility(AbilityType.SNEAK));
        assertFalse(monsterBattleStatsTop3.containsAbility(AbilityType.SNEAK));
        assertFalse(monsterBattleStatsTop4.containsAbility(AbilityType.SNEAK));
        assertFalse(monsterBattleStatsTop5.containsAbility(AbilityType.SNEAK));
        assertFalse(monsterBattleStatsTop1.containsAbility(AbilityType.SNIPE));
        assertFalse(monsterBattleStatsTop2.containsAbility(AbilityType.SNIPE));
        assertFalse(monsterBattleStatsTop3.containsAbility(AbilityType.SNIPE));
        assertFalse(monsterBattleStatsTop4.containsAbility(AbilityType.SNIPE));
        assertFalse(monsterBattleStatsTop5.containsAbility(AbilityType.SNIPE));
    }

    @Test
    public void itShouldAddArmorWhenArmoredUp() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.ARMORED_UP);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2= new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 1, 2, 1, null);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 2, 3, 1, null);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 3, 4, 1, null);
        MonsterBattleStats monsterBattleStatsTop5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 1, 4, 5, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop5);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();

        gameInitializationPhase.execute(gameContext);

        System.out.println(monsterBattleStatsTop1);
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop1.getArmor().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsTop2.getArmor().getValue());
        assertEquals(Integer.valueOf(4), monsterBattleStatsTop3.getArmor().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsTop4.getArmor().getValue());
        assertEquals(Integer.valueOf(6), monsterBattleStatsTop5.getArmor().getValue());
    }

    @Test
    public void itShouldDisableHealTankWithHealedOut() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.HEALED_OUT);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TANK_HEAL));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        monsterBattleStatsBottom.getHealth().removeFromValue(4);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(gameContext);
        RoundPhase roundPhase = new RoundPhase();

        System.out.println(monsterBattleStatsBottom2);
        roundPhase.executeTurn(monsterBattleStatsBottom2, DamageType.ATTACK, gameContext);

        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());
    }

    public void itShouldThworExceptionOnMeleeWhenKeepYourDistance() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.KEEP_YOUR_DISTANCE);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();

        assertThrows(IllegalStateException.class, () -> {
            gameInitializationPhase.execute(gameContext);
        });
    }

    @Test
    public void dualDamageTypeRangedAttacksProperlyWithDefaultRules() {
        Map<DamageType, BattleAttribute> multiDamage = new HashMap<>();
        BattleAttribute battleAttributeAttack = new BattleAttribute(2, true);
        BattleAttribute battleAttributeRanged = new BattleAttribute(3, true);
        multiDamage.put(DamageType.ATTACK, battleAttributeAttack);
        multiDamage.put(DamageType.RANGED, battleAttributeRanged);
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.STANDARD);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        MonsterBattleStats monsterBattleStatsMultiDamage = new MonsterBattleStats(1, 1, MonsterType.MONSTER, multiDamage, 0, 100, 5, null);
        MonsterBattleStats tank = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 4, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 9, 0, 100, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, tank);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsMultiDamage);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(gameContext);
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeRound(gameContext);

        assertEquals(Integer.valueOf(96), monsterBattleStatsBottom.getHealth().getValue());
    }


    @Test
    public void dualDamageTypeWillAttackMagicAndRangedInFront() {
        Map<DamageType, BattleAttribute> multiDamage = new HashMap<>();
        BattleAttribute battleAttributeAttack = new BattleAttribute(2, true);
        BattleAttribute battleAttributeMagic = new BattleAttribute(3, true);
        multiDamage.put(DamageType.ATTACK, battleAttributeAttack);
        multiDamage.put(DamageType.MAGIC, battleAttributeMagic);
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.STANDARD);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        MonsterBattleStats monsterBattleStatsMultiDamage = new MonsterBattleStats(1, 1, MonsterType.MONSTER, multiDamage, 0, 100, 5, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 9, 0, 100, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsMultiDamage);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(gameContext);
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeRound(gameContext);

        assertEquals(Integer.valueOf(95), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void dualDamageTypeWontAttackRangedInFront() {
        Map<DamageType, BattleAttribute> multiDamage = new HashMap<>();
        BattleAttribute battleAttributeAttack = new BattleAttribute(2, true);
        BattleAttribute battleAttributeRanged = new BattleAttribute(3, true);
        multiDamage.put(DamageType.ATTACK, battleAttributeAttack);
        multiDamage.put(DamageType.RANGED, battleAttributeRanged);
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.STANDARD);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        MonsterBattleStats monsterBattleStatsMultiDamage = new MonsterBattleStats(1, 1, MonsterType.MONSTER, multiDamage, 0, 100, 5, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 9, 0, 100, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsMultiDamage);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(gameContext);
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeRound(gameContext);

        assertEquals(Integer.valueOf(98), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void dualDamageTypeMeleeAttacksProperlyWithDefaultRules() {
        Map<DamageType, BattleAttribute> multiDamage = new HashMap<>();
        BattleAttribute battleAttributeAttack = new BattleAttribute(2, true);
        BattleAttribute battleAttributeRanged = new BattleAttribute(3, true);
        multiDamage.put(DamageType.ATTACK, battleAttributeAttack);
        multiDamage.put(DamageType.RANGED, battleAttributeRanged);
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.STANDARD);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        MonsterBattleStats monsterBattleStatsMultiDamage = new MonsterBattleStats(1, 1, MonsterType.MONSTER, multiDamage, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 9, 0, 100, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsMultiDamage);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(gameContext);
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeRound(gameContext);

        assertEquals(Integer.valueOf(98), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void meleeCanAttackAnyPositionWithMeleeMayhem() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.MELEE_MAYHEM);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 9, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(gameContext);

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, gameContext);
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsBottom2, DamageType.ATTACK, gameContext);

        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(90), monsterBattleStatsTop.getHealth().getValue());
    }

    @Test
    public void itShouldDamageNonFlyingOnEarthquake() {
        Set<GameRuleType> rules = new HashSet<>();
        rules.add(GameRuleType.EARTHQUAKE);
        List<Ability> flying = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.FLYING));
        GameContext gameContext = new GameContext(DEFAULT_BOARD, rules);
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 100, 1, flying);
        MonsterBattleStats monsterBattleStatsTop2= new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, flying);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 100, 100, 1, null);
        MonsterBattleStats monsterBattleStatsTop5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 1, 0, 2, 1, null);

        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 0, 100, 1, flying);
        MonsterBattleStats monsterBattleStatsBottom2= new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 0, 100, 1, flying);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 0, 100, 1, flying);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 100, 100, 1, flying);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 0, 100, 1, flying);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop5);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(gameContext);
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeRound(gameContext);

        assertEquals(Integer.valueOf(100), monsterBattleStatsTop1.getHealth().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsTop2.getHealth().getValue());
        assertEquals(Integer.valueOf(98), monsterBattleStatsTop3.getHealth().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsTop4.getHealth().getValue());
        assertEquals(Integer.valueOf(98), monsterBattleStatsTop4.getArmor().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsTop5.getArmor().getValue());
        assertNull(gameContext.getBoard().getBoardTop().peekFromLocation(5));

        roundPhase.executeRound(gameContext);

        assertEquals(Integer.valueOf(100), monsterBattleStatsTop1.getHealth().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsTop2.getHealth().getValue());
        assertEquals(Integer.valueOf(96), monsterBattleStatsTop3.getHealth().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsTop4.getHealth().getValue());
        assertEquals(Integer.valueOf(96), monsterBattleStatsTop4.getArmor().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsTop5.getArmor().getValue());
    }
}