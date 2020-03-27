package io.wisoft.pdw.example.nonidentifying.idclass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(ParentId.class)
public class Parent {

  @Id
  @Column(name = "parent_id1")
  private String id1;

  @Id
  @Column(name = "parent_id2")
  private String id2;

  private String name;

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id1", id1)
        .append("id2", id2)
        .append("name", name)
        .toString();
  }
}
