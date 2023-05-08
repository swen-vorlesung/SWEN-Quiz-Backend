package de.doubleslash.quiz.transport.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Slf4j
public final class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  final String AUTH_TOKEN = "session_token";

  public TokenAuthenticationFilter(final RequestMatcher requiresAuth) {
    super(requiresAuth);
  }

  /**
   * Called when a secured resource is requested.
   */
  @Override
  public Authentication attemptAuthentication(final HttpServletRequest request,
      final HttpServletResponse response) {

    Cookie[] cookies = request.getCookies();

    if (cookies == null) {
      throw new BadCredentialsException("No Cookie Found!");
    }

    final Optional<Cookie> sessionCookie = Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().equals(AUTH_TOKEN))
        .findFirst();

    if (sessionCookie.isEmpty()) {
      throw new BadCredentialsException("No Token Found!");
    }

    final String token = sessionCookie.get().getValue();

    final Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
    log.info("^^^^^^^^ auth: " + auth);
    return getAuthenticationManager()
        .authenticate(auth);
  }

  @Override
  protected void successfulAuthentication(final HttpServletRequest request,
      final HttpServletResponse response, final FilterChain chain,
      final Authentication authResult) throws IOException, ServletException {

    super.successfulAuthentication(request, response, chain, authResult);
    chain.doFilter(request, response);
  }
}
