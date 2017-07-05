/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package bacta.io.soe.network.dispatch;

import bacta.io.soe.network.connection.ConnectionRole;
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
