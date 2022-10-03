package de.doubleslash.quiz.engine.controller;

import de.doubleslash.quiz.engine.dto.LogIn;
import de.doubleslash.quiz.engine.repository.UserRepository;
import de.doubleslash.quiz.engine.security.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final UserAuthenticationService authentication;

  private final UserRepository userRepository;

  @PostMapping("/login")
  String login(@RequestBody LogIn body) {

    return authentication
        .login(body.getUsername(), body.getPassword())
        .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
  }

  @PostMapping("/register")
  String register(@RequestBody LogIn body) {
    return authentication.register(body.getUsername(), body.getPassword())
        .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
  }
}
