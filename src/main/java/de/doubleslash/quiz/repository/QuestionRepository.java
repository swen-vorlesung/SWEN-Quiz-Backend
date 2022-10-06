package de.doubleslash.quiz.repository;

import de.doubleslash.quiz.repository.dao.quiz.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends CrudRepository<Question, Long> {

}
