package de.doubleslash.quiz.transport.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextService {

  public String getLoggedInUser() {
    return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
  }

}
