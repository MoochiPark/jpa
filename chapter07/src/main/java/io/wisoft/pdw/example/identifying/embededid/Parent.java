package io.wisoft.pdw.example.identifying.embededid;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Parent {

  @Id @Column(name = "parent_id")
  private String id;

  private String name;

}
