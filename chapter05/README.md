# Chapter 05. 연관관계 매핑 기초

대부분의 엔티티는 다른 엔티티와 연관관계가 있다. 
그런데 객체는 참조<sup>주소</sup>를 사용해서 관계를 맺고 테이블은 외래 키를 사용해서 관계를 맺는다.
이 둘은 완전히 다르므로 객체 관계 매핑<sup>ORM</sup>에서 가장 어려운 부분이 바로 객체 연관관계와 테이블 연관관계를 매핑하는 것이다.

**객체의 참조와 테이블의 외래 키를 매핑하는 것이 이 장의 목표이다.**

시작하기 전에 연관관계 매핑의 핵심 키워드들을 요약하고 진행하면서 이해할 것이다.

- **방향**<sup>Direction</sup>: [단방향, 양방향]이 있다. 
  회원과 팀 관계가 있을 때 회원 → 팀 또는 팀 → 회원 둘 중 한 쪽만 참조하는 것을 단방향,
  양쪽 모두 서로 참조하는 것을 양방향 관계라 한다. 방향은 객체관계에만 존재하고 테이블 관계는 항상 양방향이다.
- **다중성**<sup>Multiplicity</sup>: [다대일<sup>N:1</sup>, 일대다<sup>1:N</sup>, 일대일<sup>1:1</sup>, 다대다<sup>N:M</sup>] 다중성이 있다.
  회원과 팀 관계에서 여러 회원은 한 팀에 속하므로 회원과 팀은 다대일 관계이고,
  반대로 한 팀에 여러 회원이 소속될 수 있으므로 팀과 회원은 일대다 관계다.
- **연관관계의 주인**<sup>owner</sup>: 객체를 양방향 연관관계로 만들면 연관관계의 주인을 정해야 한다.





## 5.1 단방향 연관관계

연관관계 중에선 다대일<sup>N:1</sup> 단방향 관계를 가장 먼저 이해해야 한다. 회원과 팀의 관계를 통해 다대일 단방향 연관관계를 알아보자.

- 회원과 팀이 있다.
- 회원은 하나의 팀에만 소속될 수 있다.
- 회원과 팀은 다대일 관계다.



> *다대일 연관관계 | 다대일<sup>N:1</sup>, 단방향*

- *[객체 연관관계]*

![image](https://user-images.githubusercontent.com/43429667/76401250-f26a1600-63c4-11ea-9376-ac10fb41625b.png)

- *[테이블 연관관계]*

![image](https://user-images.githubusercontent.com/43429667/76401570-6e645e00-63c5-11ea-9097-5af10c87bca2.png)



- **객체 연관관계**
  - 회원 객체는 `Member.team` 필드로 팀 객체와 연관관계를 맺는다.
  - 회원 객체와 팀 객체는 **단방향 관계**다. 회원은 `Member.team` 필드를 통해서 팀을 알 수 있지만
    반대로 팀은 회원을 알 수 없다.
    member → team의 조회는 `member.getTeam()`으로 가능하지만 반대 방향으로 접근하는 필드는 없다.



- **테이블 연관관계**
  - 회원 테이블은 TEAM_ID 외래 키로 팀 테이블과 연관관계를 맺는다.
  - 회원 테이블과 팀 테이블은 **양방향 관계**다. 회원 테이블의 TEAM_ID 외 키를 통해서 회원과 팀을 조인할 수 있고,
    반대로 팀과 회원도 조인할 수 있다.



> *회원과 팀을 조인하는 SQL*

```sql
SELECT *
FROM member, team
WHERE member.team_id = team.team_id;
```



- **객체 연관관계와 테이블 연관관계의 가장 큰 차이**

  참조를 통한 연관관계는 언제나 단방향이다. 객체간에 연관관계를 양방향으로 만들고 싶으면 반대쪽에도 필드를 추가해서
  참조를 보관해야 한다. 결국 연관관계를 하나 더 만들어야 하는 것이다. 이렇게 양쪽에서 서로 참조하는 것을 양방향 연관관계라고 한다.

  하지만 정확히 말하면 이것은 **양방향 관계가 아니라 서로 다른 단방향 관계 2개다.** 
  반면에 테이블은 외래 키 하나로 양방향으로 조인할 수 있다.



- **객체 연관관계 vs 테이블 연관관계 정리**

  - 객체는 참조로 연관관계를 맺는다.

  - 테이블은 외래 키로 연관관계를 맺는다.

    이 둘은 비슷해 보이지만 매우 다른 특징을 가진다. 연관 데이터를 조회할 때 객체는 참조, 테이블은 조인을 사용한다.

  - 참조를 사용하는 객체의 연관관계는 단방향이다.

    - A → B (a.b)

  - 외래 키를 사용하는 테이블의 연관관계는 양방향이다.

    - A JOIN B가 가능하면 B JOIN A도 가능하다.

  - 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.

    - A → B (a.b)
    - B → A (b.a)



이제 순수한 객체 연관관계 예제와 순수한 테이블 연관관계 예제를 보고 둘을 매핑해보자.



### 5.1.1 순수한 객체 연관관계

다음 코드는 JPA를 사용하지 않은 순수하게 객체만 사용한 회원과 팀 클래스의 코드다.

```java
public class Member {
  
  private String id;
  private String username;
  
  private Team team; // 팀의 참조를 보관
  
  public void setTeam(final Team team) {
    this.team = team;
  }
  
  // Getter, Setter ...
  
}

public class Team {
  
  private String id;
  private String name;
  
  // Getter, Setter ...
  
}
```

> *동작 코드*

```java
public static void main(String... args) {
  // 생성자(id, 이름)
  Member member1 = new Member("member1", "회원1");
  Member member2 = new Member("member2", "회원2");
  Team team1 = new Team("team1", "팀1");
  
  member1.setTeam(team1);
  member2.setTeam(team1);
  
  Team findTeam = member1.getTeam();
} 
```

`  Team findTeam = member1.getTeam();` 처럼 회원1이 속한 팀1을 조회할 수 있다.
이처럼 객체는 참조를 사용해 연관관계를 탐색할 수 있는데 이것을 **객체 그래프 탐색**이라 한다.



### 5.1.2 테이블 연관관계

이번에는 데이터베이스 테이블의 회원과 팀의 관계를 살펴보자. 

> *테이블 DDL*

```sql
CREATE TABLE member
(
    member_id varchar(255) primary key,
    team_id   varchar(255),
    username  varchar(255),
    CONSTRAINT fk_member_team FOREIGN KEY (team_id)
        REFERENCES team (team_id)
);

CREATE TABLE team
(
    team_id varchar(255) primary key,
    name varchar(255)
);
```

> *INSERT SQL*

```sql
INSERT INTO team VALUES ('team1', '팀1');

INSERT INTO member VALUES ('member1', 'team1', '회원1'),
                          ('member2', 'team1', '회원2');
```

> *회원 1이 소속된 팀을 조회하는 SQL*

```sql
select t.*
FROM member m, team t
WHERE m.team_id = t.team_id and m.member_id = 'member1';
```

이처럼 데이터베이스는 외래 키를 사용해서 연관관계를 탐색할 수 있다.



### 5.1.3 객체 관계 매핑

지금까지 객체만 사용한 연관관계와 테이블만 사용한 연관관계를 각각 알아보았으니, 이제 JPA를 사용해서 둘을 매핑해보자.

![image](https://user-images.githubusercontent.com/43429667/76436111-2f50ff80-63fb-11ea-90ff-3ec300c0ad81.png)



> *매핑한 회원 엔티티*

<script src="https://gist.github.com/4edf27c5118a7800e17af64a2e6e425e.js"></script>

> *매핑한 팀 엔티티*

<script src="https://gist.github.com/928a528340bf940e1de75473f00f489d.js"></script>

- **객체 연관관계**: 회원 객체의 Member.team 필드 사용
- **테이블 연관관계**: 회원 테이블의 MEMBER.TEAM_ID 외래 키 컬럼을 사용



```java
  @ManyToOne
  @JoinColumn(name = "team_id")
  private Team team;
```

- @ManyToOne : 이름 그대로 다대일 관계라는 매핑 정보다. 회원과 팀은 다대일 관계다.
  연관관계를 매핑할 때 이렇게 다중성을 나타내는 애노테이션을 필수로 사용해야 한다.
- @JoinColumn(name="team_id"): 조인 컬럼은 외래 키를 매핑할 때 사용한다.
  name 속성에는 매핑할 외래 키 이름을 지정한다. 이 애노테이션은 생략할 수 있다.



### 5.1.4 @JoinColumn

@JoinColumn은 외래 키를 매핑할 때 사용한다.

| 속성                                                         | 기능                                                         | 기본값                                        |
| ------------------------------------------------------------ | ------------------------------------------------------------ | --------------------------------------------- |
| name                                                         | 매핑할 외래 키 이름                                          | 필드명 + _ + 참조하는 테이블의 기본 키 컬럼명 |
| referencedColumnName                                         | 외래 키가 참조하는 대상 테이블의 컬럼명                      | 참조하는 테이블의 기본 키 컬럼명              |
| foreignKey(DDL)                                              | 외래 키 제약조건을 직접 지정할 수 있다. 이 속성은 테이블을 생성할 때만 사용한다. |                                               |
| unique<br />nullable<br />insertable<br />updatable<br />columnDefinition<br> table | @Column의 속성과 같다.                                       |                                               |



### @ManyToOne

| 속성         | 기능                                                         | 기본값                                                    |
| ------------ | ------------------------------------------------------------ | --------------------------------------------------------- |
| optional     | false로 설정하면 연관된 엔티티가 항상 있어야 한다.           | true                                                      |
| fetch        | 글로벌 페치 전략을 설정한다<sup>8장에서 자세히</sup>.        | @ManyToOne=FetchType.EAGER<br />@OneToMany=FetchType.LAZY |
| cascade      | 영속성 전이 기능을 사용한다<sup>8장에서 자세히</sup>.        |                                                           |
| targetEntity | 연관된 엔티티의 타입 정보를 설정한다.<br />이 기능은 거의 사용하지 않는다.<br />컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있다. |                                                           |

```java
@OneToMany
private List<Member> members; // 제네릭으로 타입 정보를 알 수 있다.

@OneToMany(targetEntity=Member.class)
private List members; // 제네릭이 없으면 타입 정보를 알 수 없다.
```



## 5.2 연관관계 사용

연관관계 매핑 작업이 끝났으니 등록, 수정, 삭제, 조회하는 예제를 통해 어떻게 사용하는지 알아보자.



### 5.2.1 저장

> *회원과 팀을 저장하는 코드*

<script src="https://gist.github.com/2dc845f91e8cbe30ea354a70cd85620a.js"></script>
</script>

중요한 부분을 분석해보자.

```java
    member1.setTeam(team1);  // 회원 -> 팀 참조
    em.persist(member1);     // 저장
```

회원 엔티티는 팀 엔티티를 참조하고 저장했다. 
이때 실행된 SQL을 보면 회원 테이블의 외래 키 값으로 참조한 팀의 식별자 값이 입력된 것을 알 수 있다.

```sql
Hibernate: 
    /* insert io.wisoft.daewon.entity.Team
        */ insert 
        into
            Team
            (name, team_id) 
        values
            (?, ?)
Hibernate: 
    /* insert io.wisoft.daewon.entity.Member
        */ insert 
        into
            Member
            (team_id, username, member_id) 
        values
            (?, ?, ?)
Hibernate: 
    /* insert io.wisoft.daewon.entity.Member
        */ insert 
        into
            Member
            (team_id, username, member_id) 
        values
            (?, ?, ?)
```

![image](https://user-images.githubusercontent.com/43429667/76445724-37b03700-6409-11ea-958c-45fbad1f2f40.png)



### 5.2.2 조회

연관관계가 있는 엔티티를 조회하는 방법은 크게 2가지이다.

- 객체 그래프 탐색<sup>객체 연관관계를 사용한 조회</sup>
- 객체지향 쿼리 사용<sup>JPQL</sup>



- **객체 그래프 탐색**

  member.getTeam()을 사용해서 member와 연관된 team 엔티티를 조회할 수 있다.

  ```java
  Member member = em.find(Member.class, "member");
  Team teeam = member.getTeam(); // 객체 그래프 탐색
  System.out.println("팀 이름 = " + team.getName()); // 출력 결과 : 팀 이름 = 팀1
  ```

  객체를 통해 연관된 엔티티를 조회하는 것을 객체 그래프 탐색이라 한다<sup>8장에서 자세히</sup>.



- **객체지향 쿼리 사용**

  JPQL도 조인을 지원하므로 연관된 테이블을 조인해서 검색조건을 사용하면 된다.

  > *JPQL 조인 탐색*

  <script src="https://gist.github.com/573d576b6b34cf4607213add69c5daae.js"></script>
    
  </script>

  

  JPQL의 `from Member m from m.team t` 부분을 보면 회원이 팀과 관계를 가지고 있는 필드<sup>m.team</sup>를 통해서 
  Member와 Team을 조인했다. 그리고 where 절에서 t.name을 검색 조건으로 사용해서 팀1에 속한 회원만 검색했다.

  참고로 teamName과 같이 :로 시작하는 것은 파라미터를 바인딩받는 문법이다. 이때 실행되는 SQL을 보자.

  ```sql
  SELECT m.*
  FROM member m
  inner join team t on m.team_id = t.team_id
  where t.name='팀1';
  ```

  SQL과 JPQL을 비교하면 JPQL은 객체<sup>엔티티</sup>를 대상으로 하고 SQL보다 간결하다. 객체 쿼리는 10장에서 자세히 다룬다.





### 5.2.3 수정

팀1 소속이던 회원을 새로운 팀2에 소속하도록 수정해보자.

> *연관관계 수정 코드*

<script src="https://gist.github.com/197b1581204f08579c3fe4e6309c88e4.js"></script>

```sql
Hibernate: 
    /* update
        io.wisoft.daewon.entity.Member */ update
            Member 
        set
            team_id=?,
            username=? 
        where
            member_id=?
```

실행되는 수정 SQL은 위와 같다. 단순히 불러온 엔티티의 값만 변경해두면 트랜잭션을 커밋할 때 플러시가 일어나면서 변경 감지 기능이 작동한다. 그리고 변경사항을 데이터베이스에 자동으로 반영한다.
이것은 연관관계를 수정할 때도 같으므로 참조하는 대상만 변경하면 나머지는 JPA가 자동으로 처리한다.



### 5.2.4 연관관계 제거

회원 1을 팀에 소속하지 않도록 변경해보자.

> *연관관계 제거 코드*

<script src="https://gist.github.com/a0d1cdf9005d1edec120771481e1c365.js"></script>

이 때 실행되는 SQL은 다음과 같다.

```sql
        update
            Member 
        set
            team_id=null,
            ... 
        where
            member_id='member1'
```

### 5.2.5 연관된 엔티티 삭제

연관된 엔티티를 삭제하려면 기존에 있던 연관관계를 먼저 제거하고 삭제해야 한다. 그렇지 않으면 외래 키 제약조건으로 인해
데이터베이스에서 오류가 발생한다. 이때 팀1을 삭제하려면 연관관계를 먼저 끊어야 한다.

```java
member1.setTeam(null);
member2.setTeam(null);
em.remove(team);
```





## 5.3 양방향 연관관계

지금까지 회원에서 팀으로만 접근하는 다대일 단방향 매핑을 알아보았다. 이번에는 팀에서 회원으로 접근하는 관계를 추가해보자.

> *양방향 객체 연관관계*

![image](https://user-images.githubusercontent.com/43429667/76450404-0fc4d180-6411-11ea-9bde-0a9f931849ff.png)

먼저 객체 연관관계를 보면 회원과 팀은 다대일 관계다. 반대로 팀에서 회원은 일대다 관계다. 일대다 관계는 여러 건과 연관관계를
맺을 수 있으므로 컬렉션을 사용해야 한다. Team.members를 List 컬렉션으로 추가했다.

- 회원 → 팀 : Member.team
- 팀 → 회원 : Team.members

> JPA는 List를 포함한 Collection, Set, Map 같은 다양한 컬렉션을 지원한다.



테이블의 관계는 미리 말한 것처럼 **데이터베이스 테이블은 외래 키 하나로 양방향으로 조회할 수 있다.**
따라서 데이터베이스에 추가할 내용은 전혀 없다.

![image](https://user-images.githubusercontent.com/43429667/76401570-6e645e00-63c5-11ea-9097-5af10c87bca2.png)



### 5.3.1 양방향 연관관계 매핑

회원 엔티티에는 변경할 부분이 없다. 팀의 엔티티를 보자.

> *매핑한 팀 엔티티*

<script src="https://gist.github.com/756113393b4bb11e392d69500929bf52.js"></script>

팀과 회원은 일대다 관계다. 따라서 members를 추가해주었다. 
그리고 일대다 관계를 매핑하기 위해 @OneToMany 매핑 정보를 사용했다. mappedBy 속성은 양방향 매핑일 때 사용하는데
반대쪽 매핑의 필드 이름을 값으로 주면 된다.

이것으로 양방향 매핑을 완료했으니 팀1에 소속된 모든 회원을 찾아보자.



### 5.3.2 일대다 컬렉션 조회

> *일대다 방향으로 객체 그래프 탐색*

<script src="https://gist.github.com/9cc988fe9f10f6371cb77be5b7a099ff.js"></script>

**실행 결과**

```java
member.username=회원1
member.username=회원2
```





## 5.4 연관관계의 주인

@OneToMany는 직관적으로 이해가 가지만 문제는 mappedBy 속성이다. mappedBy는 왜 필요한지 알아보자.

객체에서의 양방향 연관관계는 사실 없다. 서로 다른 단방향 연관관계 2개를 애플리케이션 로직으로 잘 묶어서 양방향인 것처럼 보이게 할 뿐이다. 반면에 데이터베이스 테이블은 앞서 설명했듯이 외래 키 하나로 양쪽이 서로 조인할 수 있다.

- 회원 → 팀 연관관계 1개<sup>단방향</sup>
- 팀 → 회원 연관관계 1개<sup>단방향</sup>

테이블은

- 회원 ↔ 팀 연관관계 1개<sup>양방향</sup>

다시 강조하자면 **테이블은 외래 키 하나로 두 테이블의 연관관계를 관리**한다.

**엔티티를 단방향으로 매핑하면 참조를 하나만 사용**하므로 이 참조로 외래 키를 관리하면 된다.
그런데 엔티티를 양방향으로 매핑하면 두 곳에서 서로를 참조하므로 객체의 연관관계를 관리하는 포인트는 2개로 늘어난다.

**엔티티를 양방향 연관관계로 설정하면 객체의 참조는 둘인데 외래 키는 하나다. 따라서 둘 사이에 차이가 발생한다.**

이런 차이로 인해 JPA는 **두 객체 연관관계 중 하나를 정해 테이블의 외래 키를 관리하는데 이걸 연관관계의 주인<sup>Owner</sup>**이라 한다.



### 5.4.1 양방향 매핑의 규칙: 연관관계의 주인

양방향 연관관계 매핑 시 두 연관관계 중 하나를 연관관계의 주인으로 정해야 한다.
**연관관계의 주인만이 데이터베이스 연관관계와 매핑되고 외래키를 관리**<sup>등록, 수정, 삭제</sup>**할 수 있다. 반면에 주인이 아닌 쪽은 읽기만 가능하다.**

어떤 연관관계를 주인으로 정할지는 mappedBy 속성을 사용하면 된다.

- 주인은 mappedBy 속성을 사용하지 않는다.
- 주인이 아니면 mappedBy 속성을 사용해서 속성의 값으로 연관관계의 주인을 지정해야 한다.

그렇다면 Member.team, Team.members 중 어떤 것을 연관관계의 주인으로 해야할까?

**연관관계의 주인을 정한다는 것은 외래 키 관리자를 선택하는 것이다.** 
만약 회원 엔티티에 있는 Member.team을 주인으로 선택하면 자기 테이블에 있는 외래 키를 관리하면 된다.
하지만 Team.members를 주인으로 선택하면 물리적으로 전혀 다른 테이블의 외래 키를 관리해야 한다.



### 5.4.2 연관관계의 주인은 외래 키가 있는 곳

따라서 연관관계의 주인은 테이블에 외래 키가 있는 곳으로 정해야 한다. 여기서는 회원 테이블에 외래 키가 있으므로
Member.team이 주인이 된다. 주인이 아닌 Team.members에는 mappedBy="team" 속성으로 주인이 아님을 설정한다.
mappedBy의 값은 연관관계의 주인을 주면 된다. 여기서 "team"은 Member 엔티티의  team 필드를 말한다.

![image](https://user-images.githubusercontent.com/43429667/76456828-06d8fd80-641b-11ea-856f-29414878006c.png)

정리하면 연관관계의 주인만 데이터베이스 연관관계와 매핑되고 외래 키를 관리할 수 있다.
주인이 아닌 반대편<sup>inverse, non-owning side</sup>은 읽기만 가능하고 외래 키를 변경하지는 못한다.

> *데이터베이스 테이블의 다대일, 일대다 관계에서는 항상 다 쪽이 외래 키를 가진다.*
> *다 쪽인 @ManyToOne은 항상 연관관계의 주인이 되므로 mappedBy를 설정할 수 없다.*
> *따라서 ManyToOne에는 mappedBy 속성이 없다.*





## 5.5 양방향 연관관계 저장

양방향 연관관계를 사용해서 팀1, 회원1, 회원2를 저장해보자.

> *양방향 연관관계 저장*

<script src="https://gist.github.com/8931077ebedc3ddabf2d4e7c0e17414f.js"></script>

참고로 이 코드는 **단방향 연관관계에서 살펴본 회원과 팀을 저장하는 코드와 완전히 같다.**
데이터베이스에서 회원 테이블을 조회 해보자.

![image](https://user-images.githubusercontent.com/43429667/76488571-3adb0f80-6469-11ea-842b-94ee17c91edd.png)

양방향 연관관계에서는 연관관계의 주인이 외래 키를 관리한다. 따라서 주인이 아닌 방향은 값을 설정하지 않아도 데이터베이스에
외래 키 값이 정상 입력된다. 

```java
// 주인이 아닌 곳에 입력된 값은 외래 키에 영향을 주지 않는다.
team1.getMembers().add(member1); // 무시(연관관계의 주인이 아님)
team1.getMembers().add(member2); // 무시(연관관계의 주인이 아님)

// Member.team은 연관관계의 주인이므로 엔티티 매니저는 이곳에 입력된 값을 사용해서 외래 키를 관리한다.
member1.setTeam(team1); // 연관관계 설정(연관관계의 주인)
member2.setTeam(team1); // 연관관계 설정(연관관계의 주인)
```



## 5.6 양방향 연관관계의 주의점

데이터베이스에 외래 키 값이 정상적으로 저장되지 않으면 주인이 아닌 곳에만 값을 입력하지 않는지 의심해봐야 한다.

주인이 아닌 곳에만 값을 설정하면 어떻게 되는지 알아보자.

<script src="https://gist.github.com/d2f3b77b7d63d50e6c59ea095b6bd74f.js"></script>

회원1, 회원2를 저장하고 팀의 컬렉션에 담은 후에 팀을 저장했다. 데이터베이스에서 회원 테이블을 조회해보자.

![image](https://user-images.githubusercontent.com/43429667/76495031-6cf46d80-6479-11ea-80dd-57dbafa70d73.png)

연관관계의 주인이 아닌 Team.members에만 값을 저장했으므로 team_id에 null 값이 입력되어 있는 것을 볼 수 있다.



### 5.6.1 순수한 객체까지 고려한 양방향 연관관계

정말 연관관계의 주인에만 값을 저장하고 주인이 아닌 곳에는 값을 저장하지 않아도 될까? 사실은 **객체 관점에서 양쪽 방향에 모두 값을 입력해주는 것이 가장 안전하다.** 양쪽 방향 모두 값을 입력하지 않으면 JPA를 사용하지 않는 순수한 객체 상태에서 심각한 문제가 발생할 수 있다.

예를 들어 JPA를 사용하지 않고 엔티티에 대한 테스트 코드를 작성한다고 해보자.
ORM은 객체와 관계형 데이터베이스 둘 다 중요한데, 데이터베이스와 객체 모두 고려해야 한다.

> *순수한 객체 연관관계*

<script src="https://gist.github.com/4056b9f00719e042812b908a657f3a5f.js"></script>
  
</script>

**실행 결과**

```java
members.size = 0
```

Member.team에만 연관관계를 설정하고 반대 방향은 연관관계를 설정하지 않았다. 
실행 결과를 보면 우리가 기대하던 양방향 연관관계가 아닌 것을 볼 수 있다.

양방향은 양쪽다 관계를 설정해야 한다. 위처럼 회원 → 팀을 설정하면 반대방향인 팀 → 회원도 설정해야 한다.

`team1.getMembers().add(member1);`

> *양방향 모두 관계를 설정*

<script src="https://gist.github.com/72c56ec89f79bfd8c4a6ec089d9a8488.js"></script>

**실행 결과**

```java
members.size = 2
```

양쪽 모두 관계를 설정하니 기대했던 결과 2가 나온다.

객체 까지 고려하면 이렇게 양쪽 다 관계를 맺어야 하는 것이다. 이제 JPA를 사용해서 완성한 코드를 보자.

> *JPA로 코드 완성*

<script src="https://gist.github.com/6229b9a5f163ae75cbff332df868ce00.js"></script>

양쪽에 연관관계를 설정했다. 따라서 순수한 객체 상태에서도 동작하고, 테이블의 외래 키도 정상 입력된다.
물론 외래 키의 값은 연관관계의 주인인 Member.team 값을 사용한다.

- Member.team: 연관관계의 주인, 이 값으로 외래 키를 관리한다.
- Team.members: 연관관계의 주인이 아니다. 따라서 저장 시에 사용되지 않는다.



따라서 결론은 객체의 양방향 연관관계는 양쪽 모두 관계를 맺어주자.



### 5.6.2 연관관계 편의 메서드

양방향 연관관계는 결국 양쪽 다 신경 써야 한다. 위처럼 양방향 연관관계 코드를 작성하다보면 둘 중 하나만 실수로 놓치게되면
양방향이 깨질 수 있다.

따라서 양방향 관계에선 두 코드를 하나인 것처럼 사용하는 것이 안전하다.

<script src="https://gist.github.com/e629602c9ab15f95f2a1ed49ef4d318b.js"></script>

이렇게 리팩토링하면 실수도 줄어들고 좀 더 그럴듯하게 양방향 연관관계를 설정할 수 있다.

이렇게 한 번에 양방향 관계를 설정하는 메서드를 연관관계 편의 메서드라 한다.

### 5.6.3 연관관계 편의 메서드 작성 시 주의사항

하지만 이전의 setTeam() 메서드에는 사실 버그가 있다.

```java
member.setTeam(teamA); // 1
member.setTeam(teamB); // 2
Member findMember = teamA.getMember(); // member1이 여전히 조회된다.
```

이 시나리오를 그림으로 분석해보자.

> *1*

![image](https://user-images.githubusercontent.com/43429667/76498401-26eed800-6480-11ea-8ceb-49755080153d.png)

> *2(삭제되지 않은 관계)*

![image](https://user-images.githubusercontent.com/43429667/76737070-76e4dc00-67ab-11ea-8545-40c93f23dd12.png)

teamB로 변경할 때 teamA → member1 관계를 제거하지 않았다. 따라서 기존 팀과 회원의 연관관계를 삭제하는 코드를 추가해야 한다.

<script src="https://gist.github.com/7b3d6dc1d01764fce87aba666ef793c3.js"></script>

이 코드는 서로 다른 단방향 연관관계 2개를 양방향인 것처럼 보이게 하기위해 얼마나 많은 고민과 수고가 필요한지 보여준다.

반면에 관계형 데이터베이스는 외래 키 하나로 문제를 단순하게 해결한다. 
객체에서 양방향 연관관계를 사용하려면 로직을 견고하게 작성해야 한다.



## 5.7 정리

- **단방향 매핑만으로 테이블과 객체의 연관관계 매핑은 이미 완료되었다.**
- **단방향을 양방향으로 만들면 반대방향으로 객체 그래프 탐색 기능이 추가된다.**
- **양방향 연관관계를 매핑하려면 객체에서 양쪽 방향을 모두 관리해야 한다.**

양방향 매핑은 복잡하므로 우선 단방향 매핑을 사용하고 반대 방향으로 객체 그래프 탐색 기능<sup>JPQL 쿼리 탐색 포함</sup>이 필요할 때 양방향을 사용하도록 코드를 추가해도 된다.

**연관관계의 주인은 외래 키의 위치와 관련해야 정해야한다. 비즈니스 중요도로 접근하면 안 된다.**



[실전 예제 - 연관관계 매핑 시작](https://github.com/MoochiPark/jpa/tree/master/chapter05/src)

