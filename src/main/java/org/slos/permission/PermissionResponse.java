package org.slos.permission;

import org.slos.util.ToJson;

import java.util.ArrayList;
import java.util.List;

public class PermissionResponse implements ToJson {
    private Boolean permissionGranted;
    private List<String> messages;

    public PermissionResponse() {
        messages = new ArrayList<>();
    }
    public PermissionResponse(Boolean permissionGranted, List<String> messages) {
        this.permissionGranted = permissionGranted;
        this.messages = messages;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public Boolean getPermissionGranted() {
        return permissionGranted;
    }

    public void setPermissionGranted(Boolean permissionGranted) {
        this.permissionGranted = permissionGranted;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> message) {
        this.messages = message;
    }

    @Override
    public String toString() {
        return toJson();
    }
}
