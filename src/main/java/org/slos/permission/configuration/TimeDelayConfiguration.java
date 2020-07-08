package org.slos.permission.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix="time-delay-configuration")
public class TimeDelayConfiguration {
    private Integer defaultDelay;
    private Map<String, Integer> delays;

    public TimeDelayConfiguration(){}
    public TimeDelayConfiguration(Integer defaultDelay, Map<String, Integer> delays) {
        this.defaultDelay = defaultDelay;
        this.delays = delays;
    }

    public void setDelayFor(String player, Integer delayPeriod) {
        delays.put(player, delayPeriod);
    }

    public Integer getDelayFor(String player) {
        return delays.get(player);
    }

    public Integer getDefaultDelay() {
        return defaultDelay;
    }

    public void setDefaultDelay(Integer defaultDelay) {
        this.defaultDelay = defaultDelay;
    }

    public Map<String, Integer> getDelays() {
        return delays;
    }

    public void setDelays(Map<String, Integer> delays) {
        this.delays = delays;
    }
}
