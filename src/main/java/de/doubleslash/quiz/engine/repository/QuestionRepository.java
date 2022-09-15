package de.doubleslash.quiz.engine.repository;

import de.doubleslash.quiz.engine.repository.dao.Question;
import org.springframework.data.repository.CrudRepository;


public interface QuestionRepository extends CrudRepository<Question, Long> {

}
