package de.doubleslash.quiz.repository;

import de.doubleslash.quiz.repository.dao.quiz.Quiz;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends CrudRepository<Quiz, Long> {

}
