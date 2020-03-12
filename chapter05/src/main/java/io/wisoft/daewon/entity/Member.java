package io.wisoft.daewon.entity;

import javax.persistence.*;

@Entity
public class Member {

  @Id
  @Column(name = "member_id")
  private String id;

  private String username;

  // 연관관계 매핑
  @ManyToOne
//      (cascade = {CascadeType.ALL})
  @JoinColumn(name = "team_id")
  private Team team;

  public Member() {
  }

  public Member(final String id, final String username) {
    this.id = id;
    this.username = username;
  }

  // 연관관계 설정
  public void setTeam(final Team team) {
    if (this.team != null) {
      this.team.getMembers().remove(this);
    }
    this.team = team;
    team.getMembers().add(this);
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public Team getTeam() {
    return team;
  }

}
