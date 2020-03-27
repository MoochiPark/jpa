package io.wisoft.pdw.example.singletable;

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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
public abstract class Item {

  @Id @GeneratedValue
  @Column(name = "item_id")
  private Long id;

  private String name;
  private int price;

}

//@Entity
//@DiscriminatorValue("A")
//public class Album extends Item {...}
//
//@Entity
//@DiscriminatorValue("M")
//public class Movie extends Item {...}
//
//@Entity
//@DiscriminatorValue("B")
//public class Book extends Item {...}
