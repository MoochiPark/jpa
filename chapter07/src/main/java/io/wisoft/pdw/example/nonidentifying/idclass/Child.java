package io.wisoft.pdw.example.nonidentifying.idclass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Child {

  @Id
  private String id;

  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "parent_id1", referencedColumnName = "parent_id1"),
      @JoinColumn(name = "parent_id2", referencedColumnName = "parent_id2")
  })
  private Parent parent;

}
