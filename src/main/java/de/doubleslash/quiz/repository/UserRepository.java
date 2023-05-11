package de.doubleslash.quiz.repository;

import de.doubleslash.quiz.repository.dao.auth.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByName(String username);
}
