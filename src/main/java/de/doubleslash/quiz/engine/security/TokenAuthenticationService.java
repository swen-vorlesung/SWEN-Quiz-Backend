package de.doubleslash.quiz.engine.security;

import de.doubleslash.quiz.engine.repository.UserRepository;
import de.doubleslash.quiz.engine.repository.dao.auth.Authorities;
import de.doubleslash.quiz.engine.repository.dao.auth.User;
import de.doubleslash.quiz.engine.security.util.PasswordEncoder;
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

  private final UserRepository userRepository;

  private final PasswordEncoder pwEncoder;

  @Override
  public Optional<String> login(final String username, final String password) {

    var encodedPassword = pwEncoder.encode(password);

    return encodedPassword
        .flatMap(s -> userRepository
            .findByName(username)
            .filter(user -> Objects.equals(user.getPassword(), s))
            .map(user -> tokenService.newUserToken(username)));
  }

  @Override
  public Optional<String> register(String username, String password) {

    var encodedPassword = pwEncoder.encode(password);

    if (encodedPassword.isPresent()) {
      userRepository.save(User.builder()
          .name(username)
          .password(encodedPassword.get())
          .enabled(true)
          .authorities(Authorities.builder().authority("USER").build())
          .build());

      return userRepository
          .findByName(username)
          .filter(user -> Objects.equals(user.getPassword(), encodedPassword.get()))
          .map(user -> tokenService.newUserToken(username));
    }

    return Optional.empty();
  }

  @Override
  public Optional<User> findByToken(final String token) {

    log.info("$$$$$$$$$$$$$$$$$$$$ token: " + token);
    return Optional
        .of(tokenService.verify(token))
        .map(map -> map.get("username"))
        .flatMap(userRepository::findByName);
  }

  @Override
  public void logout(final User user) {
  }
}
