package org.slos.battle.abilities;

import org.slos.battle.abilities.rule.target.TargetRuleset;

import java.util.Arrays;
import java.util.List;

public abstract class Ability {
    private AbilityType abilityType;
    private List<AbilityClassification> abilityClassifications;

    public Ability(AbilityType abilityType, AbilityClassification... abilityClassification) {
        this.abilityType = abilityType;
        this.abilityClassifications = Arrays.asList(abilityClassification);
    }

    public AbilityType getAbilityType() {
        return abilityType;
    }

    public List<AbilityClassification> getAbilityClassifications() {
        return abilityClassifications;
    }

    public boolean containsClassification(AbilityClassification abilityClassification) {
        return abilityClassifications.contains(abilityClassification);
    }

    public abstract AbilityEffect getEffect();
    public abstract TargetRuleset getTargetRuleset(); //TODO: This is only needed for buffs, rework this

    @Override
    public String toString() {
        return "Ability{" +
                "abilityType=" + abilityType +
                ", abilityClassification=" + abilityClassifications +
                '}';
    }
}
