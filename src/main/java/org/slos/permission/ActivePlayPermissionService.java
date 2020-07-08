package org.slos.permission;

import org.slos.permission.configuration.ConfigurationUpdateRequest;
import org.slos.permission.configuration.ConfigurationUpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ActivePlayPermissionService implements PermissionService {
    public static final String ACTIVE_PLAY_SETTING = "activePlay";

    @Autowired ActivePlayService activePlayService;

    @Override
    public PermissionResponse hasPermission(PermissionRequest permissionRequest) {
        Boolean isActive = activePlayService.getIsActive(permissionRequest.getPlayer());
        String message = "Play is active: " + isActive;

        return new PermissionResponse(isActive, Collections.singletonList(message));
    }

    @Override
    public ConfigurationUpdateResponse updateService(ConfigurationUpdateRequest configurationUpdateRequest) {
        List<String> messages = new ArrayList<>();

        Boolean setActivePlay = configurationUpdateRequest.getActivePlay();
        activePlayService.setIsActive(setActivePlay);
        messages.add("Active play default set to: " + setActivePlay);

//        Map<String, Boolean> getManualActiveOverride = configurationUpdateRequest.getManualActiveOverride();
//        if (getManualActiveOverride != null) {
//            activePlayService.setManualOverride(getManualActiveOverride);
//        }

        Map<String, Map<String, String>> overrides = configurationUpdateRequest.getUserOverrides();
        for (String user : overrides.keySet()) {
            Optional<String> getValueForUser = getValueForUser(user, ACTIVE_PLAY_SETTING, configurationUpdateRequest);

            if (getValueForUser.isPresent()) {
                activePlayService.setManualOverride(user, Boolean.parseBoolean(getValueForUser.get()));
            }
        }

        messages.add("Active play set to: " + activePlayService.getManualOverride());

        return new ConfigurationUpdateResponse(messages);
    }
}
