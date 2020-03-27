package io.wisoft.pdw.example.identifying.idclass;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@IdClass(ChildId.class)
public class Child {

  @Id
  @ManyToOne
  @JoinColumn(name = "parent_id")
  public Parent parent;

  @Id @Column(name = "child_id")
  private String childId;

  private String name;

}
