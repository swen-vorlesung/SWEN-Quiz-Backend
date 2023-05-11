package de.doubleslash.quiz.repository;

import de.doubleslash.quiz.repository.dao.quiz.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

}
