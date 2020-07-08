package org.slos.permission;

public class SetActivePlayRequest {
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public static Boolean adaptToBoolean(SetActivePlayRequest setActivePlayRequest) {
        return setActivePlayRequest.getActive();
    }
}
