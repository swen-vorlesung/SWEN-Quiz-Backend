package de.doubleslash.quiz.engine.repository;

import de.doubleslash.quiz.engine.repository.dao.Quiz;
import org.springframework.data.repository.CrudRepository;

public interface QuizRepository extends CrudRepository<Quiz, Long> {

}
