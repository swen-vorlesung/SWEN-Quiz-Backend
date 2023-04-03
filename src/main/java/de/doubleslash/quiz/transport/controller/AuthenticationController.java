package de.doubleslash.quiz.transport.controller;

import de.doubleslash.quiz.repository.UserRepository;
import de.doubleslash.quiz.transport.dto.LogIn;
import de.doubleslash.quiz.transport.dto.TokenResponse;
import de.doubleslash.quiz.transport.security.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  public TokenResponse login(@RequestBody LogIn body) {

    return authentication
        .login(body.getUsername(), body.getPassword())
        .map(TokenResponse::new)
        .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
  }

  @PostMapping("/register")
  public TokenResponse register(@RequestBody LogIn body) {
    return authentication
        .register(body.getUsername(), body.getPassword())
        .map(TokenResponse::new)
        .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
  }
}
