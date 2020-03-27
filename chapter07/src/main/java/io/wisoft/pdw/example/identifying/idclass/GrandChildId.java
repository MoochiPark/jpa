package io.wisoft.pdw.example.identifying.idclass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrandChildId implements Serializable {

  private ChildId childId;   // GrandChild.child 매핑
  private String id;         // GrandChild.id 매핑

  //equals, hashCode

}
