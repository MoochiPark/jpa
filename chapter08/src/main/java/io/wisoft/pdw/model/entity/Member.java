package io.wisoft.pdw.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends BaseEntity {

  @Id
  @GeneratedValue
  private Long id;

  private String name;
  private String city;
  private String street;
  private String zipcode;

  @OneToMany(mappedBy = "member")
  private List<Order> orders = new ArrayList<>();

}
