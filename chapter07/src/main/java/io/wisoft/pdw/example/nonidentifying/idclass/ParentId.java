package io.wisoft.pdw.example.nonidentifying.idclass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParentId implements Serializable {

  private String id1;  // Parent.id1 매핑
  private String id2;  // Parent.id2 매핑

}
