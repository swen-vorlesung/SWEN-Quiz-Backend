package de.doubleslash.quiz.repository;

import de.doubleslash.quiz.repository.dao.quiz.Answer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends CrudRepository<Answer, Long> {

}
