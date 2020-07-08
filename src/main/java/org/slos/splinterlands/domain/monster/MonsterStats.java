package org.slos.splinterlands.domain.monster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.slos.battle.abilities.AbilityType;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = StatsDeseralizer.class)
public class MonsterStats {
    private Integer[] mana;
    private Integer[] attack;
    private Integer[] ranged;
    private Integer[] magic;
    private Integer[] armor;
    private Integer[] health;
    private Integer[] speed;
    private AbilityType[][] abilities;

    @JsonCreator
    public MonsterStats(@JsonProperty("mana") Integer[] mana, @JsonProperty("attack") Integer[] attack, @JsonProperty("ranged") Integer[] ranged, @JsonProperty("magic") Integer[] magic, @JsonProperty("armor") Integer[] armor, @JsonProperty("health") Integer[] health, @JsonProperty("speed") Integer[] speed, @JsonProperty("abilities") AbilityType[][] abilities) {
        this.mana = mana;
        this.attack = attack;
        this.ranged = ranged;
        this.magic = magic;
        this.armor = armor;
        this.health = health;
        this.speed = speed;
        this.abilities = abilities;
    }

    public int maxLevel() {
        for(int a = 0; a < health.length; a++) {
            if (health[a] == null) {
                return a;
            }
        }
        return health.length;
    }

    public Integer[] getMana() {
        return mana;
    }

    public Integer[] getAttack() {
        return attack;
    }

    public Integer[] getRanged() {
        return ranged;
    }

    public Integer[] getMagic() {
        return magic;
    }

    public Integer[] getArmor() {
        return armor;
    }

    public Integer[] getHealth() {
        return health;
    }

    public Integer[] getSpeed() {
        return speed;
    }

    public AbilityType[][] getAbilities() {
        return abilities;
    }

    @Override
    public String toString() {
        return "MonsterStats{" +
                "mana=" + Arrays.toString(mana) +
                ", attack=" + Arrays.toString(attack) +
                ", ranged=" + Arrays.toString(ranged) +
                ", magic=" + Arrays.toString(magic) +
                ", armor=" + Arrays.toString(armor) +
                ", health=" + Arrays.toString(health) +
                ", speed=" + Arrays.toString(speed) +
                ", abilities=" + Arrays.deepToString(abilities) +
                '}';
    }
}
