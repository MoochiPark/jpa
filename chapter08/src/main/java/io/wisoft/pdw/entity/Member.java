package io.wisoft.pdw.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Member {

  private String username;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "team_id")
  private Team team;

}
