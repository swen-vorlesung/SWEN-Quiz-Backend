package de.doubleslash.quiz.transport.security;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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

  public TokenAuthenticationFilter(final RequestMatcher requiresAuth) {
    super(requiresAuth);
  }

  /**
   * Called when a secured resource is requested.
   */
  @Override
  public Authentication attemptAuthentication(final HttpServletRequest request,
      final HttpServletResponse response) {

    final String param = ofNullable(request.getHeader(AUTHORIZATION))
        .orElse(request.getParameter("t"));

    final String token = ofNullable(param)
        .map(value -> removeStart(value, "Bearer"))
        .map(String::trim)
        .orElseThrow(() -> new BadCredentialsException("No Token Found!"));

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
