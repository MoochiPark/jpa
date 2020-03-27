package io.wisoft.pdw.example.mappedsuperclass;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

  @Id @GeneratedValue
  private Long id;
  private String name;

}
