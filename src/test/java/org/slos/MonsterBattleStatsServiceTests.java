package org.slos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slos.battle.abilities.AbilityFactory;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.MonsterDetails;
import org.slos.splinterlands.domain.monster.MonsterStats;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MonsterBattleStatsServiceTests implements TestHelper {
    private MonsterBattleStatsService monsterBattleStatsService = new MonsterBattleStatsService(null, new AbilityFactory());

    private ObjectMapper objectMapper = new ObjectMapper();
    private String mimosa = "{\"id\":235,\"name\":\"Mimosa Nightshade\",\"color\":\"Black\",\"type\":\"Summoner\",\"sub_type\":null,\"rarity\":4,\"drop_rate\":0,\"stats\":{\"mana\":7,\"attack\":0,\"ranged\":-1,\"magic\":0,\"armor\":0,\"health\":0,\"speed\":0,\"abilities\":[\"Void\",\"Affliction\"]},\"is_starter\":false,\"editions\":\"4\",\"created_block_num\":41530757,\"last_update_tx\":\"2bd2e2eb17b6e4062b1c24095686bc2bba928ba9\",\"total_printed\":1503}";

    @Test
    public void itShouldGetSummonerWithAbilities() throws Exception {
        MonsterDetails monsterDetails = objectMapper.readValue(mimosa, MonsterDetails.class);
        int cardId = 235;
        int level = 0;
        MonsterType type = MonsterType.SUMMONER;
        MonsterStats monsterStats = monsterDetails.getMonsterStats();

        MonsterBattleStats mimosaBattleStats = monsterBattleStatsService.getActiveMonsterStats(cardId, level, MonsterType.SUMMONER, monsterStats);
        Assertions.assertEquals(3, mimosaBattleStats.getAbilities().size());
    }
}
