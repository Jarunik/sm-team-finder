package org.slos.permission;

import org.slos.permission.configuration.ConfigurationUpdateService;

public interface PermissionService extends ConfigurationUpdateService {
    PermissionResponse hasPermission(PermissionRequest permissionRequest);

}
