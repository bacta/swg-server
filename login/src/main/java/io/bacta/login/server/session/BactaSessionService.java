package io.bacta.login.server.session;

import io.bacta.engine.security.CryptoUtil;
import io.bacta.login.server.data.BactaAccount;
import io.bacta.login.server.repository.BactaAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.inject.Inject;
import java.util.Base64;

@Slf4j
@Service
public final class BactaSessionService implements SessionService {
    private static final KeyGenerator KEY_GENERATOR = CryptoUtil.getKeyGenerator("AES");
    private static final Base64.Encoder BASE_64_ENCODER = Base64.getEncoder();
    private static final long CLEANUP_INTERVAL = 1000 * 10; //every 10 seconds.

    private final SessionServerProperties sessionServerProperties;
    private final BactaAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionMap sessionMap;

    @Inject
    public BactaSessionService(SessionServerProperties sessionServerProperties,
                               BactaAccountRepository accountRepository,
                               PasswordEncoder passwordEncoder) {
        this.sessionServerProperties = sessionServerProperties;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;

        this.sessionMap = new SessionMap();
    }

    @Override
    public Session establish(String username, String password) throws SessionCredentialsInvalidException {
        BactaAccount account = accountRepository.findByUsername(username);

        if (account == null) {
            //If automatic account creation is enabled, then create a new account for this user.
            if (sessionServerProperties.isAutoAccountCreationEnabled()) {
                LOGGER.debug("Creating an account for {} because automatic account creation is enabled.", username);
                final String encodedPassword = passwordEncoder.encode(password);

                account = new BactaAccount(username, encodedPassword);
                account = accountRepository.save(account);
            } else {
                LOGGER.debug("Invalid login attempt by username {}. Account does not exist.", username);
                throw new SessionCredentialsInvalidException();
            }
        }

        if (!passwordEncoder.matches(password, account.getEncodedPassword())) {
            LOGGER.debug("Invalid login attempt by username {}. Wrong password.", username);
            //TODO: Do we want to limit number of failed attempts in x time?
            throw new SessionCredentialsInvalidException();
        }

        //Close any active sessions for this account if multiple sessions per account is disabled.
        if (!sessionServerProperties.isMultipleSessionsPerAccountAllowed()) {
            //Rather than iterating over every entry and calling logout, we are just going to remove them.
            sessionMap.removeByAccountId(account.getId());
        }

        //Establish a new session and add it to the map.
        final String sessionKey = generateSessionKey();
        final Session session = new Session(sessionKey, account.getId());

        sessionMap.add(session);

        LOGGER.info("Established new session for username {}.", username);

        return session;
    }

    @Override
    public Session validate(String sessionKey, int accountId) throws SessionInvalidException, SessionExpiredException {
        //Validating a session is a lot like touching a session, except that we also want to ensure that it matches
        //the provided account id.

        final Session session = sessionMap.getBySessionId(sessionKey);

        if (session == null || session.isExpired())
            throw new SessionExpiredException();

        //Check if the accounts match.
        if (session.getAccountId() != accountId)
            throw new SessionInvalidException();

        session.touch();

        return session;
    }

    @Override
    public void touch(String sessionKey) throws SessionExpiredException {
        final Session session = sessionMap.getBySessionId(sessionKey);

        if (session == null || session.isExpired())
            throw new SessionExpiredException();

        session.touch();
    }

    @Override
    public void logout(String sessionKey) {
        sessionMap.removeBySessionId(sessionKey);
    }

    private String generateSessionKey() {
        final SecretKey secretKey = KEY_GENERATOR.generateKey();
        return BASE_64_ENCODER.encodeToString(secretKey.getEncoded());
    }

    @Scheduled(fixedRate = CLEANUP_INTERVAL)
    private void cleanup() {
        if (sessionMap.size() > 0) {
            LOGGER.debug("Cleaning up sessions.");

            for (final Session session : sessionMap) {
                if (session.isExpired())
                    sessionMap.remove(session);
            }
        }
    }
}