package de.doubleslash.quiz.engine.repository;

import de.doubleslash.quiz.engine.repository.dao.Quiz;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends CrudRepository<Quiz, Long> {

  Optional<Quiz> findDistinctById(Long id);
}
