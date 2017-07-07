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

import co.paralleluniverse.fibers.Suspendable;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.session.message.*;
import io.bacta.soe.network.connection.SoeUdpConnection;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Scope("prototype")
@Component
public final class SessionClient {
    private static final AtomicInteger nextRequestId = new AtomicInteger();

    private final TIntObjectMap<SessionRequestAsync> outstandingRequests;
    private final SessionClientProperties properties;

    @Inject
    public SessionClient(final SessionClientProperties properties) {
        this.properties = properties;

        this.outstandingRequests = new TIntObjectHashMap<>();
    }

    /**
     * Validate an existing session key.
     *
     * @param key The key being validated.
     */
    @Suspendable
    public Session validate(final String key) {
        try {
            final int requestId = nextRequestId.incrementAndGet();
            final SessionRequestAsync request = new SessionRequestAsync() {
                protected void requestAsync() {
                    final ValidateSessionMessage message = new ValidateSessionMessage(requestId, key);
                    //Send to SessionServer.
                }
            };

            outstandingRequests.put(requestId, request);

            return request.run();
        } catch (InterruptedException ex) {
            LOGGER.error("InterruptedException", ex);
            return null;
        }
    }

    /**
     * Establish a connection with a username and password, obtaining a session key from the session server.
     *
     * @param username The username.
     * @param password The password.
     */
    public Session establish(final String username, final String password) {
        try {
            final int requestId = nextRequestId.incrementAndGet();
            final SessionRequestAsync request = new SessionRequestAsync() {
                protected void requestAsync() {
                    final EstablishSessionMessage message = new EstablishSessionMessage(requestId, username, password);
                    //Send to SessionServer.
                }
            };

            outstandingRequests.put(requestId, request);

            return request.run();
        } catch (InterruptedException ex) {
            LOGGER.error("InterruptedException", ex);
            return null;
        }
    }

    /**
     * Called when the {@link io.bacta.session.client.controller.EstablishSessionResponseMessageController} has received
     * the response for an {@link EstablishSessionResponseMessage} from the SessionServer.
     * <p>
     * This should complete the Future that was awaiting the response.
     *
     * @param connection The connection that sent the response. Should be a connection to the session server.
     * @param response   The response message.
     */
    public void receivedEstablishSessionResponse(SoeUdpConnection connection, EstablishSessionResponseMessage response) {
        final int requestId = response.getRequestId();

        final SessionRequestAsync outstandingRequest = outstandingRequests.get(requestId);

        if (outstandingRequest == null)
            throw new SessionRequestException(String.format("Invalid request id %d", requestId));

        if (outstandingRequest.isCompleted()) {
            LOGGER.error("An outstanding session request was already completed with request id {}", requestId);
            outstandingRequest.failure(new SessionRequestException("Session request already completed."));
        } else {
            final Session session = new Session(
                    response.getResult(),
                    response.getBactaId(),
                    response.getSessionId());

            outstandingRequest.success(session);
        }

        outstandingRequests.remove(requestId);
    }

    public void receivedValidateSessionResponse(SoeUdpConnection connection, ValidateSessionResponseMessage response) {
        final int requestId = response.getRequestId();

        final SessionRequestAsync outstandingRequest = outstandingRequests.get(requestId);

        if (outstandingRequest == null)
            throw new SessionRequestException(String.format("Invalid request id %d", requestId));

        if (outstandingRequest.isCompleted()) {
            LOGGER.error("An outstanding session request was already completed with request id {}", requestId);
            outstandingRequest.failure(new SessionRequestException("Session request already completed."));
        } else {
            final Session session = new Session(response.getResult(), -1, "");

            outstandingRequest.success(session);
        }

        outstandingRequests.remove(requestId);
    }
}