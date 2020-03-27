package io.wisoft.pdw.example.join;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Getter
@Setter
@Entity
@DiscriminatorValue("B")
@PrimaryKeyJoinColumn(name = "book_id")
public class Book extends Item {

  private String author;
  private String isbn;

}
