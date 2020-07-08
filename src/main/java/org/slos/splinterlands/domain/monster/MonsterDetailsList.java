package org.slos.splinterlands.domain.monster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonsterDetailsList {
    private MonsterDetails[] monsterDetailsList;

    @JsonCreator
    public MonsterDetailsList(@JsonProperty("monsterDetailsList") MonsterDetails[] monsterDetailsList) {
        this.monsterDetailsList = monsterDetailsList;
    }

    public MonsterDetails getDetails(Integer id) {
        for (MonsterDetails monsterDetails : monsterDetailsList) {
            if (monsterDetails.getId().equals(id)) {
                return monsterDetails;
            }
        }
        return null;
    }

    public MonsterDetails[] getMonsterDetailsList() {
        return monsterDetailsList;
    }

    public MonsterDetails[] getSummoners() {
        return Arrays.stream(monsterDetailsList).filter(monsterDetails -> monsterDetails.getType().equals(MonsterType.SUMMONER)).collect(Collectors.toList()).toArray(new MonsterDetails[0]);
    }

    public MonsterDetails[] getMonsters() {
        return Arrays.stream(monsterDetailsList).filter(monsterDetails -> monsterDetails.getType().equals(MonsterType.MONSTER)).collect(Collectors.toList()).toArray(new MonsterDetails[0]);
    }

    @Override
    public String toString() {
        return "MonsterDetailsList{" +
                "monsterDetailsList=" + Arrays.toString(monsterDetailsList) +
                '}';
    }
}
