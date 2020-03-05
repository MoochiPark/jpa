package io.wisoft.daewon;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MEMBER")
public class Member {

  @Id
  @Column(name = "ID")
  private String id;

  @Column(name = "NAME")
  private String username;

  // 매핑 정보가 없는 필드
  private Integer age;

  public Member() {
  }

  public Member(final String id, final String username, final Integer age) {
    this.id = id;
    this.username = username;
    this.age = age;
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
