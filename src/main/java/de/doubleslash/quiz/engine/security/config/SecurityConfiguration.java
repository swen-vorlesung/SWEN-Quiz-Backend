package de.doubleslash.quiz.engine.security.config;

import static java.util.Objects.requireNonNull;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import de.doubleslash.quiz.engine.security.TokenAuthenticationFilter;
import de.doubleslash.quiz.engine.security.TokenAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
      new AntPathRequestMatcher("/auth/**"),
      new AntPathRequestMatcher("**/participants/**")
  );

  private static final RequestMatcher PROTECTED_URLS = new NegatedRequestMatcher(PUBLIC_URLS);

  private final TokenAuthenticationProvider provider;

  public SecurityConfiguration(final TokenAuthenticationProvider provider) {
    super();
    this.provider = requireNonNull(provider);
  }

  @Override
  public void configure(final WebSecurity web) {
    web.ignoring().requestMatchers(PUBLIC_URLS);
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {

    http
        .sessionManagement()
        .sessionCreationPolicy(STATELESS)
        .and()
        .exceptionHandling()
        .defaultAuthenticationEntryPointFor(forbiddenEntryPoint(), PROTECTED_URLS)
        .and()
        .authenticationProvider(provider)
        .addFilterBefore(restAuthenticationFilter(), AnonymousAuthenticationFilter.class)
        .authorizeRequests()
        .requestMatchers(PROTECTED_URLS)
        .authenticated()
        .and()
        .csrf().disable()
        .formLogin().disable()
        .httpBasic().disable()
        .logout().disable();
  }

  @Bean
  public AuthenticationEntryPoint forbiddenEntryPoint() {
    return new HttpStatusEntryPoint(HttpStatus.FORBIDDEN);
  }

  @Bean
  public SimpleUrlAuthenticationSuccessHandler successHandler() {

    final var successHandler = new SimpleUrlAuthenticationSuccessHandler();
    successHandler.setRedirectStrategy(new NoRedirectStrategy());
    return successHandler;
  }

  @Bean
  public TokenAuthenticationFilter restAuthenticationFilter() throws Exception {

    final var filter = new TokenAuthenticationFilter(PROTECTED_URLS);
    filter.setAuthenticationManager(authenticationManager());
    filter.setAuthenticationSuccessHandler(successHandler());
    return filter;
  }
}