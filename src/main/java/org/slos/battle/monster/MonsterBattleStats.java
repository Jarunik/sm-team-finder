package org.slos.battle.monster;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.board.BoardPlacement;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonsterBattleStats {
    private Integer id;
    private Integer mana;
    private MonsterType type;
    private BattleAttributes battleAttributes;
    private List<Ability> abilities = new ArrayList<>();
    @JsonIgnore
    private BoardPlacement placedIn;

    public MonsterBattleStats(Integer id, Integer mana, MonsterType type, DamageType damageType, BattleAttributes battleAttributes, List<Ability> abilities) {
        this.id = id;
        this.mana = mana;
        this.type = type;
        this.battleAttributes = battleAttributes;
        this.abilities = abilities;
    }

    public MonsterBattleStats(Integer id, Integer mana, MonsterType type, Map<DamageType, BattleAttribute> damage, Integer armor, Integer health, Integer speed, List<Ability> abilities) {
        this.id = id;
        this.mana = mana;
        this.type = type;

        BattleAttribute damageValueBA = null;
        BattleAttribute armorBA = null;
        if (armor != null) {
            armorBA = new BattleAttribute(armor, false);
        }
        else {
            armorBA = new BattleAttribute(0, false);
        }
        BattleAttribute healthBA = new BattleAttribute(health);
        BattleAttribute speedBA = new BattleAttribute(speed);
        BattleAttribute baseHitChanceBA = new BattleAttribute(100);

        this.battleAttributes = new BattleAttributes(damage, armorBA, healthBA, speedBA, baseHitChanceBA);

        if ((abilities != null) && (abilities.size() > 0)) {
            this.abilities.addAll(abilities);
        }
    }
    public MonsterBattleStats(Integer id, Integer mana, MonsterType type, DamageType damageType, Integer damageValue, Integer armor, Integer health, Integer speed, List<Ability> abilities) {
        this.id = id;
        this.mana = mana;
        this.type = type;

        BattleAttribute damageValueBA = null;
        if (damageValue != null) {
            damageValueBA = new BattleAttribute(damageValue);
        }
        else {
            damageValueBA = new BattleAttribute(0);
        }
        BattleAttribute armorBA = null;
        if (armor != null) {
            armorBA = new BattleAttribute(armor, false);
        }
        else {
            armorBA = new BattleAttribute(0, false);
        }
        BattleAttribute healthBA = new BattleAttribute(health);
        BattleAttribute speedBA = new BattleAttribute(speed);
        BattleAttribute baseHitChanceBA = new BattleAttribute(100);
        Map<DamageType, BattleAttribute> damageTypeBattleAttributeMap = new HashMap<>();
        damageTypeBattleAttributeMap.put(damageType, damageValueBA);
        this.battleAttributes = new BattleAttributes(damageTypeBattleAttributeMap, armorBA, healthBA, speedBA, baseHitChanceBA);

        if ((abilities != null) && (abilities.size() > 0)) {
            this.abilities.addAll(abilities);
        }
    }

    public void addDamage(DamageType damageType, Integer damageAmount) {
        BattleAttribute damage = new BattleAttribute(damageAmount, true);
        battleAttributes.getDamageValues().put(damageType, damage);
    }

    public boolean isOfDamageType(DamageType damageType) {
        for (DamageType hasDamageType : battleAttributes.getDamageValues().keySet()) {
            if (hasDamageType.equals(damageType)) {
                if (battleAttributes.getDamageValues().get(hasDamageType).getValue() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }

    public boolean isDead() {
        return getHealth().getValue() <= 0;
    }

    public MonsterBattleStats() {}

    public Integer getMana() {
        return mana;
    }

    public MonsterType getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    public BattleAttribute getDamageValue(DamageType damageType) {
        if (damageType.NONE.equals(damageType)) {
            return new BattleAttribute(0);
        }

        return battleAttributes.getDamageValues().get(damageType);
    }

    public BattleAttribute getDamageValue(BattleAttributeType damageType) {
        return battleAttributes.getDamageValues().get(damageType);
    }

    public Map<DamageType, BattleAttribute> getDamageValues() {
        return battleAttributes.getDamageValues();
    }

    public void resetArmor() {
        battleAttributes.getArmor().setValue(battleAttributes.getArmor().getBaseValue());
        battleAttributes.getArmor().setBuffValue(0);
        battleAttributes.getArmor().setBuffBaseAmount(0);
    }

    public BattleAttribute getArmor() {
        return battleAttributes.getArmor();
    }

    public BattleAttribute getHealth() {
        return battleAttributes.getHealth();
    }

    public BattleAttribute getSpeed() {
        return battleAttributes.getSpeed();
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public BattleAttribute getBaseHitChance() {
        return battleAttributes.getBaseHitChance();
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    public BoardPlacement getPlacedIn() {
        return placedIn;
    }

    public void setPlacedIn(BoardPlacement placedIn) {
        this.placedIn = placedIn;
    }

    public boolean containsAbility(AbilityType abilityType) {
        for (Ability ability : abilities) {
            if (ability.getAbilityType() == abilityType) {
                return true;
            }
        }
        return false;
    }

    public BattleAttribute getAttributeOfType(BattleAttributeType battleAttributeType) {
        switch (battleAttributeType) {
            case HEALTH:
                return getHealth();
            case HIT_CHANCE:
                return getBaseHitChance();
            case ATTACK:
            case MAGIC:
            case RANGED:
                return getDamageValue(battleAttributeType);
            case ARMOR:
                return getArmor();
            case SPEED:
                return getSpeed();
        }

        throw new IllegalStateException("MonsterBattleStats is not configured to return type: " + battleAttributeType);
    }

    @Override
    public String toString() {
        return "MonsterBattleStats{" +
                "id=" + id +
                ", mana=" + mana +
                ", type=" + type +
                ", battleAttributes=" + battleAttributes +
                ", abilities=" + abilities +
                ", placedIn=" + placedIn +
                '}';
    }

}
