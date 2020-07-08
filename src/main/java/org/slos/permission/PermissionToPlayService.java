package org.slos.permission;

import org.slos.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class PermissionToPlayService {
    @Autowired SafeToPlayLookupService safeToPlayLookupService;
    @Autowired ActivePlayPermissionService activePlayPermissionService;
    @Autowired MaxRatingService maxRatingService;
    @Autowired MaxRankPermissionService maxRankPermissionService;
    @Autowired TimeDelayPermission timeDelayPermission;
    @Autowired AppConfig appConfig;

    Logger logger = LoggerFactory.getLogger(PermissionToPlayService.class);

    public PermissionResponse permissionToPlay(PermissionRequest permissionRequest) {

        System.out.println("User seeking permission to play: " + permissionRequest.getPlayer() + " - " + permissionRequest);
        Boolean permissionGranted = null;
        List<String> messages = new ArrayList<>();
        try {

            PermissionResponse activePlayPermission = activePlayPermissionService.hasPermission(permissionRequest);
            System.out.println("Active play set to: " + activePlayPermission.getPermissionGranted());

            messages.addAll(activePlayPermission.getMessages());
            if (activePlayPermission.getPermissionGranted().equals(false)) {
                permissionGranted = activePlayPermission.getPermissionGranted();
            } else {
                PermissionResponse rankPermissionGranted = maxRankPermissionService.hasPermission(permissionRequest);
                messages.addAll(rankPermissionGranted.getMessages());
                System.out.println("Rank Permission: " + rankPermissionGranted.getPermissionGranted());
                permissionGranted = rankPermissionGranted.getPermissionGranted();

                PermissionResponse maxRatingServiceGranted = maxRankPermissionService.hasPermission(permissionRequest);
                messages.addAll(maxRatingServiceGranted.getMessages());
                System.out.println("Rating Permission: " + maxRatingServiceGranted.getPermissionGranted());
                permissionGranted = permissionGranted && maxRatingServiceGranted.getPermissionGranted();

                PermissionResponse safetyPermissionGranted = safeToPlayLookupService.hasPermission(permissionRequest);
                messages.addAll(safetyPermissionGranted.getMessages());
                System.out.println("Safety Permission set to: " + safetyPermissionGranted);
                permissionGranted = permissionGranted && safetyPermissionGranted.getPermissionGranted();

                PermissionResponse timeDelayPermissionResponse = timeDelayPermission.hasPermission(permissionRequest);
                messages.addAll(timeDelayPermissionResponse.getMessages());
                System.out.println("Time delay permission: " + timeDelayPermissionResponse);
                permissionGranted = permissionGranted && timeDelayPermissionResponse.getPermissionGranted();

                if (appConfig.getAlwaysAllowPermission().contains(permissionRequest.getPlayer())) {
                    messages.add("Permission override granted! Permission was: " + permissionGranted + " but is now true.");
                    permissionGranted = true;
                }
            }

            System.out.println("Permission Granted[" + permissionRequest.getPlayer() + "]: " + permissionGranted);
        } catch (Throwable t) {
            String message = "Failed in figuring permission: " + t.getMessage();
            logger.error(message, t);
            messages.add(message);

            return new PermissionResponse(false, messages);
        }

        return new PermissionResponse(permissionGranted, messages);

    }
}
