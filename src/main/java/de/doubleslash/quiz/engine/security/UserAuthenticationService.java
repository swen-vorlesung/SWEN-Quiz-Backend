package de.doubleslash.quiz.engine.security;

import de.doubleslash.quiz.engine.repository.dao.auth.User;
import java.util.Optional;

public interface UserAuthenticationService {

  Optional<String> login(String username, String password);

  Optional<User> findByToken(String token);

  void logout(User user);
}
