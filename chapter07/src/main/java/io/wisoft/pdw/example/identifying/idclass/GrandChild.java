package io.wisoft.pdw.example.identifying.idclass;


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
@IdClass(GrandChildId.class)
public class GrandChild {

  @Id
  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "parent_id"),
      @JoinColumn(name = "child_id")
  })
  private Child child;

  @Id @Column(name = "grandchild_id")
  private String id;

  private String name;

}
