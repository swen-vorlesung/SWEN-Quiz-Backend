package de.doubleslash.quiz.repository.dao.quiz;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.doubleslash.quiz.transport.dto.QuestionDto;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "QUI02_QUESTION")
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qui02_id_generator")
  @SequenceGenerator(name="qui02_id_generator", sequenceName = "qui02_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(nullable = false)
  private String question;

  private byte[] image;

  @OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
  @JsonManagedReference
  private Set<Answer> answers;

  @ManyToOne
  @JoinColumn(name = "QUI01_ID")
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

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }

    if (object.getClass() == (QuestionDto.class)) {
      return Objects.equals(id, ((QuestionDto) object).getId());
    } else if (object.getClass() != getClass()) {
      return false;
    }

    Question question = (Question) object;
    return Objects.equals(id, question.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
