package org.slos.permission;

import org.slos.permission.configuration.ConfigurationUpdateRequest;
import org.slos.permission.configuration.ConfigurationUpdateResponse;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxRatingService implements PermissionService {

    public static final String MAX_RATING_SETTING = "maxRating";

    @Value("${defaults.permission.maxRating}") Integer maxRatingDefault;
    private Integer maxRatingOverride;
    private Map<String, Integer> overrides = new HashMap<>();

    @Override
    public PermissionResponse hasPermission(PermissionRequest permissionRequest) {
        if (permissionRequest.getRating() >= maxRatingDefault) {
            Boolean isActive = false;
            String message = "Play is above rating limit: " + isActive;

            return new PermissionResponse(isActive, Collections.singletonList(message));
        }

        Boolean isActive = true;
        String message = "Play is below rating limit: " + isActive;

        return new PermissionResponse(isActive, Collections.singletonList(message));
    }

    @Override
    public ConfigurationUpdateResponse updateService(ConfigurationUpdateRequest configurationUpdateRequest) {
        String baseOverrideRaw = configurationUpdateRequest.getBaseOverrides().get(MAX_RATING_SETTING);

        if (baseOverrideRaw != null) {
            this.maxRatingOverride = Integer.parseInt(baseOverrideRaw);
        }

        Map<String, Map<String, String>> allOverrides = configurationUpdateRequest.getUserOverrides();
        List<String> messages = new ArrayList<>();

        for (String user : allOverrides.keySet()) {
            Map<String, String> userOverrides = allOverrides.get(user);

            String overrideValue = userOverrides.get(MAX_RATING_SETTING);

            if (overrideValue != null) {
                overrides.put(user, Integer.parseInt(overrideValue));
            }
        }

        messages.add("MaxRatingOverrides set to: " + maxRatingOverride);
        return new ConfigurationUpdateResponse(messages);
    }
}
