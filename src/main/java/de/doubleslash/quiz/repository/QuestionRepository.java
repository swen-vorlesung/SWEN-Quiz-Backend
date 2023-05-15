package de.doubleslash.quiz.repository;

import de.doubleslash.quiz.repository.dao.quiz.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

}
