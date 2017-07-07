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

package io.bacta.session.client;

import io.bacta.session.message.EstablishSessionMessage;
import io.bacta.session.message.EstablishSessionResponseMessage;
import io.bacta.session.message.ValidateSessionMessage;
import io.bacta.session.message.ValidateSessionResponseMessage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A client that communicates directly with a Session Server.
 * <p>
 * Sessions are established by first executing the {@link #establish(String, String)} method. The token from that flow can then be passed around to
 * identify with other servers. The servers will use the {@link #validate(String)} method to ensure that a token is valid.
 */
@Scope("prototype")
@Component
public final class SessionClient {
    private static final AtomicInteger nextRequestId = new AtomicInteger();

    private final SessionClientProperties properties;

    @Inject
    public SessionClient(final SessionClientProperties properties) {
        this.properties = properties;
    }

    /**
     * Validate an existing session key.
     *
     * @param key The key being validated.
     */
    public void validate(final String key) {
        final int requestId = nextRequestId.incrementAndGet();
        final ValidateSessionMessage message = new ValidateSessionMessage(requestId, key);

        //We need a connection to the session server.
        //sessionServerConnection.sendMessage(message);
    }

    /**
     * Establish a connection with a username and password, obtaining a session key from the session server.
     *
     * @param username The username.
     * @param password The password.
     */
    public void establish(final String username, final String password) {
        final int requestId = nextRequestId.incrementAndGet();
        final EstablishSessionMessage message = new EstablishSessionMessage(requestId, username, password);

        //We need a connection to the session server.
        //sessionServerConnection.sendMessage(message);
    }

    /**
     * Handles a response to a validate session response.
     *
     * @param responseMessage The response message.
     */
    private void validationReceived(final ValidateSessionResponseMessage responseMessage) {
        final int requestId = responseMessage.getRequestId();
    }

    /**
     * Handles a response to an establish session response.
     *
     * @param responseMessage The response message.
     */
    private void loginReceived(final EstablishSessionResponseMessage responseMessage) {
        final int requestId = responseMessage.getRequestId();
    }
}