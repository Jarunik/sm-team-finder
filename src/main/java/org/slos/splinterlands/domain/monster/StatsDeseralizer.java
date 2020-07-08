package org.slos.splinterlands.domain.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slos.battle.abilities.AbilityType;

import java.io.IOException;

public class StatsDeseralizer extends StdDeserializer<MonsterStats> {
    public StatsDeseralizer() {
        this(null);
    }
    public StatsDeseralizer(Class<?> vc) {
        super(vc);
    }

    @Override
    public MonsterStats deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        Integer[] mana = convertToArray(node.get("mana"));
        Integer[] attack = convertToArray(node.get("attack"));
        Integer[] ranged = convertToArray(node.get("ranged"));
        Integer[] magic = convertToArray(node.get("magic"));
        Integer[] armor = convertToArray(node.get("armor"));
        Integer[] health = convertToArray(node.get("health"));
        Integer[] speed = convertToArray(node.get("speed"));
        AbilityType[][] abilities = convertToAbilitiesArray(node.get("abilities"));

        return new MonsterStats(mana, attack, ranged, magic, armor, health, speed, abilities);
    }

    private AbilityType[][] convertToAbilitiesArray(JsonNode jsonNode) {
        AbilityType[][] abilities = new AbilityType[10][5];
        if (jsonNode == null) {
            return abilities;
        }

        int x = 0;
        int y = 0;

        for (JsonNode abilityNode: jsonNode) {
            if (abilityNode.isTextual()) {
                abilities[0][x] = AbilityType.valueOf(getName(abilityNode));
            }
            else {
                for (JsonNode innerAbilityNode : abilityNode) {
                    abilities[x][y] = AbilityType.valueOf(getName(innerAbilityNode));
                    y++;
                }
            }
            x++;
        }

        return abilities;
    }

    private String getName(JsonNode innerAbilityNode) {
        return innerAbilityNode.toString()
                .replaceAll("\"", "")
                .replaceAll(" ", "_")
                .toUpperCase();
    }

    private Integer[] convertToArray(JsonNode node) {
        Integer[] values = new Integer[10];
        if (node.isArray()) {
            int i = 0;
            for (JsonNode intNode : node) {
                values[i] = intNode.asInt();
                i++;
            }
            return values;
        }
        Integer value = node.asInt();
        return new Integer[] {value, value, value, value, value, value, value, value, value, value};
    }
}
