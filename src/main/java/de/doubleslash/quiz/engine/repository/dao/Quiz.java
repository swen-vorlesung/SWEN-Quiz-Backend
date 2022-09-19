package de.doubleslash.quiz.engine.repository.dao;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "QUI01_QUIZ")
public class Quiz {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private Long id;

  @OneToMany(mappedBy = "quiz", fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<Question> questions;

  private String name;
}
