package io.wisoft.pdw.example.jointable.manytomany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Parent {

  @Id
  @GeneratedValue
  @Column(name = "parent_id")
  private Long id;
  private String name;

  @ManyToMany
  @JoinTable(name = "parent_child",
      joinColumns = @JoinColumn(name = "parent_id"),
      inverseJoinColumns = @JoinColumn(name = "child_id")
  )
  private List<Child> children = new ArrayList<>();

}
