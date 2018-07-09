package io.bacta.login.server.auth;

import com.google.common.collect.Lists;
import io.bacta.login.server.model.Account;
import io.bacta.login.server.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class BactaAccountUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        LOGGER.trace("Looking up user with username {}.", username);

        final Account account = accountRepository.findByUsername(username);

        if (account == null) {
            LOGGER.trace("Did not find user with username {}.", username);
            throw new UsernameNotFoundException(String.format("Username '%s' not found.", username));
        }

        LOGGER.trace("Found user with username {}.", username);

        return new User(
                account.getUsername(),
                account.getEncodedPassword(),
                Lists.newArrayList());
    }
}
