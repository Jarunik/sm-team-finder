package org.slos.battle.abilities;

import org.slos.battle.abilities.attack.AfflictionAbility;
import org.slos.battle.abilities.attack.BlastAbility;
import org.slos.battle.abilities.attack.DivineShieldAbility;
import org.slos.battle.abilities.attack.DodgeAbility;
import org.slos.battle.abilities.attack.DoubleStrikeAbility;
import org.slos.battle.abilities.attack.EnrageAbility;
import org.slos.battle.abilities.attack.FlyingAbility;
import org.slos.battle.abilities.attack.HalvingAbility;
import org.slos.battle.abilities.attack.KnockoutAbility;
import org.slos.battle.abilities.attack.LifeLeechAbility;
import org.slos.battle.abilities.attack.MagicReflectAbility;
import org.slos.battle.abilities.attack.PierceAbility;
import org.slos.battle.abilities.attack.PoisonAbility;
import org.slos.battle.abilities.attack.RetaliateAbility;
import org.slos.battle.abilities.attack.ReturnFireAbility;
import org.slos.battle.abilities.attack.ShatterAbility;
import org.slos.battle.abilities.attack.ShieldAbility;
import org.slos.battle.abilities.attack.SnareAbility;
import org.slos.battle.abilities.attack.StunAbility;
import org.slos.battle.abilities.attack.ThornsAbility;
import org.slos.battle.abilities.attack.TrampleAbility;
import org.slos.battle.abilities.attack.VoidAbility;
import org.slos.battle.abilities.attribute.BlindAbility;
import org.slos.battle.abilities.attribute.DemoralizeAbility;
import org.slos.battle.abilities.attribute.HeadwindsAbility;
import org.slos.battle.abilities.attribute.InspireAbility;
import org.slos.battle.abilities.attribute.ProtectAbility;
import org.slos.battle.abilities.attribute.RepairAbility;
import org.slos.battle.abilities.attribute.RustAbility;
import org.slos.battle.abilities.attribute.SilenceAbility;
import org.slos.battle.abilities.attribute.SlowAbility;
import org.slos.battle.abilities.attribute.StrengthenAbility;
import org.slos.battle.abilities.attribute.SwiftnessAbility;
import org.slos.battle.abilities.attribute.WeakenAbility;
import org.slos.battle.abilities.attribute.summoner.SummonerArmorAbility;
import org.slos.battle.abilities.attribute.summoner.SummonerAttackAbility;
import org.slos.battle.abilities.attribute.summoner.SummonerHealthAbility;
import org.slos.battle.abilities.attribute.summoner.SummonerMagicAbility;
import org.slos.battle.abilities.attribute.summoner.SummonerRangedAbility;
import org.slos.battle.abilities.attribute.summoner.SummonerSpeedAbility;
import org.slos.battle.abilities.death.LastStandAbility;
import org.slos.battle.abilities.death.RedemptionAbility;
import org.slos.battle.abilities.death.ResurrectAbility;
import org.slos.battle.abilities.death.ScavengerAbility;
import org.slos.battle.abilities.heal.CleanseAbility;
import org.slos.battle.abilities.heal.HealAbility;
import org.slos.battle.abilities.heal.TankHealAbility;
import org.slos.battle.abilities.heal.TriageAbility;
import org.slos.battle.abilities.target.OpportunityAbility;
import org.slos.battle.abilities.target.ReachAbility;
import org.slos.battle.abilities.target.SneakAbility;
import org.slos.battle.abilities.target.SnipeAbility;
import org.slos.battle.abilities.target.TauntAbility;

public class AbilityFactory {
    private static final StrengthenAbility STRENGTHEN_ABILITY = new StrengthenAbility();
    private static final BlindAbility BLIND_ABILITY = new BlindAbility();
    private static final WeakenAbility WEAKEN_ABILITY = new WeakenAbility();
    private static final ReachAbility REACH_ABILITY = new ReachAbility();
    private static final SnipeAbility SNIPE_ABILITY = new SnipeAbility();
    private static final SummonerRangedAbility SUMMONER_RANGED_ABILITY = new SummonerRangedAbility(1);
    private static final VoidAbility VOID_ABILITY = new VoidAbility();
    private static final ThornsAbility THORNS_ABILITY = new ThornsAbility();
    private static final LifeLeechAbility LIFE_LEECH_ABILITY = new LifeLeechAbility();
    private static final MagicReflectAbility MAGIC_REFLECT_ABILITY = new MagicReflectAbility();
    private static final ReturnFireAbility RETURN_FIRE_ABILITY = new ReturnFireAbility();
    private static final ShatterAbility SHATTER_ABILITY = new ShatterAbility();
    private static final PierceAbility PIERCE_ABILITY = new PierceAbility();
    private static final ShieldAbility SHIELD_ABILITY = new ShieldAbility();
    private static final BlastAbility BLAST_ABILITY = new BlastAbility();
    private static final DodgeAbility DODGE_ABILITY = new DodgeAbility();
    private static final FlyingAbility FLYING_ABILITY = new FlyingAbility();
    private static final OpportunityAbility OPPORTUNITY_ABILITY = new OpportunityAbility();
    private static final SneakAbility SNEAK_ABILITY = new SneakAbility();
    private static final HealAbility HEAL_ABILITY = new HealAbility();
    private static final TankHealAbility TANK_HEAL_ABILITY = new TankHealAbility();
    private static final InspireAbility INSPIRE_ABILITY = new InspireAbility();
    private static final DemoralizeAbility DEMORALIZE_ABILITY = new DemoralizeAbility();
    private static final HeadwindsAbility HEADWINDS_ABILITY = new HeadwindsAbility();
    private static final ProtectAbility PROTECT_ABILITY = new ProtectAbility();
    private static final SilenceAbility SILENCE_ABILITY = new SilenceAbility();
    private static final SwiftnessAbility SWIFTNESS_ABILITY = new SwiftnessAbility();
    private static final SlowAbility SLOW_ABILITY = new SlowAbility();
    private static final TriageAbility TRIAGE_ABILITY = new TriageAbility();
    private static final RepairAbility REPAIR_ABILITY = new RepairAbility();
    private static final RetaliateAbility RETALIATE_ABILITY = new RetaliateAbility();
    private static final StunAbility STUN_ABILITY = new StunAbility();
    private static final CleanseAbility CLEANSE_ABILITY = new CleanseAbility();
    private static final AfflictionAbility AFFLICTION_ABILITY = new AfflictionAbility();
    private static final PoisonAbility POISON_ABILITY = new PoisonAbility();
    private static final RustAbility RUST_ABILITY = new RustAbility();
    private static final ScavengerAbility SCAVENGER_ABILITY = new ScavengerAbility();
    private static final TauntAbility TAUNT_ABILITY = new TauntAbility();
    private static final SnareAbility SNARE_ABILITY = new SnareAbility();
    private static final KnockoutAbility KNOCK_OUT_ABILITY = new KnockoutAbility();
    private static final HalvingAbility HALVING_ABILITY = new HalvingAbility();

    public Ability getSummonerAbility(AbilityType abilityType, int amount) {
        switch (abilityType) {
            case SUMMONER_SPEED:
                return new SummonerSpeedAbility(amount);
            case SUMMONER_HEALTH:
                return new SummonerHealthAbility(amount);
            case SUMMONER_ARMOR:
                return new SummonerArmorAbility(amount);
            case SUMMONER_RANGED:
                return new SummonerRangedAbility(amount);
            case SUMMONER_ATTACK:
                return new SummonerAttackAbility(amount);
            case SUMMONER_MAGIC:
                return new SummonerMagicAbility(amount);
//            case SUMMONER_BLAST:
//                return new SummonerBlastAbility();
//            case SUMMONER_VOID: //TODO
//                return new SummonerVoidAbility();
//            case SUMMONER_AFFLICTION:
//                return new SummonerAffliction();
        }

        throw new IllegalStateException("Unable to create summoner ability for: " + abilityType);
    }

    public Ability getAbility(AbilityType abilityType) {
        switch(abilityType) {
            case DIVINE_SHIELD:
                return new DivineShieldAbility();
            case DOUBLE_STRIKE:
                return new DoubleStrikeAbility();
            case RESURRECT:
                return new ResurrectAbility();
            case ENRAGE:
                return new EnrageAbility();
            case TRAMPLE:
                return new TrampleAbility();
            case REDEMPTION:
                return new RedemptionAbility();
            case LAST_STAND:
                return new LastStandAbility();
            case HALVING:
                return HALVING_ABILITY;
            case STRENGTHEN:
                return STRENGTHEN_ABILITY;
            case BLIND:
                return BLIND_ABILITY;
            case REACH:
                return REACH_ABILITY;
            case SNIPE:
                return SNIPE_ABILITY;
            case WEAKEN:
                return WEAKEN_ABILITY;
            case VOID:
                return VOID_ABILITY;
            case THORNS:
                return THORNS_ABILITY;
            case LIFE_LEECH:
                return LIFE_LEECH_ABILITY;
            case MAGIC_REFLECT:
                return MAGIC_REFLECT_ABILITY;
            case RETURN_FIRE:
                return RETURN_FIRE_ABILITY;
            case SHATTER:
                return SHATTER_ABILITY;
            case PIERCING:
                return PIERCE_ABILITY;
            case SHIELD:
                return SHIELD_ABILITY;
            case BLAST:
                return BLAST_ABILITY;
            case DODGE:
                return DODGE_ABILITY;
            case FLYING:
                return FLYING_ABILITY;
            case OPPORTUNITY:
                return OPPORTUNITY_ABILITY;
            case SNEAK:
                return SNEAK_ABILITY;
            case HEAL:
                return HEAL_ABILITY;
            case TANK_HEAL:
                return TANK_HEAL_ABILITY;
            case INSPIRE:
                return INSPIRE_ABILITY;
            case DEMORALIZE:
                return DEMORALIZE_ABILITY;
            case HEADWINDS:
                return HEADWINDS_ABILITY;
            case PROTECT:
                return PROTECT_ABILITY;
            case SILENCE:
                return SILENCE_ABILITY;
            case SWIFTNESS:
                return SWIFTNESS_ABILITY;
            case SLOW:
                return SLOW_ABILITY;
            case TRIAGE:
                return TRIAGE_ABILITY;
            case REPAIR:
                return REPAIR_ABILITY;
            case RETALIATE:
                return RETALIATE_ABILITY;
            case STUN:
                return STUN_ABILITY;
            case CLEANSE:
                return CLEANSE_ABILITY;
            case AFFLICTION:
                return AFFLICTION_ABILITY;
            case POISON:
                return POISON_ABILITY;
            case RUST:
                return RUST_ABILITY;
            case SCAVENGER:
                return SCAVENGER_ABILITY;
            case TAUNT:
                return TAUNT_ABILITY;
            case SNARE:
                return SNARE_ABILITY;
            case KNOCK_OUT:
                return KNOCK_OUT_ABILITY;
        }

        throw new IllegalStateException("Unable to create ability for: " + abilityType);
    }
}
