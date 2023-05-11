package de.doubleslash.quiz.repository;

import de.doubleslash.quiz.repository.dao.quiz.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

}
