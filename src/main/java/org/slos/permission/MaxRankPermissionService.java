package org.slos.permission;

import org.slos.permission.configuration.ConfigurationUpdateRequest;
import org.slos.permission.configuration.ConfigurationUpdateResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO Flesh this out more
public class MaxRankPermissionService implements PermissionService {
    public static final Integer MAX_RANK_DEFAULT = 1;

    @Override
    public PermissionResponse hasPermission(PermissionRequest permissionRequest) {
        List<String> messages = new ArrayList<>();

        if ((permissionRequest.getRank() != null) && (permissionRequest.getRank() == MAX_RANK_DEFAULT)) {

            messages.add("Rank is at maximum limit of: " + MAX_RANK_DEFAULT);
            return new PermissionResponse(false, messages);
        }

        return new PermissionResponse(true, messages);
    }

    @Override
    public ConfigurationUpdateResponse updateService(ConfigurationUpdateRequest configurationUpdateRequest) {
        return new ConfigurationUpdateResponse(Collections.singletonList("Max Rank Permission Not Yet Implemented!"));
    }
}
