package io.bacta.login.server.session;

import io.bacta.login.server.LoginServerProperties;
import io.bacta.login.server.data.BactaAccount;
import io.bacta.login.server.repository.BactaAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public final class DefaultSessionService implements SessionService {
    //TODO: Temporary until implement real key generation.
    private static final Random random = new Random();
    /**
     * Time in milliseconds that sessions are cleaned up.
     */
    private static final long SESSION_CLEANUP_INTERVAL = 1000 * 60 * 10; //10 minutes.

    private final LoginServerProperties loginServerProperties;
    private final BactaAccountRepository accountRepository;
    private final Map<String, Session> sessions;

    @Inject
    public DefaultSessionService(LoginServerProperties loginServerProperties, BactaAccountRepository accountRepository) {
        this.loginServerProperties = loginServerProperties;
        this.accountRepository = accountRepository;
        this.sessions = new HashMap<>();
    }

    @Override
    public Session establish(String username, String password) throws SessionException {
        final BactaAccount account = accountRepository.findByUsername(username);

        if (account == null) {
            //Throw an exception
            throw new SessionException(
                    String.format("Could not establish session because username %s was not found.", username));
        }

        final String key = generateSessionKey();
        final Session session = new Session(key, account.getId());

        sessions.put(key, session);

        LOGGER.debug("Established session with key {} for username {}.", key, username);

        return session;
    }

    @Override
    public Session validate(String sessionKey) {
        final Session session = sessions.get(sessionKey);

        if (session == null)
            return null;

        if (session.isExpired()) {
            sessions.remove(sessionKey);
            return null;
        }

        return session;
    }

    private String generateSessionKey() {
        final String key = "Secret" + random.ints();
        return key;
    }

    /**
     * This method will periodically cleanup outdated sessions that have expired. We don't need to run it very often
     * as when a session tries to get validated, it will get removed if it is expired. This is just a memory cleanup
     * mechanism in case the sessions never get validated.
     */
    @Scheduled(fixedDelay = SESSION_CLEANUP_INTERVAL)
    private void cleanupSessions() {
        LOGGER.debug("Cleaning up sessions.");

        final long currentMilliseconds = System.currentTimeMillis();

        for (final Session session : sessions.values()) {
            if (session.isExpired()) {
                sessions.remove(session.getKey());

                LOGGER.trace("Removed session with key {}.", session.getKey());
            }
        }
    }
}
