package org.slos;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.attack.AfflictedAbility;
import org.slos.battle.abilities.attack.IsPoisonedAbility;
import org.slos.battle.abilities.attack.IsStunnedAbility;
import org.slos.battle.abilities.attack.PierceAbility;
import org.slos.battle.abilities.attack.SnaredAbility;
import org.slos.battle.abilities.rule.turn.OnTurnStartRule;
import org.slos.battle.attack.AttackService;
import org.slos.battle.board.Board;
import org.slos.battle.decision.strategy.ChoiceStrategyMode;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.battle.phase.GameInitializationPhase;
import org.slos.battle.phase.RoundPhase;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttackAndAbilityServiceTests implements TestHelper {
    private Board DEFAULT_BOARD = null;
    private GameContext DEFAULT_GAME_CONTEXT = null;
    private AttackService DEFAULT_ATTACK_SERVICE = new AttackService();

    @BeforeEach
    public void setup() {
        DEFAULT_BOARD = getDefaultBoard();
        Set<GameRuleType> gameRules = new HashSet<>();
        DEFAULT_GAME_CONTEXT = new GameContext(DEFAULT_BOARD, gameRules);
        DEFAULT_GAME_CONTEXT.setChoiceStrategyMode(ChoiceStrategyMode.FIRST_CONFIGURED);
        DEFAULT_GAME_CONTEXT.resetAttackQueue();
    }

    @Test
    public void itShouldMeleeAttackWhenInFirstPosition() {
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldNotMeleeAttackWhenInSecondPosition() {
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldMeleeAttackWhenInSecondPositionWithReach() {
        List<Ability> reachAbility =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.REACH));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, reachAbility);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldNotMeleeAttackWhenInThirdPositionWithReach() {
        List<Ability> reachAbility =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.REACH));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, reachAbility);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldNotAttackInFirstPositionWithRanged() {
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.RANGED, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldAttackInSecondPositionWithRanged() {
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.RANGED, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldAttackInFirstPositionWithMagic() {
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldAttackInSecondPositionWithMagic() {
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldTauntSnipe() {
        List<Ability> snipeAbility =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNIPE));
        List<Ability> tauntAbility =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TAUNT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, snipeAbility);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, tauntAbility);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(6, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.RANGED, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom1.getHealth().getValue());
    }

    @Disabled //TODO: verify once card is out if this test is needed
    @Test
    public void itShouldNotTauntIfAttackerCantTargetTaunter() {
        List<Ability> snipeAbility =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNIPE));
        List<Ability> tauntAbility =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TAUNT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, snipeAbility);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, tauntAbility);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(6, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        DEFAULT_GAME_CONTEXT.printHistory();
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getHealth().getValue());

        assertTrue(false);
    }

    @Test
    public void itShouldSnipe() {
        List<Ability> snipeAbility =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNIPE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, snipeAbility);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(4).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.RANGED, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getHealth().getValue());
    }

    @Test
    public void itShouldStillAttackIfNoSnipeCondition() {
        List<Ability> snipeAbility =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNIPE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, snipeAbility);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.RANGED, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldApplyTheWeakenBuff() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.WEAKEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 3, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 4, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 5, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);

        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(4), monsterBattleStatsBottom4.getHealth().getValue());
    }

    @Test
    public void reflectShouldBeHalfRoundUp() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.MAGIC_REFLECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 100, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.MAGIC, 5, 0, 100, 1, null);

        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom1);

        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsBottom1, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        System.out.println(DEFAULT_BOARD.toSimpleString());
        assertEquals(Integer.valueOf(97), monsterBattleStatsBottom1.getHealth().getValue());
    }

    @Test
    public void itShouldDamageAllEnemyByTwoOnDeathWithRedemption() {
        List<Ability> redemption =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.REDEMPTION));
        List<Ability> shield =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SHIELD));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 0, 0, 1, 1, redemption);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 3, 5, 1, null);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(6, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 5, 1, shield);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_GAME_CONTEXT);
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeTurn(monsterBattleStatsBottom1, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
//        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsBottom1, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        DEFAULT_GAME_CONTEXT.printHistory();
        assertTrue(monsterBattleStatsBottom1.isDead());
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom4.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom4.getArmor().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom5.getHealth().getValue());
    }

    @Test
    public void itShouldApplyTheRustBuff() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RUST));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 1, 3, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 2, 4, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 3, 5, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);

        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom3.getArmor().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom4.getArmor().getValue());
    }

    @Test
    public void itShouldRestoreToStartingValueWhenRustDies() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RUST));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 1, 3, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.MAGIC, 100, 2, 4, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(6, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 3, 5, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_GAME_CONTEXT);
        RoundPhase roundPhase = new RoundPhase();

//        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(monsterBattleStatsBottom3, DamageType.MAGIC, monsterBattleStatsTop2, DEFAULT_GAME_CONTEXT);
        roundPhase.executeTurn(monsterBattleStatsBottom3, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertTrue(monsterBattleStatsTop2.isDead());

        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getArmor().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsBottom4.getArmor().getValue());
    }

    @Test
    public void itShouldNotApplyTheWeakenBuffToFriends() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.WEAKEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 3, 1, null);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 4, 1, null);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 5, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);

        assertNull(DEFAULT_GAME_CONTEXT.getBuffService().getBuffsAppliedTo(monsterBattleStatsTop));
        assertNull(DEFAULT_GAME_CONTEXT.getBuffService().getBuffsAppliedTo(monsterBattleStatsTop1));
        assertNull(DEFAULT_GAME_CONTEXT.getBuffService().getBuffsAppliedTo(monsterBattleStatsTop2));
        assertNull(DEFAULT_GAME_CONTEXT.getBuffService().getBuffsAppliedTo(monsterBattleStatsTop3));
        assertNull(DEFAULT_GAME_CONTEXT.getBuffService().getBuffsAppliedTo(monsterBattleStatsTop4));
        assertEquals(Integer.valueOf(1), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop1.getHealth().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsTop2.getHealth().getValue());
        assertEquals(Integer.valueOf(4), monsterBattleStatsTop3.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsTop4.getHealth().getValue());
    }

    @Test
    public void itShouldNotApplyWeakenIfHealthIsOne() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.WEAKEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);

        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(2).getHealth().getValue());
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(3).getHealth().getValue());
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(4).getHealth().getValue());
    }

    @Test
    public void itShouldRemoveWeakenIfApplierDies() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.WEAKEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 10, 10, null);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(3, 100, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 1, 10, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(6, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(10), monsterBattleStatsTop.getHealth().getValue());
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(9), monsterBattleStatsTop.getHealth().getValue());
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.log("Done.");
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(10), monsterBattleStatsTop.getHealth().getValue());
    }



    @Test
    public void itShouldRemoveWeakenIfAlreadyTookDamageWhenApplierDies() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.WEAKEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 10, 10, null);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(3, 100, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 11, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(6, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(10), monsterBattleStatsTop.getHealth().getValue());
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(9), monsterBattleStatsTop.getHealth().getValue());
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeRound(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.log("Done.");
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(9), monsterBattleStatsTop.getHealth().getValue());
    }

    @Test
    public void itShouldApplyStrengthen() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.STRENGTHEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 100, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(2).getHealth().getValue());
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(2).getHealth().getValue());
    }

    @Test
    public void itShouldApplyBlind() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.BLIND));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom1.getBaseHitChance().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom2.getBaseHitChance().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom3.getBaseHitChance().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom4.getBaseHitChance().getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(85), monsterBattleStatsBottom1.getBaseHitChance().getValue());
        assertEquals(Integer.valueOf(85), monsterBattleStatsBottom2.getBaseHitChance().getValue());
        assertEquals(Integer.valueOf(85), monsterBattleStatsBottom3.getBaseHitChance().getValue());
        assertEquals(Integer.valueOf(85), monsterBattleStatsBottom4.getBaseHitChance().getValue());
    }

    @Test
    public void itShouldApplyInspire() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.INSPIRE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom1.getDamageValue(DamageType.ATTACK).getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getDamageValue(DamageType.RANGED).getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom4.getDamageValue(DamageType.ATTACK).getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom1.getDamageValue(DamageType.ATTACK).getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getDamageValue(DamageType.RANGED).getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom4.getDamageValue(DamageType.ATTACK).getValue());
    }

    @Test
    public void itShouldApplyDemoralize() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.DEMORALIZE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getDamageValue(DamageType.ATTACK).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getDamageValue(DamageType.RANGED).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom4.getDamageValue(DamageType.ATTACK).getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom1.getDamageValue(DamageType.ATTACK).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getDamageValue(DamageType.RANGED).getValue());
        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom4.getDamageValue(DamageType.ATTACK).getValue());
    }

    @Test
    public void demoralizeShouldNotLowerAttackBelowOne() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.DEMORALIZE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom1.getDamageValue(DamageType.ATTACK).getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(monsterBattleStatsBottom1, DamageType.ATTACK, monsterBattleStatsTop, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(9), monsterBattleStatsTop.getHealth().getValue());
    }

    @Test
    public void itShouldApplyHeadwinds() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.HEADWINDS));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 10, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getDamageValue(DamageType.ATTACK).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getDamageValue(DamageType.RANGED).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom4.getDamageValue(DamageType.RANGED).getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getDamageValue(DamageType.ATTACK).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());
        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom3.getDamageValue(DamageType.RANGED).getValue());
        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom4.getDamageValue(DamageType.RANGED).getValue());
    }

    @Test
    public void itShouldApplyProtect() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.PROTECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 10, 10, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom3.getArmor().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom4.getArmor().getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getArmor().getValue());
        assertEquals(Integer.valueOf(12), monsterBattleStatsBottom4.getArmor().getValue());
    }

    @Test
    public void itShouldApplySilence() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SILENCE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1,null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.RANGED, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(6, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 10, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getDamageValue(DamageType.ATTACK).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getDamageValue(DamageType.RANGED).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom4.getDamageValue(DamageType.MAGIC).getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.log("Done.");
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getDamageValue(DamageType.ATTACK).getValue());
        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getDamageValue(DamageType.RANGED).getValue());
        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom4.getDamageValue(DamageType.MAGIC).getValue());
    }

    @Test
    public void silenceShouldNotReduceBelowOne() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SILENCE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());
    }

    @Test
    public void itShouldApplySwiftness() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SWIFTNESS));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 10, 1, 2, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom1.getSpeed().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getSpeed().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getSpeed().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom4.getSpeed().getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom1.getSpeed().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getSpeed().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getSpeed().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsBottom4.getSpeed().getValue());
    }

    @Test
    public void itShouldApplySlow() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SLOW));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 3, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 0, 1, 3, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 10, 0, 1, 3, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 10, 1, 4, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(3), monsterBattleStatsBottom1.getSpeed().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsBottom2.getSpeed().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsBottom3.getSpeed().getValue());
        assertEquals(Integer.valueOf(4), monsterBattleStatsBottom4.getSpeed().getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom1.getSpeed().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getSpeed().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getSpeed().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsBottom4.getSpeed().getValue());
    }

    @Test
    public void itShouldNotReduceSpeedLessThanOneWithSlow() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SLOW));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom1.getSpeed().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getSpeed().getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom1.getSpeed().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getSpeed().getValue());
    }

    @Test
    public void itShouldRemoveStrengthenOnDeath() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.STRENGTHEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 100, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(2).getHealth().getValue());
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        assertSame(monsterBattleStatsBottom2, DEFAULT_BOARD.getBoardBottom().peekFromLocation(2));
        assertEquals(Integer.valueOf(2), DEFAULT_BOARD.getBoardBottom().peekFromLocation(2).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertSame(monsterBattleStatsBottom2, DEFAULT_BOARD.getBoardBottom().peekFromLocation(1));
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldNotRemoveStrengthenHealthIfAlreadyDamaged() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.STRENGTHEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 100, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getHealth().getValue());
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(11), monsterBattleStatsBottom2.getHealth().getValue());

        MonsterBattleStats damagedMonster = DEFAULT_BOARD.getBoardBottom().peekFromLocation(2);
        assertSame(monsterBattleStatsBottom2, damagedMonster);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom2, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(10), damagedMonster.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom1, DEFAULT_GAME_CONTEXT);

        assertSame(monsterBattleStatsBottom2, damagedMonster);
        assertEquals(Integer.valueOf(10), damagedMonster.getHealth().getValue());
    }

    @Test
    public void itShouldReduceMagacDimageWithVoidAbility() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.VOID));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom.getHealth().getValue());
    }


    @Test
    public void itShouldReduceMagacDimageWithVoidAbility2() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.VOID));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 3, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom.getHealth().getValue());
    }


    @Test
    public void itShouldReduceMagacDimageWithVoidAbility3() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.VOID));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldNotReduceAttackDimageWithVoidAbility() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.VOID));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(6), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldApplyThornsOnHit() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.THORNS));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 11, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(11), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(98), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldNotApplyThornsOnMagicHit() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.THORNS));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(10), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(100), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(6), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void thornsShouldRemoveFromArmorFirst() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.THORNS));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 10, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(10), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(100), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(8), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getArmor().getValue());
        assertEquals(Integer.valueOf(6), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void divineShieldShouldIgnoreFirstAttackDamage() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.DIVINE_SHIELD));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 10, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(10), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldNotBlockSecondHitWithDivineShield() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.DIVINE_SHIELD));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 10, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(6), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldLeechHealthWithLifeLeech() { //TODO Verify health goes up
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.LIFE_LEECH));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 10, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 2, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(6), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsBottom, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(8), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldApplyMagicReflectOnHit() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.MAGIC_REFLECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(10), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(98), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(6), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldNotApplyMagicReflectOnRangedHit() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.MAGIC_REFLECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 4, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(10), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.RANGED, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(100), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(6), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void magicReflectShouldDamageHealth() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.MAGIC_REFLECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 10, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(10), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(98), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(10), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getArmor().getValue());
        assertEquals(Integer.valueOf(6), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
    }

    @Test
    public void itShouldApplyRangedReflectOnHit() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RETURN_FIRE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 6, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.RANGED, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(97), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(4), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldRemoveArmorFromShatterOnHit() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SHATTER));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 10, 100, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 10, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getArmor().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(100), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsTop.getArmor().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom.getArmor().getValue());
    }

    @Test
    public void itShouldRemoveExtraDamageFromHealthFromPierceOnHit() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.PIERCING));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 15, 10, 100, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 10, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(100), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldReduceMeleeDimageWithShieldAbility() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SHIELD));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldReduceRangedDimageWithShieldAbility() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SHIELD));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.RANGED, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldReduceRangedDimageWithShieldAbility2() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SHIELD));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 3, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.RANGED, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldNotReduceMagicDimageWithShieldAbility() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SHIELD));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldApplyBlastToAdjacentTargets() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.BLAST));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.MAGIC, monsterBattleStatsBottom2, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom4.getHealth().getValue());
    }

    @Test
    public void itShouldNotThornsOnBlastDamage() {
        List<Ability> blast =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.BLAST));
        List<Ability> thorns =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.THORNS));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 10, 1, blast);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, thorns);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, thorns);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom2, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsTop.getHealth().getValue());
    }

    @Test
    public void itShouldApplyBlastWhenInFirstLocation() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.BLAST));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.MAGIC, monsterBattleStatsBottom1, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom2.getHealth().getValue());
    }

    @Test
    public void itShouldApplyBlastWhenInLastAliveLocation() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.BLAST));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.MAGIC, monsterBattleStatsBottom4, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom4.getHealth().getValue());
    }

    @Test
    public void itShouldApplyBlastWhenInLastPositionLocation() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.BLAST));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom6 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, monsterBattleStatsBottom6);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom4.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom4.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.MAGIC, monsterBattleStatsBottom6, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom5.getHealth().getValue());
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom6.getHealth().getValue());
    }



    //TODO: Figure out how to test this
    @Test
    public void itShouldDecreaseAccuracyAgainstMeleeAndRangedOnDodge() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.DODGE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop2, DamageType.RANGED, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop3, DamageType.MAGIC, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
    }

    //TODO: Figure out how to test this
    @Test
    public void itShouldDecreaseAccuracyAgainstMeleeAgainstFlyingOnDodge() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.FLYING));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 4, 0, 1, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop2, DamageType.ATTACK, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
    }



    //TODO: Figure out how to test this
    @Test
    public void itShouldNullifyFlyingWhenSnared() {
        List<Ability> abilities =  new ArrayList<>();
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.FLYING));
        abilities.add(new SnaredAbility());
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 4, 0, 1, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
    }

    //TODO: Figure out how to test this
    @Test
    public void itShouldAddBothFlyingAndDodgeToAccuracy() {
        List<Ability> abilities =  new ArrayList<>();
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.FLYING));
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.DODGE));

        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 4, 0, 1, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop2, DamageType.ATTACK, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
    }

    //TODO: Figure out how to test this
    @Test
    public void itShouldAddBothFlyingAndDodgeAndSpeedDifferenceToAccuracy() {
        List<Ability> abilities =  new ArrayList<>();
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.FLYING));
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.DODGE));

        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 1, 8, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 4, 0, 1, 6, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop2, DamageType.ATTACK, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
    }



    //TODO: Figure out how to test this
    @Test
    public void itShouldReverseSpeedAccuracyModifierOnReverseSpeed() {
        Set<GameRuleType> gameRules = new HashSet<>();
        gameRules.add(GameRuleType.REVERSE_SPEED);
        GameContext gameContext = new GameContext(DEFAULT_BOARD, gameRules);

        List<Ability> abilities =  new ArrayList<>();
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 8, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 10, 6, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(gameContext);
        gameContext.newRound();

        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom, gameContext);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop2, DamageType.ATTACK, monsterBattleStatsBottom, gameContext);
    }

    @Test
    public void itShouldNotUseOpportunitySelectionIfInFirstPosition() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.OPPORTUNITY));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 5, 1, null);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 5, 1, null);
        MonsterBattleStats monsterBattleStatsBottom6 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, monsterBattleStatsBottom6);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom4.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom5.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom6.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom4.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom5.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom6.getHealth().getValue());
    }

    @Test
    public void itShouldAttackAtAnyPositionWithOpportunity() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.OPPORTUNITY));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 5, 1, null);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 5, 1, null);
        MonsterBattleStats monsterBattleStatsBottom6 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, monsterBattleStatsBottom6);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom4.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom5.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom6.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop2, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom4.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom5.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom6.getHealth().getValue());
    }

    @Test
    public void itShouldSneak() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNEAK));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom6 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, monsterBattleStatsBottom6);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom6.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom4.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom5.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom6.getHealth().getValue());
    }



    @Test
    public void itShouldAttackFirstIfInFrontEvenWithSneak() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNEAK));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom6 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 2, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, monsterBattleStatsBottom6);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom6.getHealth().getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom4.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom5.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom6.getHealth().getValue());
    }

    @Test
    public void itShouldEnrageWhenDamaged() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.ENRAGE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 5, 0, 10, 5, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom.getSpeed().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom.getDamageValue(DamageType.ATTACK).getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        DEFAULT_GAME_CONTEXT.printHistory();
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom.getDamageValue(DamageType.ATTACK).getValue());
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom.getSpeed().getValue());
    }

    @Test
    public void itShouldLastStandWhenLastAlive() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.LAST_STAND));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 5, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(118, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 12, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(12), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getSpeed().getValue());
        assertEquals(Integer.valueOf(4), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        DEFAULT_GAME_CONTEXT.printHistory();
        assertEquals(Integer.valueOf(18), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getSpeed().getValue());
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());
    }


    @Test
    public void itShouldHaveCorrectDamageIncreaseWhenBaseIsThree() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.LAST_STAND));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 5, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(118, 1, MonsterType.MONSTER, DamageType.MAGIC, 3, 0, 12, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(12), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getSpeed().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        DEFAULT_GAME_CONTEXT.printHistory();
        assertEquals(Integer.valueOf(18), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getSpeed().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom2.getDamageValue(DamageType.MAGIC).getValue());
    }

    @Test
    public void itShouldRemoveEnragedWhenHealed() {
        List<Ability> enrage =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.ENRAGE));
        List<Ability> tankHeal =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TANK_HEAL));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.MAGIC, 2, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, enrage);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.RANGED, 10, 0, 10, 1, tankHeal);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom.getSpeed().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getDamageValue(DamageType.ATTACK).getValue());
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom.getSpeed().getValue());
        assertEquals(Integer.valueOf(15), monsterBattleStatsBottom.getDamageValue(DamageType.ATTACK).getValue());
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeTurn(monsterBattleStatsBottom2, DamageType.RANGED, DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom.getSpeed().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getDamageValue(DamageType.ATTACK).getValue());
    }

    @Test
    public void itShouldNotOverHeal() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.HEAL));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 11, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(11), monsterBattleStatsBottom.getHealth().getValue());
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());

        ((OnTurnStartRule)monsterBattleStatsBottom.getAbilities().get(0).getEffect()).executeOnTurnStart(monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(11), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldHealWithHealAbility() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.HEAL));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 11, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(11), monsterBattleStatsBottom.getHealth().getValue());
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(7), monsterBattleStatsBottom.getHealth().getValue());

        ((OnTurnStartRule)monsterBattleStatsBottom.getAbilities().get(0).getEffect()).executeOnTurnStart(monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldNotHealWhenStunned() {
        List<Ability> abilities = new ArrayList<>();
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.HEAL));
        abilities.add(new IsStunnedAbility());
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 7, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.NONE, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.NONE, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.NONE, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop5 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.NONE, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop6 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.NONE, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(7, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 5, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.NONE, 10, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.NONE, 10, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.NONE, 10, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.NONE, 10, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom6 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.NONE, 10, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop5);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, monsterBattleStatsTop6);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, monsterBattleStatsBottom6);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeRound(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldHealWithHealAbility2() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.HEAL));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 5, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 12, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(12), monsterBattleStatsBottom.getHealth().getValue());
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(7), monsterBattleStatsBottom.getHealth().getValue());

        ((OnTurnStartRule)monsterBattleStatsBottom.getAbilities().get(0).getEffect()).executeOnTurnStart(monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(11), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldHealWithHealAbility3() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.HEAL));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 2, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 5, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom.getHealth().getValue());
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(3), monsterBattleStatsBottom.getHealth().getValue());

        ((OnTurnStartRule)monsterBattleStatsBottom.getAbilities().get(0).getEffect()).executeOnTurnStart(monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldHealWithBuffValuesWithHealAbility() {
        List<Ability> heal =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.HEAL));
        List<Ability> strengthen =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.STRENGTHEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 5, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 11, 1, heal);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 11, 1, strengthen);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        System.out.println(DEFAULT_GAME_CONTEXT);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(12), monsterBattleStatsBottom.getHealth().getValue());
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(7), monsterBattleStatsBottom.getHealth().getValue());

        ((OnTurnStartRule)monsterBattleStatsBottom.getAbilities().get(0).getEffect()).executeOnTurnStart(monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(11), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldHealTankWithTankHealAbility() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TANK_HEAL));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom.getSpeed().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getDamageValue(DamageType.ATTACK).getValue());
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());

        roundPhase.executeTurn(monsterBattleStatsBottom2, DamageType.NONE, DEFAULT_GAME_CONTEXT);

        DEFAULT_GAME_CONTEXT.printHistory();
        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldTriageHealWithTriageAbility() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TRIAGE));
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 5, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 6, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 2, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom1 =  new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop1, DamageType.MAGIC, monsterBattleStatsBottom1, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop2, DamageType.MAGIC, monsterBattleStatsBottom2, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop3, DamageType.MAGIC, monsterBattleStatsBottom3, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop4, DamageType.MAGIC, monsterBattleStatsBottom4, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop5, DamageType.MAGIC, monsterBattleStatsBottom5, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(4), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom4.getHealth().getValue());
        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom5.getHealth().getValue());

        ((OnTurnStartRule)monsterBattleStatsBottom1.getAbilities().get(0).getEffect()).executeOnTurnStart(monsterBattleStatsBottom1, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(7), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom4.getHealth().getValue());
        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom5.getHealth().getValue());
    }

    @Test
    public void itShouldRestoreArmorHealWithRepairAbility() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.REPAIR));
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 5, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 6, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 2, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom1 =  new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 10, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 10, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 10, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 10, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 10, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop5);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop1, DamageType.ATTACK, monsterBattleStatsBottom1, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop2, DamageType.RANGED, monsterBattleStatsBottom2, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop3, DamageType.RANGED, monsterBattleStatsBottom3, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop4, DamageType.RANGED, monsterBattleStatsBottom4, DEFAULT_GAME_CONTEXT);
        DEFAULT_ATTACK_SERVICE.attackSpecific(monsterBattleStatsTop5, DamageType.RANGED, monsterBattleStatsBottom5, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(4), monsterBattleStatsBottom3.getArmor().getValue());
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom4.getArmor().getValue());
        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom5.getArmor().getValue());

        ((OnTurnStartRule)monsterBattleStatsBottom1.getAbilities().get(0).getEffect()).executeOnTurnStart(monsterBattleStatsBottom1, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom3.getArmor().getValue());
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom4.getArmor().getValue());
        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom5.getArmor().getValue());
    }

    @Test
    public void itShouldNotDoAnythingOnRepairIfNoDamageToArmor() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.REPAIR));
        MonsterBattleStats monsterBattleStatsBottom1 =  new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 10, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 10, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 10, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 10, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 10, 10, 1, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getArmor().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom4.getArmor().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom5.getArmor().getValue());

        ((OnTurnStartRule)monsterBattleStatsBottom1.getAbilities().get(0).getEffect()).executeOnTurnStart(monsterBattleStatsBottom1, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom3.getArmor().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom4.getArmor().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom5.getArmor().getValue());
    }

    @Test
    public void itShouldAttackNextOnKillWithTrample() {
        List<Ability> trample =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TRAMPLE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, trample);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(2, 11, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 22, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(4, 33, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(5, 44, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom4.getHealth().getValue());
        RoundPhase roundPhase = new RoundPhase();

        System.out.println(DEFAULT_BOARD.getBoardBottom());
        roundPhase.executeRound(DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_BOARD.getBoardBottom());
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom2.getHealth().getValue());
    }

    @Test
    public void itShouldNotAttackOnKillingHitIfNoNextMonsterForTrample() {
        List<Ability> trample =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TRAMPLE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, trample);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(2, 11, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom.getHealth().getValue());
        RoundPhase roundPhase = new RoundPhase();

        System.out.println(DEFAULT_BOARD.getBoardBottom());
        roundPhase.executeRound(DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_BOARD.getBoardBottom());
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldAttackTheAttackerWhenRetaliate() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RETALIATE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom.getHealth().getValue());

        System.out.println(DEFAULT_BOARD.getBoardBottom());
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_BOARD.getBoardBottom());

        assertEquals(Integer.valueOf(90), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(99), monsterBattleStatsTop.getHealth().getValue());
    }

    @Test
    public void itShouldApplySnareToFlyingOnHit() {
        List<Ability> snare =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNARE));
        List<Ability> flying =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.FLYING));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, snare);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(3, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, flying);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertTrue(monsterBattleStatsBottom.containsAbility(AbilityType.SNARED));
    }

    @Test
    public void itShouldNotApplySnareOnNotFlyingOnHit() {
        List<Ability> snare =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNARE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, snare);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(3, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertFalse(monsterBattleStatsBottom.containsAbility(AbilityType.SNARED));
    }

    @Test
    public void itShouldApplyKnockout() {
        List<Ability> knockout =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.KNOCK_OUT));
        List<Ability> stunned =  Collections.singletonList(new IsStunnedAbility());
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 100, 1, knockout);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(3, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, stunned);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(80), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldApplyStun() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.STUN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 100, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(0, monsterBattleStatsBottom.getAbilities().size());

        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertTrue(monsterBattleStatsBottom.getAbilities().get(0).getAbilityType() == AbilityType.STUNNED);
    }

    @Test
    public void itShouldNotAttackWhenStunned() {
        List<Ability> abilities =  Collections.singletonList(new IsStunnedAbility());
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 100, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(0, monsterBattleStatsBottom.getAbilities().size());

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertTrue(monsterBattleStatsBottom.getAbilities().size() == 0);
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldAttackTwiceWithDoubleStrike() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.DOUBLE_STRIKE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 11, 0, 100, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(78), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldIncreaseHealthOnDeathWithScavenger() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SCAVENGER));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 100, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(3, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(4, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        System.out.println(DEFAULT_GAME_CONTEXT);

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(101), monsterBattleStatsTop.getHealth().getValue());
    }

    @Test
    public void itShouldResurrectTheFirstDeath() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RESURRECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldNotResurrectTheSecondDeath() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RESURRECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        RoundPhase roundPhase = new RoundPhase();
//        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);

        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom.getHealth().getValue());
//        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldNotTriggerResurrectOnEnemyDeath() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RESURRECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 100, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.resetAttackQueue();

        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldCleanseFriendlyTank() {
        List<Ability> cleanse =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.CLEANSE));
        List<Ability> abilities = new ArrayList<>();
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.PIERCING));
        List<Ability> debuffs = new ArrayList<>();
        debuffs.add(new IsStunnedAbility());
        List<Ability> slow = new ArrayList<>();
        slow.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SLOW));
        abilities.addAll(debuffs);
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 100, 1, slow);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 5, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 5, cleanse);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        RoundPhase roundPhase = new RoundPhase();
        assertEquals(2, monsterBattleStatsBottom.getAbilities().size());
        assertEquals(5, monsterBattleStatsBottom.getSpeed().getValue());

        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        assertEquals(2, monsterBattleStatsBottom.getAbilities().size());
        assertEquals(4, monsterBattleStatsBottom.getSpeed().getValue());

        roundPhase.executeTurn(monsterBattleStatsBottom2, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        DEFAULT_GAME_CONTEXT.printHistory();
        assertEquals(1, monsterBattleStatsBottom.getAbilities().size());
        assertEquals(AbilityType.PIERCING, monsterBattleStatsBottom.getAbilities().get(0).getAbilityType());
        assertEquals(5, monsterBattleStatsBottom.getSpeed().getValue());
    }

    @Test
    public void itShouldNotCleanseEnemyTank() {
        List<Ability> cleanse =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.CLEANSE));
        List<Ability> abilities = new ArrayList<>();
        abilities.add(new PierceAbility());
        List<Ability> debuffs = new ArrayList<>();//TODO: Add in poison and affliction
        debuffs.add(new IsStunnedAbility());
        abilities.addAll(debuffs);
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 100, 1, debuffs);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, cleanse);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        System.out.println(DEFAULT_GAME_CONTEXT);
        RoundPhase roundPhase = new RoundPhase();
        assertEquals(1, monsterBattleStatsTop.getAbilities().size());

        roundPhase.executeTurn(monsterBattleStatsBottom2, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(1, monsterBattleStatsTop.getAbilities().size());
        assertEquals(AbilityType.STUNNED, monsterBattleStatsTop.getAbilities().get(0).getAbilityType());
    }

    @Test
    public void itShouldNotHealWithTankHealWhenAfflictedAbility() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TANK_HEAL));
        List<Ability> affliction = Collections.singletonList(new AfflictedAbility());
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, affliction);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeTurn(monsterBattleStatsBottom2, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldNotHealWithHealWhenAfflictedAbility() {
        List<Ability> abilities = new ArrayList<>();
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TANK_HEAL));
        abilities.add(new AfflictedAbility());
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
        DEFAULT_ATTACK_SERVICE.attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeTurn(monsterBattleStatsBottom, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldDealPoisonDamageOnRoundStart() {
        List<Ability> abilities = Collections.singletonList(new IsPoisonedAbility());
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(101, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        MonsterBattleStats monsterBattleStats = new MonsterBattleStats(123, 3, MonsterType.MONSTER, DamageType.NONE, 0, 0, 100, 1, new ArrayList<>());
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStats);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);

        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeRound(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom.getHealth().getValue());
    }
}
