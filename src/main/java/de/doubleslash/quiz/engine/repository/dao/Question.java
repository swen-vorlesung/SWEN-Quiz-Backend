package de.doubleslash.quiz.engine.repository.dao;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "QUI02_QUESTION")
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(nullable = false)
  private String question;

  private byte[] image;

  @OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
  private List<Answer> answers;

  @ManyToOne
  @JoinColumn(name = "quiz_id")
  private Quiz quiz;

  /**
   * Answer Time in Seconds.
   */
  @Column(nullable = false)
  private Long answerTime;

  @Transient
  public boolean isCorrectAnswer(Long answerId) {
    return answers.stream()
        .filter(a -> a.getId().equals(answerId))
        .map(Answer::getIsCorrect)
        .findFirst()
        .orElse(Boolean.FALSE);
  }
}
