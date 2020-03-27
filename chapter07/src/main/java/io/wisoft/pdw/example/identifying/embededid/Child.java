package io.wisoft.pdw.example.identifying.embededid;

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

  @EmbeddedId
  private ChildId id;

  @MapsId("parentId")  // ChildId.parentId 매핑
  @ManyToOne
  @JoinColumn(name = "parent_id")
  public Parent parent;

  private String name;

}
