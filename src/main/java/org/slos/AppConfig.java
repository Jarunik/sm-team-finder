package org.slos;

import org.slos.permission.configuration.TimeDelayConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="defaults")
public class AppConfig {
    private List<String> rankedYieldList;
    private List<String> tournamentYieldList;
    private Map<Integer, Integer> recentCardWins;
    private List<String> safetyCheck;
    private List<String> useMaxTimeAgainst;
    private TimeDelayConfiguration timeDelayConfiguration;
    private List<String> alwaysAllowPermission;
    private Map<Integer, List<Integer>> cardReplacementOptions;

    public Map<Integer, List<Integer>> getCardReplacementOptions() {
        return cardReplacementOptions;
    }

    public void setCardReplacementOptions(Map<Integer, List<Integer>> cardReplacementOptions) {
        this.cardReplacementOptions = cardReplacementOptions;
    }

    public TimeDelayConfiguration getTimeDelayConfiguration() {
        return timeDelayConfiguration;
    }

    public List<String> getAlwaysAllowPermission() {
        return alwaysAllowPermission;
    }

    public void setAlwaysAllowPermission(List<String> alwaysAllowPermission) {
        this.alwaysAllowPermission = alwaysAllowPermission;
    }

    public void setTimeDelayConfiguration(TimeDelayConfiguration timeDelayConfiguration) {
        this.timeDelayConfiguration = timeDelayConfiguration;
    }

    public List<String> getUseMaxTimeAgainst() {
        return useMaxTimeAgainst;
    }

    public void setUseMaxTimeAgainst(List<String> useMaxTimeAgainst) {
        this.useMaxTimeAgainst = useMaxTimeAgainst;
    }

    public List<String> getSafetyCheck() {
        return safetyCheck;
    }

    public void setSafetyCheck(List<String> safetyCheck) {
        this.safetyCheck = safetyCheck;
    }

    public List<String> getRankedYieldList() {
        return rankedYieldList;
    }

    public void setRankedYieldList(List<String> rankedYieldList) {
        this.rankedYieldList = rankedYieldList;
    }

    public List<String> getTournamentYieldList() {
        return tournamentYieldList;
    }

    public void setTournamentYieldList(List<String> tournamentYieldList) {
        this.tournamentYieldList = tournamentYieldList;
    }

    public Map<Integer, Integer> getRecentCardWins() {
        return recentCardWins;
    }

    public void setRecentCardWins(Map<Integer, Integer> recentCardWins) {
        this.recentCardWins = recentCardWins;
    }
}
