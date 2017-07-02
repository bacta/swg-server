package com.ocdsoft.bacta.soe.network.dispatch;

import com.ocdsoft.bacta.soe.network.connection.ConnectionRole;
import lombok.Getter;

import java.util.List;

/**
 * Created by kyle on 4/22/2016.
 */
@Getter
public class ControllerData<T> {
    private final T controller;
    private final ConnectionRole[] roles;

    public ControllerData(final T controller,
                          final ConnectionRole[] roles) {
        this.controller = controller;
        this.roles = roles;
    }

    public boolean containsRoles(final List<ConnectionRole> userRoles) {

        for (final ConnectionRole role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }

        return roles.length == 0;
    }
}
