package io.wisoft.pdw.example.identifying.embededid;

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
public class GrandChild {

  @EmbeddedId
  private GrandChildId id;

  @MapsId("childId")  // GrandChildId.childId 매핑
  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "parent_id"),
      @JoinColumn(name = "child_id")
  })
  private Child child;

  private String name;

}
