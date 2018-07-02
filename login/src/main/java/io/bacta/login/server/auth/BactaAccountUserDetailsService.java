package io.bacta.login.server.auth;

import com.google.common.collect.Lists;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public final class BactaAccountUserDetailsService implements UserDetailsService {
    //@Autowired
    //private BactaAccountRepository bactaAccountRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return new User(
                "john",
                "123",
                Lists.newArrayList());
    }
}
