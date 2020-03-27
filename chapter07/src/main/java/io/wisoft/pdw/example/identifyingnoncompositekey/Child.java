package io.wisoft.pdw.example.identifyingnoncompositekey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Child {

  @Id @GeneratedValue
  @Column(name = "child_id")
  private Long id;
  private String name;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private Parent parent;

}
