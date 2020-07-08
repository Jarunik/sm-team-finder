package org.slos.permission.configuration;

import org.slos.util.ToJson;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationUpdateResponse implements ToJson {
    private List<String> message = new ArrayList<>();

    public ConfigurationUpdateResponse(String message) {
        this.message.add(message);
    }
    public ConfigurationUpdateResponse(List<String> message) {
        this.message = message;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return toJson();
    }
}
