package io.bacta.login.server.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Collections;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    private final KeyPair keyPair;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, KeyPair keyPair) {
        super(authenticationManager);

        this.keyPair = keyPair;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        final String header = request.getHeader(HEADER_STRING);

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            final UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(HEADER_STRING);

        if (token != null) {
//            final String user = Jwts.parser()
//                    .setSigningKey(keyPair.getPublic())
//                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
//                    .getBody()
//                    .getSubject();
final String user = "crush";


            if (user != null)
                return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        }

        return null;
    }
}
