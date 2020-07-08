package org.slos.permission;

import org.slos.AppConfig;
import org.slos.permission.configuration.ConfigurationUpdateRequest;
import org.slos.permission.configuration.ConfigurationUpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SafeToPlayLookupService implements PermissionService {
    @Autowired AppConfig appConfig;
    private Integer safetyTime;
    private Boolean cachedResponse = true;
    private Integer lastNemesisPlayCount = -1;
    private Integer lastPlayCount = -1;
    private AtomicLong LAST_CHECK = new AtomicLong(-1l);
    private static AtomicInteger SAFETY_LEVEL;
    private Float safetyRatio;

    public SafeToPlayLookupService(Integer safetyLevel, Integer safetyTime) {
        System.out.println("Safety Level set to: " + safetyLevel);
        System.out.println("Safety Time set to: " + safetyTime);
        SAFETY_LEVEL = new AtomicInteger(safetyLevel);
        this.safetyTime = safetyTime;
    }

    @Override
    public PermissionResponse hasPermission(PermissionRequest permissionRequest) {
        Boolean safetyPermission = checkSafetyLevel(permissionRequest);

        List<String> messages = new ArrayList<>();
        messages.add("Safety level [current/limit]: " + lastNemesisPlayCount + "/" + SAFETY_LEVEL);
        messages.add("Game count: " + lastPlayCount);
        messages.add("Safety ratio: " + safetyRatio);

        return new PermissionResponse(safetyPermission, messages);
    }

    public ConfigurationUpdateResponse updateService(ConfigurationUpdateRequest configurationUpdateRequest) {
        Integer newSafetyLevel = configurationUpdateRequest.getSafetyLevel();

        SAFETY_LEVEL.set(newSafetyLevel);
        LAST_CHECK.set(-1l);
        safetyRatio = configurationUpdateRequest.getSafetyRatio();

        System.out.println("Setting safety level: " + newSafetyLevel);
        System.out.println("Setting safety ratio: " + safetyRatio);
        List<String> messages = new ArrayList<>();
        messages.add("Safety Level set to: " + newSafetyLevel);
        messages.add("Safety ratio set to: " + safetyRatio);

        return new ConfigurationUpdateResponse(messages);
    }

    private Boolean checkSafetyLevel(PermissionRequest permissionRequest) {
        return true;
    }

    private Boolean function(PermissionRequest permissionRequest) {
        System.out.println("Checking safety level.  Current cache: " + cachedResponse + " - Last updated ago: " + (System.currentTimeMillis() - LAST_CHECK.get() / 1000));
        if (appConfig.getSafetyCheck().contains(permissionRequest.getPlayer())) {
            Long now = System.currentTimeMillis();
            if ((LAST_CHECK.get() > 0) && ((now - LAST_CHECK.get()) < 60000)) {
                System.out.println("Returning cached safe to play: " + cachedResponse);
                return cachedResponse;
            }

            String connectionUrl = "<add your connection url>";
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (Exception e) {
            }

            Integer recentSafetyLevel = nemesisGameCount(connectionUrl);
            Integer totalTopGames = totalGameCount(connectionUrl);
            System.out.println("Total game count: " + totalTopGames);
            System.out.println("Nemesis count: " + recentSafetyLevel);

            safetyRatio = ((float) lastNemesisPlayCount) / ((float) lastPlayCount) * 1000;

            if (recentSafetyLevel <= SAFETY_LEVEL.get()) {
                return updateAndReturnCache(true);
            }

            boolean response = updateAndReturnCache(false);

            return response && (safetyRatio < 10);
        }
        return true;
    }

    private Integer totalGameCount(String connectionUrl) {
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement()) {
            String SQL = "";

            SQL = "select count(*) as gameCount from sm_battle " +
                    "where created_date >=  DATEADD(MINUTE,-" + safetyTime + ",GETDATE()) " +
                    " and match_type = 'Ranked';";
            ResultSet rs = stmt.executeQuery(SQL);
            rs.next();

            Integer recentPlayCount = rs.getInt("gameCount");
            lastPlayCount = recentPlayCount;
            System.out.println("Refreshing play count: " + recentPlayCount);

            return lastPlayCount;

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return -1;
    }

    private Integer nemesisGameCount(String connectionUrl) {
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement()) {
            String SQL = "";

            System.out.println("Safety time: " + safetyTime);

            SQL = "select count(*) as gameCount from sm_battle " +
                    "where created_date >=  DATEADD(MINUTE,-" + safetyTime + ",GETDATE()) " +
                    "  and ((player1_name in " +
//                    "('begonethot', 'belo4ka', 'marjanko', 'vaansteam', 'schneegecko', 'twinner', 'ts-neoxian', 'swedishdragon', 'steelman', 'coolbowser', 'faiyazmahmud', 'bji1203', 'jacekw', 'smk2000', 'hossainbd', 'goldfashioned', 'caraxes', 'donekim', 'toocurious', 'bigjoy', 'mellofello', 'glory7', 'giker', 'aicu', 'steallion', 'tendershepard', 'bubke', 'imperfect-one', 'themightyvolcano', 'hossaindb', 'monster-grinder', 'tailcock', 'swedishdragon', 'fenrir78', 'toocurious', 'akomoajong') " +
                    "('jacekw') " +
                    "  or (player2_name in " +
//                    "('begonethot', 'belo4ka', 'marjanko', 'vaansteam', 'schneegecko', 'twinner', 'ts-neoxian', 'swedishdragon', 'steelman', 'coolbowser', 'faiyazmahmud', 'bji1203', 'jacekw', 'smk2000', 'hossainbd', 'goldfashioned', 'caraxes', 'donekim', 'toocurious', 'bigjoy', 'mellofello', 'glory7', 'giker', 'aicu', 'steallion', 'tendershepard', 'bubke', 'imperfect-one', 'themightyvolcano', 'hossaindb', 'monster-grinder', 'tailcock', 'swedishdragon', 'fenrir78', 'toocurious', 'akomoajong')))) " +
                    "('jacekw')))) " +
                    " and match_type = 'Ranked';";
            ResultSet rs = stmt.executeQuery(SQL);
            rs.next();

            System.out.println("SQL: " + SQL);

            Integer recentSafetyLevel = rs.getInt("gameCount");
            lastNemesisPlayCount = recentSafetyLevel;
            System.out.println("Refreshing safety level: " + recentSafetyLevel);

            return lastNemesisPlayCount;

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return -1;
    }

    private Boolean updateAndReturnCache(Boolean response) {
        LAST_CHECK.set(System.currentTimeMillis());
        cachedResponse = response;
        return response;
    }
}
