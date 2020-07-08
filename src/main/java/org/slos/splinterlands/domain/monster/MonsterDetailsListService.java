package org.slos.splinterlands.domain.monster;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slos.TeamSuggestionToActualCardsService;
import org.slos.splinterlands.SplinterlandsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonsterDetailsListService {
    @Autowired private ObjectMapper objectMapper;
    @Autowired private SplinterlandsClient splinterlandsClient;

    private static MonsterDetailsList monsterDetailsList = null;

    private final Logger logger = LoggerFactory.getLogger(TeamSuggestionToActualCardsService.class);

    public MonsterDetailsList getMonsterDetailsList() {
        if (monsterDetailsList == null) {
            try {
                String monsterDetailsRaw = splinterlandsClient.getCardDetails().getBody();

                monsterDetailsRaw = "{\"monsterDetailsList\":" + monsterDetailsRaw + "}";
                monsterDetailsList = objectMapper.readValue(monsterDetailsRaw, MonsterDetailsList.class);

                return monsterDetailsList;
            } catch (Exception e) {
                logger.error("Error creating monster details list!", e);
                e.printStackTrace();
            }
        }

        return monsterDetailsList;
    }
}
