package io.wisoft.pdw.example.identifying.embededid;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class GrandChildId implements Serializable {

  private ChildId childId; // @MapsId("childId")로 매핑

  @Column(name = "grandchild_id")
  private String id;

  // equals, hashCode

}
