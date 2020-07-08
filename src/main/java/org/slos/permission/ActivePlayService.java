package org.slos.permission;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActivePlayService {
    public static AtomicBoolean playIsActive = new AtomicBoolean(true);
    private static Map<String, Boolean> MANUAL_OVERRIDE = new HashMap<>();

    public Boolean getIsActive(String player) {
        if (MANUAL_OVERRIDE.containsKey(player)) {
            return MANUAL_OVERRIDE.get(player);
        }

        return playIsActive.get();
    }

    public static Map<String, Boolean> getManualOverride() {
        return MANUAL_OVERRIDE;
    }

    public void setIsActive(Boolean isActive) {
        System.out.println("Setting active play: " + isActive);
        playIsActive.set(isActive);
    }

    public void setManualOverride(String user, Boolean manualOverride) {
        MANUAL_OVERRIDE.put(user, manualOverride);
    }

    public void setManualOverride(Map<String, Boolean> manualOverride) {
        MANUAL_OVERRIDE = manualOverride;
    }
}
