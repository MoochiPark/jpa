package io.wisoft.pdw.example.jointable.manytoone;

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

  @Id @GeneratedValue
  @Column(name = "parent_id")
  private Long id;
  private String name;

  @OneToMany(mappedBy = "parent")
  private List<Child> children = new ArrayList<>();

}
