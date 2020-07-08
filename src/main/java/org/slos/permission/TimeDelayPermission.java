package org.slos.permission;

import org.slos.permission.configuration.ConfigurationUpdateRequest;
import org.slos.permission.configuration.ConfigurationUpdateResponse;
import org.slos.permission.configuration.TimeDelayConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeDelayPermission implements PermissionService {
    public static String PLAY_DELAY_SETTING = "playDelay";
    @Autowired
    TimeDelayConfiguration timeDelayConfiguration;
    private final Map<String, Long> mostRecentGame = new HashMap<>();

    @Override
    public PermissionResponse hasPermission(PermissionRequest permissionRequest) {
        String player = permissionRequest.getPlayer();
        Boolean timePermission = true;
        List<String> messages = new ArrayList<>();

        Long lastPlayTime = mostRecentGame.get(player);

        if (lastPlayTime != null) {
            Integer configuredTimeInbetweenPlay = timeDelayConfiguration.getDefaultDelay();
            Map<String, Integer> delayMapping = timeDelayConfiguration.getDelays();

            if (delayMapping.containsKey(player)) {
                configuredTimeInbetweenPlay = delayMapping.get(player);
            }

            Long now = System.currentTimeMillis();
            Long lastPlayedAgo = (now - lastPlayTime);
            Integer lastPlayedAgoInSeconds = (int)(lastPlayedAgo / 1000);
            messages.add("Played ago vs configured: " + lastPlayedAgoInSeconds + "/" + configuredTimeInbetweenPlay);

            if (lastPlayedAgoInSeconds > configuredTimeInbetweenPlay) {
                timePermission = true;
            }
            else {
                timePermission = false;
            }
        }
        else {
            messages.add("No known last play time.");
        }


        if (timePermission == true) {
            mostRecentGame.put(player, System.currentTimeMillis());
        }

        return new PermissionResponse(timePermission, messages);
    }

    @Override
    public ConfigurationUpdateResponse updateService(ConfigurationUpdateRequest configurationUpdateRequest) {
        timeDelayConfiguration.setDefaultDelay(configurationUpdateRequest.getDefaultPlayDelay());
        Map<String, Map<String, String>> allOverrides = configurationUpdateRequest.getUserOverrides();
        List<String> messages = new ArrayList<>();

        for (String player : allOverrides.keySet()) {
            Map<String, String> userOverrides = allOverrides.get(player);

            String overrideValue = userOverrides.get(PLAY_DELAY_SETTING);

            if (overrideValue != null) {
                timeDelayConfiguration.setDelayFor(player, Integer.parseInt(overrideValue));
            }
        }

        messages.add("Updated default play delay to: " + timeDelayConfiguration.getDefaultDelay());
        messages.add("Updated play delays to: " + timeDelayConfiguration.getDelays());

        ConfigurationUpdateResponse configurationUpdateResponse = new ConfigurationUpdateResponse(messages);

        return configurationUpdateResponse;
    }
}
