package org.slos;

import org.slos.battle.GameContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityFactory;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.buff.BattleAttributeBuff;
import org.slos.battle.abilities.buff.Buff;
import org.slos.battle.abilities.buff.BuffApplication;
import org.slos.battle.abilities.buff.BuffService;
import org.slos.battle.monster.BattleAttributeType;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;


public class BuffServiceTests implements TestHelper {
    private AbilityFactory DEFAULT_ABILITY_FACTORY = new AbilityFactory();
    private BuffService DEFAULT_BUFF_SERVICE;
    private Buff DEFAULT_BUFF;
    private MonsterBattleStats DEFAULT_APPLYING_BUFF;
    private MonsterBattleStats DEFAULT_GETTING_BUFF_APPLIED_TO;
    private GameContext DEFAULT_GAME_CONTEXT = new GameContext(getDefaultBoard(), new HashSet<>());

    @BeforeEach
    public void setup() {
        DEFAULT_BUFF_SERVICE = new BuffService();
        DEFAULT_BUFF = new BattleAttributeBuff(BattleAttributeType.HEALTH, -1);
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.WEAKEN));
        DEFAULT_APPLYING_BUFF = new MonsterBattleStats(1, 100, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 1, 100, abilities);
        DEFAULT_GETTING_BUFF_APPLIED_TO = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
    }

    @Test
    public void itShouldRegisterBuffApplication() {
        DEFAULT_BUFF_SERVICE.applyBuff(DEFAULT_BUFF, DEFAULT_APPLYING_BUFF, DEFAULT_GETTING_BUFF_APPLIED_TO, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(9), DEFAULT_GETTING_BUFF_APPLIED_TO.getHealth().getValue());
    }

    @Test
    public void itShouldRegisterTheAppliedToRecord() {
        DEFAULT_BUFF_SERVICE.applyBuff(DEFAULT_BUFF, DEFAULT_APPLYING_BUFF, DEFAULT_GETTING_BUFF_APPLIED_TO, DEFAULT_GAME_CONTEXT);

        List<BuffApplication> buffs = DEFAULT_BUFF_SERVICE.getBuffsAppliedTo(DEFAULT_GETTING_BUFF_APPLIED_TO);

        assertNotNull(buffs);
        assertEquals(1, buffs.size());
        assertSame(DEFAULT_GETTING_BUFF_APPLIED_TO, buffs.get(0).getAppliedTo());
        assertSame(DEFAULT_APPLYING_BUFF, buffs.get(0).getAppliedBy());
        assertSame(DEFAULT_BUFF, buffs.get(0).getBuff());
    }

    @Test
    public void itShouldRegisterTheAppliedByRecord() {
        DEFAULT_BUFF_SERVICE.applyBuff(DEFAULT_BUFF, DEFAULT_APPLYING_BUFF, DEFAULT_GETTING_BUFF_APPLIED_TO, DEFAULT_GAME_CONTEXT);

        List<BuffApplication> buffs = DEFAULT_BUFF_SERVICE.getBuffsAppliedBy(DEFAULT_APPLYING_BUFF);

        assertNotNull(buffs);
        assertEquals(1, buffs.size());
        assertSame(DEFAULT_APPLYING_BUFF, buffs.get(0).getAppliedBy());
        assertSame(DEFAULT_GETTING_BUFF_APPLIED_TO, buffs.get(0).getAppliedTo());
        assertSame(DEFAULT_BUFF, buffs.get(0).getBuff());
    }
}
