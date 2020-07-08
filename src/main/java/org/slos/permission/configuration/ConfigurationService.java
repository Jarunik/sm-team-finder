package org.slos.permission.configuration;

import org.slos.permission.ActivePlayPermissionService;
import org.slos.permission.MaxRankPermissionService;
import org.slos.permission.MaxRatingService;
import org.slos.permission.SafeToPlayLookupService;
import org.slos.permission.TimeDelayPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationService implements ConfigurationUpdateService {
    @Autowired ActivePlayPermissionService activePlayPermissionService;
    @Autowired SafeToPlayLookupService safeToPlayLookupService;
    @Autowired TimeDelayPermission timeDelayPermission;
    @Autowired MaxRatingService maxRatingService;
    @Autowired MaxRankPermissionService maxRankPermissionService;

    Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    @Override
    public ConfigurationUpdateResponse updateService(ConfigurationUpdateRequest configurationUpdateRequest) {
        System.out.println("Updating configuration: " + configurationUpdateRequest.toJson());

        List<String> messages = new ArrayList<>();

        try {
            if (configurationUpdateRequest.getActivePlay() != null) {
                messages.addAll(activePlayPermissionService.updateService(configurationUpdateRequest).getMessage());
            }

            if (configurationUpdateRequest.getSafetyLevel() != null) {
                messages.addAll(safeToPlayLookupService.updateService(configurationUpdateRequest).getMessage());
            }

            if (configurationUpdateRequest.getDefaultPlayDelay() != null) {
                messages.addAll(timeDelayPermission.updateService(configurationUpdateRequest).getMessage());
            }

            messages.addAll(maxRankPermissionService.updateService(configurationUpdateRequest).getMessage());
            messages.addAll(maxRatingService.updateService(configurationUpdateRequest).getMessage());
        }
        catch (Exception e) {
            logger.error("Exception setting settings.", e);
            messages.add("Exception updating settings: " + e);
        }

        return new ConfigurationUpdateResponse(messages);
    }
}
