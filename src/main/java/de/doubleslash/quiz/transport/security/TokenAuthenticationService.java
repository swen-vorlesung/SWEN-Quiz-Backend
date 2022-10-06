package de.doubleslash.quiz.transport.security;

import de.doubleslash.quiz.repository.UserRepository;
import de.doubleslash.quiz.repository.dao.auth.User;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
final class TokenAuthenticationService implements UserAuthenticationService {

  private static final String SUCCESS = "SUCCESS";

  private static final String PROC_LOGIN = "uspLogin";

  private static final String PROC_REGISTER = "uspAddUser";

  private final TokenService tokenService;

  private final UserRepository userRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Optional<String> login(final String username, final String password) {

    var result = (String) entityManager.createStoredProcedureQuery(PROC_LOGIN)
        .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
        .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
        .registerStoredProcedureParameter(3, String.class, ParameterMode.OUT)
        .setParameter(1, username)
        .setParameter(2, password)
        .getOutputParameterValue(3);

    if (SUCCESS.equals(result)) {
      return Optional.of(tokenService.newUserToken(username));
    }

    return Optional.empty();
  }

  @Override
  public Optional<String> register(String username, String password) {

    var result = (String) entityManager.createStoredProcedureQuery(PROC_REGISTER)
        .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
        .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
        .registerStoredProcedureParameter(3, String.class, ParameterMode.OUT)
        .setParameter(1, username)
        .setParameter(2, password)
        .getOutputParameterValue(3);

    if(SUCCESS.equals(result)) {
      return Optional.of(tokenService.newUserToken(username));
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
