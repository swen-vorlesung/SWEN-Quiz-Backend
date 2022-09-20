package de.doubleslash.quiz.engine.repository.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
  @JsonManagedReference
  private List<Answer> answers;

  @ManyToOne
  @JoinColumn(name = "quiz_id")
  @JsonBackReference
  private Quiz quiz;

  /**
   * Answer Time in Seconds.
   */
  @Column(nullable = false)
  private Long answerTime;

  @Transient
  public int countCorrectAnswers(List<Long> answerIds) {
    var correctAnswerCount = 0;
    for (Long answerId : answerIds) {

      var isCorrect = answers.stream()
          .filter(a -> a.getId().equals(answerId))
          .map(Answer::getIsCorrect)
          .findFirst()
          .orElse(Boolean.FALSE);

      if (Boolean.TRUE.equals(isCorrect)) {
        correctAnswerCount++;
      }
    }
    return correctAnswerCount;
  }

  @Transient
  public int getTotalAmaountOfCorrectAnswers() {
    return (int) answers.stream()
        .filter(Answer::getIsCorrect)
        .count();
  }
}
