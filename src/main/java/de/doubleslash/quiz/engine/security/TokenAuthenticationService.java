package de.doubleslash.quiz.engine.security;

import com.google.common.collect.ImmutableMap;
import de.doubleslash.quiz.engine.repository.UserRepository;
import de.doubleslash.quiz.engine.repository.dao.auth.User;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
final class TokenAuthenticationService implements UserAuthenticationService {

  private final TokenService tokenService;

  private final UserRepository users;

  @Override
  public Optional<String> login(final String username, final String password) {
    return users
        .findByName(username)
        .filter(user -> Objects.equals(password, user.getPassword()))
        .map(user -> tokenService.newToken(ImmutableMap.of("username", username)));
  }

  @Override
  public Optional<User> findByToken(final String token) {

    log.info("$$$$$$$$$$$$$$$$$$$$ token: " + token);
    return Optional
        .of(tokenService.verify(token))
        .map(map -> map.get("username"))
        .flatMap(users::findByName);
  }

  @Override
  public void logout(final User user) {
  }
}
