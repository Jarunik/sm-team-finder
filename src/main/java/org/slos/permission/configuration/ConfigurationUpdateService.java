package org.slos.permission.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConfigurationUpdateService {
    ConfigurationUpdateResponse updateService(ConfigurationUpdateRequest configurationUpdateRequest);

    default Optional<String> getValueForUser(String userKey, String valueKey, ConfigurationUpdateRequest configurationUpdateRequest) {
        Map<String, Map<String, String>> allOverrides = configurationUpdateRequest.getUserOverrides();
        List<String> messages = new ArrayList<>();

        for (String user : allOverrides.keySet()) {
            if (user.equals(userKey)) {
                Map<String, String> userOverrides = allOverrides.get(user);

                String overrideValue = userOverrides.get(valueKey);

                if (overrideValue != null) {
                    return Optional.of(userOverrides.get(valueKey));
                }
            }
        }

        return Optional.empty();
    }
}
