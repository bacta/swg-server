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

package io.bacta.login.server;

/**
 * Defines the method of validating a session on the LoginServer.
 */
public enum LoginSessionMode {
    /**
     * The LoginServer is expecting to be given a session id in the {@link io.bacta.login.message.LoginClientId} message.
     * If validation of the value of {@link io.bacta.login.message.LoginClientId#key} fails on the SessionServer, then
     * the client is refused.
     */
    VALIDATE,
    /**
     * The LoginServer is expecting a username and password in the {@link io.bacta.login.message.LoginClientId} message.
     * The {@link io.bacta.login.message.LoginClientId#id} is the username, and the
     * {@link io.bacta.login.message.LoginClientId#key} is the password. If the SessionServer fails to establish a
     * session with these credentials, then the client is refused.
     */
    ESTABLISH,
    /**
     * The LoginServer will try to discover the means of validating or establishing a session by first attempting to
     * validate the session. If validation fails, it will then attempt to establish a session using the credentials in the
     * {@link io.bacta.login.message.LoginClientId} message. If this also fails, then the client will be rejected.
     */
    DISCOVER;
}
