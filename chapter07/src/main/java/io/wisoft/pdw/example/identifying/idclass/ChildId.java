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
public class ChildId implements Serializable {

  private String parent;   // Child.parent 매핑
  private String childId;  // Child.childId 매핑

  //equals, hashCode
}
