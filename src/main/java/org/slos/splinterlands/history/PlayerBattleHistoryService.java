package org.slos.splinterlands.history;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slos.splinterlands.SplinterlandsClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.WeakHashMap;

public class PlayerBattleHistoryService {
    @Autowired private SplinterlandsClient splinterlandsClient;
    @Autowired private ObjectMapper objectMapper;

    private Map<String, BattleHistory> battleHistoryCache = new WeakHashMap<>();

    public BattleHistory getBattleHistoryFor(String id) {
        String response = splinterlandsClient.getBattleHistory(id).getBody();
        try {
            BattleHistory history = objectMapper.readValue(response, BattleHistory.class);
            battleHistoryCache.put(id, history);
            return history;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
