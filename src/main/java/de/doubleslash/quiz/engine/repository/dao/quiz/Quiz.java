package de.doubleslash.quiz.engine.repository.dao.quiz;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.doubleslash.quiz.engine.repository.dao.auth.User;
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
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "QUI01_QUIZ")
public class Quiz {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private Long id;

  @OneToMany(mappedBy = "quiz", fetch = FetchType.EAGER)
  @JsonManagedReference
  private Set<Question> questions;

  private String name;

  @ManyToOne
  @JoinColumn(name = "USR01_ID")
  @JsonBackReference
  private User user;
}
