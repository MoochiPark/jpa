package io.wisoft.pdw.example.jointable.manytoone;

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

  @Id
  @GeneratedValue
  @Column(name = "child_id")
  private Long id;
  private String name;

  @ManyToOne(optional = false) // null 허용
  @JoinTable(name = "parent_child",
      joinColumns = @JoinColumn(name = "parent_id"),
      inverseJoinColumns = @JoinColumn(name = "child_id")
  )
  private Parent parent;

}
