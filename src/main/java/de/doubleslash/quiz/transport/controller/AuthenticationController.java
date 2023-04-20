package de.doubleslash.quiz.transport.controller;

import de.doubleslash.quiz.repository.UserRepository;
import de.doubleslash.quiz.transport.dto.LogIn;
import de.doubleslash.quiz.transport.dto.TokenResponse;
import de.doubleslash.quiz.transport.security.UserAuthenticationService;
import java.time.Duration;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

  private final UserAuthenticationService authentication;

  private final UserRepository userRepository;

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LogIn body, HttpServletResponse response) {

    String token = authentication
        .login(body.getUsername(), body.getPassword())
        .orElseThrow(() -> new RuntimeException("invalid login and/or password"));

    ResponseCookie cookie = ResponseCookie.from("session_token", token)
        .httpOnly(false)
        .secure(true)
        .sameSite("None")
        .maxAge(Duration.ofDays(1))
        .path("/")
        .build();

    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @PostMapping("/register")
  public TokenResponse register(@RequestBody LogIn body) {
    return authentication
        .register(body.getUsername(), body.getPassword())
        .map(TokenResponse::new)
        .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
  }
}
