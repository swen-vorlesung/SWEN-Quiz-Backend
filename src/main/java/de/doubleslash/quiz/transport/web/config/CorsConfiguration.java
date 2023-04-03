package de.doubleslash.quiz.transport.web.config;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@Slf4j
public class CorsConfiguration {

  @Value("${cors.client.url}")
  private String allowedOrigin;
  @Bean
  public CorsFilter corsFilter() {
    var origins = allowedOrigin.split(",");
    log.info("Allowed Origin:");

    for(var origin : origins)
      log.info("ORIGIN: " + origin);

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(Arrays.asList(origins));
    config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "responseType", "Authorization"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}
