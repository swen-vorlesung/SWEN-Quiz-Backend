package de.doubleslash.quiz.transport.security;

import de.doubleslash.quiz.repository.dao.auth.User;
import java.util.Optional;

public interface UserAuthenticationService {

  Optional<String> login(String username, String password);

  Optional<String> register(String username, String password);

  Optional<User> findByToken(String token);

  void logout(User user);
}
