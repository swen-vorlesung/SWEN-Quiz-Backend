package de.doubleslash.quiz.transport.security;

import com.google.common.collect.Lists;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  private final UserAuthenticationService auth;

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    // NOOP
  }

  @Override
  protected UserDetails retrieveUser(final String username, final UsernamePasswordAuthenticationToken authentication) {

    final var token = authentication.getCredentials();

    var user = Optional.ofNullable(token)
        .map(String::valueOf)
        .flatMap(auth::findByToken)
        .orElseThrow(() -> new UsernameNotFoundException("Couldn't find user: " + token));

    return new User(user.getName(),
        user.getPassword(),
        Lists.newArrayList(new SimpleGrantedAuthority("USER")));
  }
}
