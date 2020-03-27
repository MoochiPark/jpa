package io.wisoft.pdw.example.nonidentifying.embededid;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ParentId implements Serializable {

  @Column(name = "parent_id1")
  private String id1;
  @Column(name = "parent_id2")
  private String id2;

//  @Override
//  public boolean equals(Object o) {
//    ...
//  }
//
//  @Override
//  public int hashCode() {
//    ...
//  }

}
