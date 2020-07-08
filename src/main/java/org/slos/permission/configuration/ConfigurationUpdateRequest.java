package org.slos.permission.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slos.util.ToJson;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigurationUpdateRequest implements ToJson {
    private Boolean activePlay;
    private Integer safetyLevel;
    private Float safetyRatio;
    private Integer defaultPlayDelay;
    private Map<String, String> baseOverrides;
    private Map<String, Map<String, String>> userOverrides;

    private static final Map emptySetting = Collections.emptyMap();

    @JsonCreator
    public ConfigurationUpdateRequest(@JsonProperty("activePlay") Boolean activePlay, @JsonProperty("safetyLevel") Integer safetyLevel, @JsonProperty("safetyRatio") Float safetyRatio, @JsonProperty("defaultPlayDelay") Integer defaultPlayDelay, @JsonProperty("baseOverrides") Map<String, String> baseOverrides, @JsonProperty("userOverrides") Map<String, Map<String, String>> userOverrides) {
        this.activePlay = activePlay;
        this.safetyLevel = safetyLevel;
        this.safetyRatio = safetyRatio;
        this.defaultPlayDelay = defaultPlayDelay;
        this.baseOverrides = baseOverrides;
        this.userOverrides = userOverrides;

        if (baseOverrides == null) {
            this.baseOverrides = emptySetting;
        }

        if (userOverrides == null) {
            this.userOverrides = emptySetting;
        }
    }
    public ConfigurationUpdateRequest() {}

    public Integer getDefaultPlayDelay() {
        return defaultPlayDelay;
    }

    public void setDefaultPlayDelay(Integer defaultPlayDelay) {
        this.defaultPlayDelay = defaultPlayDelay;
    }

    public Boolean getActivePlay() {
        return activePlay;
    }

    public void setActivePlay(Boolean activePlay) {
        this.activePlay = activePlay;
    }

    public Integer getSafetyLevel() {
        return safetyLevel;
    }

    public void setSafetyLevel(Integer safetyLevel) {
        this.safetyLevel = safetyLevel;
    }

    public Float getSafetyRatio() {
        return safetyRatio;
    }

    public void setSafetyRatio(Float safetyRatio) {
        this.safetyRatio = safetyRatio;
    }

    public Map<String, String> getBaseOverrides() {
        return baseOverrides;
    }

    public void setBaseOverrides(Map<String, String> baseOverrides) {
        this.baseOverrides = baseOverrides;
    }

    public Map<String, Map<String, String>> getUserOverrides() {
        return userOverrides;
    }

    public void setUserOverrides(Map<String, Map<String, String>> userOverrides) {
        this.userOverrides = userOverrides;
    }

    @Override
    public String toString() {
        return toJson();
    }
}
