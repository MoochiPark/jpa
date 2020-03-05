package io.wisoft.daewon.entity;


import io.wisoft.daewon.RoleType;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "Member")
@Table(name = "MEMBER", uniqueConstraints = {@UniqueConstraint(
    name = "NAME_AGE_UNIQUE",
    columnNames = {"NAME", "AGE"} )})
//@SequenceGenerator(
//    name = "MEMBER_SEQ_GENERATOR",
//    sequenceName = "MEMBER_SEQ",
//    initialValue = 1, allocationSize = 50)
public class Member {

//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
  @Id @GeneratedValue
  private Long id;

  @Column(name = "NAME", nullable = false, length = 10)
  private String username;

  private Integer age;

  // 추가
  @Enumerated(EnumType.STRING)
  private RoleType roleType;

  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModifiedDate;

  @Lob
  private String description;

  public Member() {
  }

  public Member(final Long id, final String username, final Integer age) {
    this.id = id;
    this.username = username;
    this.age = age;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(final Integer age) {
    this.age = age;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Member{");
    sb.append("id='").append(id).append('\'');
    sb.append(", username='").append(username).append('\'');
    sb.append(", age=").append(age);
    sb.append('}');
    return sb.toString();
  }
}
