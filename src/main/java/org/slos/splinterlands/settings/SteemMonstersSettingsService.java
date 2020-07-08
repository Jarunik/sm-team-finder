package org.slos.splinterlands.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slos.splinterlands.SplinterlandsClient;
import org.springframework.beans.factory.annotation.Autowired;

public class SteemMonstersSettingsService {
    private static final Integer refreshInSeconds = 60;
    private Long cacheAge;
    @Autowired SplinterlandsClient splinterlandsClient;
    @Autowired ObjectMapper objectMapper;
    private static SMSettings smSettings = null;

    public synchronized SMSettings getSettings() {
        if ((smSettings == null) || (cacheAge < (System.currentTimeMillis() - (refreshInSeconds * 1000)))) {
            smSettings = splinterlandsClient.getSettings();

            try {
                cacheAge = System.currentTimeMillis();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return smSettings;
    }
}
