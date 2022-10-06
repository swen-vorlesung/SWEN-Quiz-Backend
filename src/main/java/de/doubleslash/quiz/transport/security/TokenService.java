package de.doubleslash.quiz.transport.security;

import java.util.Map;

public interface TokenService {

  String newToken(final Map<String, String> attributes);

  Map<String, String> verify(String token);

  String newUserToken(String username);
}
