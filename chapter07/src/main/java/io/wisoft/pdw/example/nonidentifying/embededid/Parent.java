package io.wisoft.pdw.example.nonidentifying.embededid;

import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Parent {

  @EmbeddedId
  private ParentId id;

  private String name;

}
