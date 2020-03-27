package io.wisoft.pdw.example.identifying.embededid;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ChildId implements Serializable {

  private String parentId;  // @MapsId("parentId")로 매핑

  @Column(name = "child_id")
  private String id;

  // equals, hashCode

}
