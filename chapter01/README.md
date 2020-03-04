# Chapter 01. JPA 소개

객체를 관계형 데이터베이스에 저장하려면 많은 시간과 코드를 소비해야 한다. 자바 진영에서는 이런 문제를 해결하기 위해 
JPA라는 표준 기술을 제공한다. 자바 ORM<sup>Object Relational Mapping</sup> 표준 JPA는 SQL 작성 없이 객체를 데이터베이스에 직접
저장할 수 있게 도와주고, 객체와 데이터베이스의 차이도 중간에서 해결해준다.



> ***이 장의 내용***
>
> - SQL을 직접 다룰 때 발생하는 문제점
> - 패러다임의 불일치
> - JPA란 무엇인가?



MyBatis, 스프링의 JdbcTemplate 같은 SQL 매퍼를 사용하면 JDBC 코드를 많이 줄일 수 있지만 여전히 등록, 수정, 삭제, 조회(CRUD)용 SQL은 반복해서 작성하게 된다. 

JPA는 지루하고 반복적인 CRUD SQL을 알아서 처리해줄 뿐만 아니라 객체 모델링과 관계형 데이터베이스 사이의 차이점도 해결해준다. JPA는 실행 시점엣 자동으로 SQL을 만들어서 실행하는데 개발자는 SQL을 직접 작성하는 것이 아니라 어떤 SQL이 실행될지 생각만 하면 된다.

반복적인 CRUD SQL을 작성하고 객체를 SQL에 매핑하는 데 시간을 보내기에는 우리의 시간이 너무 아깝다.
이미 많은 자바 개발자들이 오랫동안 비슷한 문제를 해결하려고 노력해왔고 그 노력의 결정체가 바로 JPA이다.

지금부터는 SQL을 직접 다룰 때 어떤 문제가 발생하는지, 객체와 관계형 데이터베이스 사이에는 어떤 차이가 있는지 알아보자.



## 1.1 SQL을 직접 다룰 때 발생하는 문제점

관계형 데이터베이스?

- 가장 대중적이고 신뢰할 만한 데이터 저장소.
- 자바로 개발하는 애플리케이션은 대부분 관계형 데이터베이스를 데이터 저장소로 사용.



### 1.1.1 반복, 반복 그리고 반복

SQL을 직접 다룰 때의 문제점을 알아보기 위해 자바와 관계형 데이터베이스를 사용해서 회원 관리 기능을 개발해보자.
다음처럼 회원을 CRUD하는 기능을 개발해보자.

> *예제 1.1 회원 객체*

```java
public class Member {

  private String memberId;
  private String name;
  ...
}
```



다음으로 회원 객체를 데이터베이스에 관리할 목적으로 회원용 DAO<sup>Data Access Object</sup>를 만들자.

> *예제 1.2 회원용 DAO*

```java
public class MemberDao {
  
  public Member find(final String memberId) {
    ...
  }
  
}
```

MemberDao의 find() 메소드를 완성해서 회원을 조회하는 기능을 개발해보자. 보통 다음 순서로 개발할 것이다.

1. 회원 조회용 SQL을 작성한다.

   `SELECT member_id, name FROM member where member_id = ?`

2. JDBC API를 사용해서 SQL을 실행한다.

   `ps = conn.preparedStatement(query);`

   `rs = ps.executeQuery();`

3. 조회 결과를 Member 객체로 매핑한다.

   ```java
   Member member = new Member();
   member.setMemberId(rs.getString("member_id"));
   member.setName(rs.getString("name"));
   ...
   ```

   

회원 조회 기능을 완성했으니 회원 등록 기능도 만들어보자.

> *예제 1.3 회원 등록 기능 추가*

```java
public class MemberDao {
  
  public Member find(final String memberId) {
    ...
  }
  
  public void save(final Member member) {
    ...
  }
  
}
```

1. 회원 등록용 SQL을 작성한다.

   `String query = "INSERT INTO member(member_id, name) VALUES(?, ?)";`

2. 회원 객체의 값을 꺼내서 등록 SQL에 전달한다.

   `pstmt.setString(1, member.getMemberId());`

   `pstmt.setString(2, member.getName());`

3. JDBC API를 사용해서 SQL을 실행한다.

   `pstmt.excuteUpdate(query);`

다음으로 회원을 수정하고 삭제하는 기능도 추가한다면, SQL을 작성하고 JDBC API를 사용하는 비슷한 일을 반복할 것이다.

만약 회원 객체를 데이터베이스가 아닌 자바 컬렉션에 보관한다면 다음 한 줄로 객체를 저장할 수 있을 것이다.

<center><i>list.add(member);</i></center>

하지만 데이터베이스는 객체 구조와는 다른 데이터 중심의 구조를 가지므로 객체를 데이터베이스에 직접 저장하거나 조회할 수 
없다. 따라서 개발자가 객체지향 애플리케이션과 데이터베이스 중간에서 SQL과 JDBC API를 사용해서 직접 변환해주어야 한다.

여기서 문제는 객체를 데이터베이스에 CRUD하려면 너무 많은 SQL과 JDBC API를 코드로 작성해야 한다는 점이다.
그리고 테이블마다 이런 비슷한 일을 반복해야 한다. 데이터 접근 계층<sup>DAO</sup>를 개발하는 일은 이렇게 지루하고 반복적이다.



### 1.1.2 SQL에 의존적인 개발

갑자기 회원의 연락처도 함께 저장해달라는 요구사항이 추가되었다고 해보자.

> *예제 1.4 회원 클래스에 연락처 필드 추가*

```java
public class Member {

  private String memberId;
  private String name;
  private String tel; // 추가
  ...
}
```



1. **등록 코드 변경**

   연락처를 저장할 수 있도록 INSERT SQL을 수정해야 한다.

   `String sql = "INSERT INTO member (member_id, name, tel) values(?, ?, ?)";`



2. **조회 코드 변경**

   제대로 출력이 되도록 조회 SQL에 연락처 컬럼을 추가해야 한다.

   `SELECT member_id, name, tel FROM member WHERE member_id = ?`

   또한 연락처의 조회 결과를 Member 객체에 추가로 매핑해야 한다.

   `String tel = rs.getString("TEL");`

   `member.setTel(tel);`

   

3. **수정 코드 변경**

   UPDATE SQL에서 TEL 컬럼을 추가해야만 연락처가 정상적으로 수정될 것이다. 그렇다면 또 다시 UPDATE SQL과 MemberDao.update()의 일부 코드를 변경해서 연락처가 정상적으로 수정되도록 해야한다.

   만약 회원 객체를 데이터베이스가 아닌 자바 컬렉션에 보관했다면 필드를 추가한다고 해서 이렇게 많은 코드를 수정할 필요는
   없을 것이다.

   ```java
   list.add(member); // 등록
   Member member = list.get(xxx); //조회
   member.setTel("xxx"); // 수정
   ```



4. **연관된 객체가 존재할 경우**

회원은 어떤 한 팀에 필수로 소속되어야 한다는 요구사항이 생겼다고 해보자. 팀 모듈을 전담하는 개발자가 Member 객체에 team 필드를 추가 해놨다. 이 때 회원 정보를 출력할 때 연관된 팀 이름도 함께 출력하는 기능을 추가한다고 생각해보자.

> *예제 1.5 회원 클래스에 연관된 팀 추가*

```java
public class Member {

  private String memberId;
  private String name;
  private String tel;
  private Team team;
  ...
}
```

다음의 코드를 추가해서 화면에 팀의 이름을 출력하도록 했다.

소속 팀: `member.getTeam().getTeamName();`

하지만 그대로 실행하게되면 member.getTeam()의 값이 항상 null일 것이다. 

- 회원을 조회하는 find() 메서드는 기본 동작(회원 정보만 조회)만 수행
- findWithTeam()이라는 회원과 연관된 팀을 함께 조회하는 메서드 발견
- 결국 DAO를 열어서 SQL을 확인하고 나서야 원인 포착
- 회원 조회 코드를 memberDao.find()에서 memberDao.findWithTeam()으로 변경하여 해결



그렇다면 SQL의 문제점들을 정리해보자.

Member 객체가 연관된 Team 객체를 사용할 수 있을지 없을지는 전적으로 사용되는 SQL에 달려 있다. 이런 방식의 가장 큰 
문제점은 DAO를 사용해서 SQL을 숨겨도 결국 DAO를 열어서 어떤 SQL이 실행되는지 확인해야하는 점이다.

Member나 Team처럼 비즈니스 요구사항을 모델링한 객체를 엔티티라 하는데, 지금처럼 SQL에 모든 것을 의존하는 상황에선
개발자들이 엔티티를 신뢰하고 사용할 수 없다. DAO를 열어 어떤 SQL이 실행되고 어떤 객체들이 함께 조회되는지 일일히 확인
해야한다. 이것은 진정한 의미의 계층 분할이 아니다.

물리적으로 SQL과 JDBC API를 데이터 접근 계층에 숨겼어도 논리적으로는 엔티티와 아주 강한 의존관계를 가지고 있다.

이런 강한 의존관계 때문에 회원을 조회할 때는 물론이고 회원 객체에 필드를 하나만 추가해도 DAO의 CRUD 코드, SQL의
대부분을 변경해야 하는 문제가 발생한다. 애플리케이션에서 SQL을 직접 다룰 때 발생하는 문제점을 요약하자면,

- 진정한 의미의 계층 분할이 어렵다.
- 엔티티를 신뢰할 수 없다.
- SQL에 의존적인 개발을 피하기 어렵다.



### 1.1.3 JPA와 문제 해결

곧 배우게될 JPA는 위의 문제들을 어떻게 해결할까? 

JPA를 사용하면 객체를 데이터베이스에 저장하고 관리할 때, 개발자가 SQL을 작성하는 것이 아니라 JPA가 제공하는 API를 사용하면 된다. 그러면 JPA가 개발자 대신 **적절한 SQL을 생성해서 데이터베이스에 전달**한다.

자세한 내용을 알아보기 전에 JPA가 제공하는 CRUD API를 간단히 알아보자.

- **저장 기능**

  `jpa.persist(member);`

  persist() 메서드는 객체를 데이터베이스에 저장한다. 이 메소드를 호출하면 JPA가 객체와 매핑정보를 보고 적절한 INSERT SQL을 생성해서 데이터베이스에 전달한다. 

  > **매핑정보**: 어떤 객체를 어떤 테이블에 관리할지 정의한 정보(다음 장에서 자세히)

- **조회 기능**

  `String memberId = "helloId";`

  `Member member = jpa.find(Member.class, memberId);`

  find() 메서드는 객체 하나를 데이터베이스에서 조회한다. JPA는 객체와 매핑정보를 보고 적절한 SELECT SQL을 생성해서 
  데이터베이스에 전달하고 그 결과로 Member 객체를 생성해서 반환한다.

- **수정 기능**

  `Member member = jpa.find(Member.class, memberId);`

  `member.setName("이름변경");`

  JPA는 별도의 수정 메서드를 제공하지 않는다. 대신에 객체를 조회해서 값을 변경만 하면 트랜잭션을 커밋할 때 데이터베이스에 적절한 UPDATE SQL이 전달된다. (3장에서 자세히)

- **연관된 객체 조회**

  `Member member = jpa.find(Member.class, memberId);`

  `Team team = member.getTeam();`

  JPA는 연관된 객체를 사용하는 시점에 적절한 SELECT SQL을 실행한다. 따라서 JPA를 사용하면 연관된 객체를 마음껏 조회할 수 있다.(8장에서 자세히)

수정 기능과 연관된 객체 조회에서 설명한 것처럼 JPA는 SQL을 대신 작성해서 실행하는 것 이상의 기능들을 제공한다.

다음은 관계형 데이터베이스의 패러다임 차이 때문에 발생하는 다양한 문제를 살펴보고 JPA는 이런 문제들을 어떻게 해결하는지 알아보자.



## 1.2 패러다임의 불일치

애플리케이션은 발전하면서 그 내부의 복잡성도 점점 커진다. 복잡성을 제어하지 못하면 결국 유지보수하기 어려운 애플리케이션이 된다.

객체지향 프로그래밍은 추상화, 캡슐화, 정보은닉, 상속, 다형성 등 시스템의 복잡성을 제어할 수 있는 다양한 장치들을 제공한다.
그렇기에 현대의 복잡한 애플리케이션은 대부분 객체지향 언어로 개발된다. 도메인 모델도 객체로 모델링하면 객체지향 언어가 
가진 장점들을 활용할 수 있다. 문제는 이렇게 정의한 도메인 모델을 저장할 때 발생한다. 

예를 들어 특정 유저가 시스템에 회원 가입하면 회원이라는 객체 인스턴스를 생성한 후에 이 객체를 어딘가에 영구 보관해야 한다.

객체는 속성<sup>필드</sup>과 기능<sup>메서드</sup>을 가진다. 객체가 단순하면 모든 속성 값을 꺼내서 저장하면 되지만 부모 객체를 상속받거나, 다른 
객체를 참조하고 있다면 객체의 상태를 저장하기가 쉽지 않다. 

예를 들어 회원 객체를 저장해야 하는데 회원 객체가 팀 객체를 참조하고 있다면, 회원 객체를 저장할 때 팀 객체도 저장해야 한다.
관계형 데이터베이스는 데이터 중심으로 구조화되어 있고, 집합적인 사고를 요구한다. 또한 객체지향에서의 추상화, 상속, 다형성 같은 개념이 없다.

객체와 관계형 데이터베이스는 **지향하는 목적이 서로 다르므로 둘의 기능과 표현 방법도 다르다**. 이것을 객체와 
관계형 데이터베이스의 **패러다임 불일치 문제**라고 한다. 따라서 객체 구조를 테이블 구조에 저장하는 데는 한계가 있다.

애플리케이션은 자바로 개발하고 데이터는 관계형 데이터베이스에 저장해야 한다면, 패러다임의 불일치 문제를 중간에서 개발자가 해결해야 한다.

지금부터 패러다임의 불일치로 인해 발생하는 문제를 구체적으로 살펴보자. 그 다음 JPA를 통한 해결책을 알아보자.



### 1.2.1 상속

객체는 상속이라는 기능을 가지고 있지만 테이블은 상속이라는 기능이 없다.

> *그림 1.2 객체 상속 모델*

![image](https://user-images.githubusercontent.com/43429667/75353356-9ac0aa80-58ee-11ea-9c9d-e48b05c4cc4a.png)

그나마 데이터베이스 모델링에서의 슈퍼타입 서브타입 관계를 사용하면 객체 상속과 가장 유사한 형태로 설계할 수 있다.


> *그림 1.3 테이블 모델*

![image](https://user-images.githubusercontent.com/43429667/75354950-1facc380-58f1-11ea-8550-dbf8c82f1106.png)

ITEM 테이블의 DTYPE 컬럼을 사용해서 어떤 자식 테이블과 관계가 있는지 정의했다. 예를들어 DTYPE의 값이 MOVIE이면 
영화 테이블과 관계가 있다.

만약 Album 객체를 저장하려면 이 객체를 분해해서 두 SQL을 만들어야 한다.

- `INSERT INTO ITEM ...`
- `INSERT INTO ALBUM ...`

Movie 객체도 마찬가지다.

- `INSERT INTO ITEM ...`
- `INSERT INTO MOVIE ...`

JDBC API를 사용해서 이 코드를 완성하려면 부모 객체에서 부모 데이터만 꺼내서 ITEM용 INSERT SQL을 작성하고 자식 객체에서 자식 데이터만 꺼내서 ALBUM용 INSERT SQL을 작성해야 하는데 듣기만해도 작성해야 할 코드가 만만치 않다.

조회하는 것도 마찬가지로 쉬운 일이 아니다. 이런 과정이 모두 패러다임의 불일치를 해결하려고 소모하는 비용이다.
해당 객체들을 데이터베이스가 아닌 자바 컬렉션에 보관한다면 다음 같이 부모 자식이나 타입에 대한 고민 없이 그냥 쓰면 된다.

`list.add(album);`

`list.add(movie);`

`Album album = list.get(albumId);`



###  *JPA와 상속*

JPA는 상속과 관련된 패러다임의 불일치 문제를 개발자 대신 해결해준다. 개발자는 마치 컬렉션에 객체를 저장하듯이 JPA에게 
객체를 저장하면 된다.

JPA를 사용해서 Item을 상속한 Album 객체를 저장해보자. 앞서 설명한 persist() 메서드를 사용하면 된다.

`jpa.persist(album);`

​	JPA는 다음 SQL을 실행해서 객체를 ITEM, ALBUM 두 테이블에 나누어 저장한다.

​	`INSERT INTO ITEM ...`

​	`INSERT INTO ALBUM ...`

다음으로 Album 객체를 조회해보자. 앞서 설명한 find() 메서드를 사용해서 객체를 조회하면 된다.

`String albumId = "id100";`

`Album album = jpa.find(Album.class, albumId);`

  JPA는 ITEM과 ALBUM 두 테이블을 조인해서 필요한 데이터를 조회하고 그 결과를 반환한다.

  ```sql
SELECT I.*, A.*
  FROM ITEM I
  JOIN ALBUM A ON I.ITEM_ID = A.ITEM_ID
  ```



### 1.2.2 연관관계

**객체는 참조**를 사용해서 다른 객체와 연관관계를 가지고 **참조에 접근해서 연관된 객체를 조회**한다. 
반면에 **테이블은 외래 키**를 사용해서 다른 테이블과 연관관계를 가지고 **조인을 사용해서 연관된 테이블을 조회**한다.

참조를 사용하는 객체와 외래키를 사용하는 관계형 데이터베이스 사이의 패러다임 불일치는 객체지향 모델링을 거의 포기하게 
만들 정도로 어렵다. 예제를 통해 문제점을 파악해보자.

> *그림 1.4 연관관계*

![image](https://user-images.githubusercontent.com/43429667/75359734-2a1e8b80-58f8-11ea-8011-83c6efc049a7.png)

Member 객체는 Member.team 필드에 Team 객체의 참조를 보관해서 Team 객체와 관계를 맺는다. 따라서 이 참조 필드에 접근하면 Member와 연관된 Team을 조회할 수 있다.

```java
class Member {
  
  Team team;
  ...
  Team getTeam() {
    return team;
  }
  
}

class Team {
  ...
}

member.getTeam(); // member -> team 접근
```



MEMBER 테이블은 MEMBER.TEAM_ID 외래 키 컬럼을 사용해서 TEAM 테이블과 관계를 맺는다. 이 왜래 키를 사용해서
MEMBER 테이블과 TEAM 테이블을 조인하면 MEMBER 테이블과 연관된 TEAM 테이블을 조회할 수 있다.

```sql
SELECT M.*, T.*
  FROM MEMBER M
  JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID
```

객체는 참조가 있는 방향으로 조회할 수 있는 반면에 테이블은 외래 키 하나로 MEMBER JOIN TEAM도 가능하지만
반대의 경우도 가능하다.



### *객체를 테이블에 맞추어 모델링*

객체와 테이블의 차이를 알아보기 위해 객체를 단순히 테이블에 맞추어 모델링해보자.

> *예제 1.8 테이블에 맞춘 객체 모델*

```java
class Member {
  String id;   			// MEMBER_ID 컬럼 사용
  Long teamId; 			// TEAM_ID FK 컬럼 사용
  String username;  // USERNAME 컬럼 사용
  ...
}

class Team {
  
  Long id;  			// TEAM_ID PK 사용
  String name;    // NAME 컬럼 사용
	...
}
```

이렇게 테이블의 컬럼을 그대로 가져와서 모델링하면 객체를 테이블에 저장하거나 조회할 때는 편리하다.

하지만 여기서 teamId 필드에는 문제가 있다.
관계형 데이터베이스는 조인이라는 기능이 있으므로 외래 키의 값을 그대로 보관해도 되지만,
객체는 연관된 객체의 참조를 보관해야 다음처럼 참조를 통해 연관된 객체를 찾을 수 있다.

`Team team = member.getTeam();`

Member.teamId 필드처럼 TEAM_ID 외래 키까지 테이블에 맞추어 모델링한다면 Member 객체와 연관된 Team 객체를
참조를 통해서 조회할 수 없어 결국엔 객체지향의 특징을 잃어버리게 된다.



### *객체지향 모델링*

객체는 참조를 통해서 관계를 맺는다. 아래와 같이 참조를 사용하도록 모델링해야 한다.

> *예제 1.9 참조를 사용하는 객체 모델*

```java
class Member {
  String id;
  Team team; // 참조로 연관관계를 맺음
  String username;
  
  Team getTeam() {
    return team;
  }
  ...
}
```

하지만 이처럼 객체지향 모델링을 사용하면 객체를 테이블에 저장하거나 조회하기가 쉽지 않다.
Member 객체는 team 필드로 연관관계를 맺고 MEMBER 테이블은 TEAM_ID 외래 키로 연관관계를 맺기 때문이다.

**객체 모델**

- 외래키 필요 없음
- 참조만 있으면 됨

**테이블**

- 참조 필요 없음
- 외래 키만 있으면 됨

결국, 개발자가 중간에서 변환 역할을 해줘야 한다.



#### **저장**

객체를 데이터베이스에 저장하려면 team 필드를 TEAM_ID 외래 키 값으로 변환해야 한다.

TEAM_ID는 TEAM 테이블의 기본 키 이므로 member.getTeam().getId()로 구할 수 있다.

`member.getTeam().getId(); // TEAM_ID FK에 저장`  



#### 조회

조회할 때는 TEAM_ID 외래 키 값을 Member 객체의 team 참조로 변환해서 객체에 보관해야 한다.

먼저 다음 SQL과 같이 MEMBER와 TEAM을 조회하자.

```sql
SELECT M.*, T.*
  FROM MEMBER M
  JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID
```

이제 SQL의 결과로 객체를 생성하고 연관관계를 설정해서 반환하면 된다.

> *예제 1.10 개발자가 직접 연관관계 설정*

```java
public Member find(final String memberId) {
  // SQL 실행
  ...
  Member member = new Member();
  ...
  // 데이터베이스에서 조회한 회원 관련 정보를 모두 입력
  Team team = new Team();
  ...
  // 데이터베이스에서 조회한 팀 관련 정보를 모두 입력
  
  // 회원과 팀 관계 설정
  member.setTeam(team);
  return member;
}
```

위의 과정들은 전부 패러다임 불일치를 해결하려고 소모되는 비용이다. 만약 컬렉션에 회원 객체를 저장한다면 이런 비용이 전혀
들지 않는다.



### *JPA와 연관관계*

JPA는 연관관계와 관련된 패러다임의 불일치 문제를 해결해준다.

```java
member.setTeam(team);  // 회원과 팀 연관관계 설정
jpa.persist(member);   // 회원과 연관관계 함께 저장
```

개발자는 회원과 팀의 관계를 설정하고 회원 객체를 저장하면 된다. JPA는 team의 참조를 외래 키로 변환해서 적절한 INSERT SQL을 데이터베이스에 전달한다.

객체를 조회할 때 외래 키를 참조로 변환하는 일도 JPA가 처리해준다.

```java
Member member = jpa.find(Member.class, memberId);
Team team = member.getTeam();
```



지금까지 설명한 문제들은 SQL을 직접 다뤄도 열심히 코드만 작성하면 어느정도 극복할 수 있는 문제들이었다.
연관관계와 관련해서 극복하기 어려운 패러다임의 불일치 문제를 알아보자.



### 1.2.3 객체 그래프 탐색

객체에서 회원이 소속된 팀을 조회할 때는 밑의 그림처럼 참조를 사용해서 연관된 팀을 찾으면 되는데,
이것을 객체 그래프 탐색이라 한다.

`Team team = member.getTeam();`

> *그림 1.5 객체 연관관계*

![image](https://user-images.githubusercontent.com/43429667/75373145-0f0a4680-590d-11ea-892f-d029dbcbe254.png)

객체는 마음껏 객체 그래프를 탐색할 수 있어야 한다.

`member.getOrder().getOrderItem()... // 자유로운 객체 그래프 탐색`

예를 들어 MemberDao에서 member 객체를 조회할 때 

```sql
SELECT M.*, T.*
  FROM MEMBER M
  JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID
```

위의 SQL을 실행해서 회원과 팀에 대한 데이터만 조회했다면 member.getTeam()은 성공하지만 다음처럼 다른 객체 그래프는
데이터가 없으므로 탐색할 수 없다.

`member.getOrder() // null`

결국 **SQL을 직접 다루면 처음 실행하는 SQL에 따라 객체 그래프를 어디까지 탐색할 수 있는지 정해진다.**
이것은 객체지향 개발자에겐 너무 큰 제약이다. 왜냐하면 비즈니스 로직에 따라 사용하는 객체 그래프가 다른데, 언제 끊어질지 
모를 객체 그래프를 함부로 탐색할 수는 없기 때문이다.

> *예제 1.11 회원 조회 비즈니스 로직*

```java
class MemberService {
  ..
  public void process() {
    Member member = memberDao.find(memberId);
    member.getTeam(); // member->team 객체 그래프 탐색이 가능할까?
    member.getOrder().getDelivery(); // ??
  }
}
```

위 코드에서 memberDao를 통해서 member 객체를 조회했지만 이 코드만 보고는 Team, Order, Delivery 방향으로 객체
그래프 탐색을 할 수 있을지 없을지 전혀 예측할 수 없다. 결국 또 Dao를 열어서 SQL을 직접 확인해야 하는 것이다.

그렇다고 member와 연관된 모든 객체 그래프를 메모리에 올려두는 것은 현실성이 없다. 결국 memberDao에 회원을 조회
하는 메서드를 상황에 따라 여러개 만들어서 사용해야 한다.

```java
memberDao.getMember();
memberDao.getMemberWithTeam();
memberDao.getMemberWithOrderWithDelivery();
```

그렇다면 JPA는 이 문제를 어떻게 해결하는지 보자.



### JPA와 객체 그래프 탐색

JPA를 사용하면 객체 그래프를 마음껏 탐색할 수 있다.

앞에서 나왔듯이 JPA는 연관된 객체를 사용하는 시점에 적절한 SELECT SQL을 실행한다. 따라서 JPA를 사용하면 연관된 객체를
신뢰하고 조회할 수 있다. 이 기능은 실제 객체를 사용하는 시점까지 데이터베이스 조회를 미룬다고 해서 **지연 로딩**이라 한다.

JPA는 지연 로딩을 투명<sup>transparent</sup>하게 처리한다. 아래의 코드를 보면 메서드 구현 부분에 JPA에 관련된 어떤 코드도 직접 사용하지 않는다.

> *예제 1.12 투명한 엔티티*

```java
class Member {
  private Order order;
  
  public Order getOrder() {
    return order;
  }
  ...
```



> *예제 1.13 지연 로딩 사용*

```java
// 처음 조회 시점에 SELECT MEMBER SQL
Member member = jpa.find(Member.class, memberId);

Order order = member.getOrder();
order.getOrderDate(); // Order를 사용하는 시점에 SELECT ORDER SQL
```

만약 Member를 사용할 때마다 Order를 사용한다면?

- JPA는 연관된 객체를 즉시 함께 조회할지 아니면 실제 사용되는 시점에 지연해서 조회할지 간단한 설정으로 정의할 수 있다.
  



### 1.2.4 비교

데이터베이스는 기본 키의 값으로 각 로우<sup>row</sup>를 구분한다. 반면 객체는 동일성<sup>identity</sup> 비교와 동등성<sup>equality</sup> 비교 두 방법이 있다.

- 동일성 비교는 == 비교. 객체 인스턴스의 주소 값을 비교한다.
- 동등성 비교는 equals() 메서드를 사용해서 객체 내부의 값을 비교한다.

따라서 테이블의 로우를 구분하는 방법과 객체를 구분하는 방법에는 차이가 있다.

> *예제 1.14 MemberDao 코드*

```java
class MemberDao {
  
  public Member getMember(final String memberId) {
    String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ?";
    ...
    // JDBC API, SQL 실행
    return new Member(...);
  }
}
```

> *예제 1.15 조회한 회원 비교하기*

```java
String MemberId = "100";
Member member1 = memberDao.getMember(memberId);
Member member2 = memberDao.getMember(memberId);

member1 == member2 ==> false
```

member1과 member2는 같은 데이터베이스 로우에서 조회했지만, 객체 측면에선 다른 인스턴스이다.

따라서 객체의 동일성 비교에는 실패한다. 만약 객체를 컬렉션에 보관했다면 비교에 성공했을 것이다.

```java
Member member1 = list.get(0);
Member member2 = list.get(0);

member1 == member2 ==> true
```

이런 패러다임 불일치 문제를 해결하기 위해 데이터베이스의 같은 로우를 조회할 때마다 같은 인스턴스를 반환하도록 구현하는 것은 쉽지 않다.



### *JPA와 비교*

JPA는 같은 트랜잭션일 때 같은 객체가 조회되는 것을 보장한다. 그러므로 다음 코드에서 member1과 member2는 
동일성 비교에 성공한다.

```java
String MemberId = "100";
Member member1 = jpa.find(Member.class, memberId);
Member member2 = jpa.find(Member.class, memberId);

member1 == member2 ==> true
```

객체 비교하기는 분산 환경이나 트랜잭션이 다른 상황까지 고려하면 더 복잡해진다. (책을 진행하면서 자세히)



### 1.2.5 정리

- 객체 모델과 관계형 데이터베이스 모델은 지향하는 패러다임이 서로 다르다. 문제는 이 차이를 극복하려고 개발자가 너무 
  많은 시간과 코드를 소비한다는 점이다.
- 더 어려운 문제는 객체지향의 특성상 정교한 객체 모델링을 할수록 패러다임의 불일치 문제가 더 커진다는 것이다. 
- 자바 진영에서는 패러다임의 불일치 문제를 해결하기 위해 많은 노력을 기울여왔다. 그리고 그 결과물이 JPA이다.
- JPA는 패러다임의 불일치 문제를 해결해주고 정교한 객체 모델링을 유지하게 도와준다.



JPA를 문제 해결 위주로 간단히 살펴보았는데, 이제 본격적으로 JPA에 대해 알아보자.



## 1.3 JPA란 무엇인가?

JPA<sup>Java Persistence API</sup>는 자바 진영의 ORM 기술 표준이다.

> *그림 1.6 JPA*

![image](https://user-images.githubusercontent.com/43429667/75376089-c786b900-5912-11ea-828c-1f8201744fe2.png)

JPA는 애플리케이션과 JDBC 사이에서 동작한다.

ORM<sup>Object-Relational Mapping</sup>은 이름 그대로 객체와 관계형 데이터베이스를 매핑한다는 뜻이다. ORM 프레임워크는 
객체와 테이블을 매핑해서 패러다임의 불일치 문제를 개발자 대신 해결해준다. 예를 들어 객체를 데이터베이스에 저장할 때
INSERT SQL을 직접 작성하는 것이 아니라 객체를 마치 컬렉션에 저장하듯이 ORM 프레임워크에 저장하면 된다.
그러면 ORM 프레임워크가 적절한 INSERT SQL을 생성해서 데이터베이스에 객체를 저장해준다.

> *그림 1.7 JPA 저장*

![image](https://user-images.githubusercontent.com/43429667/75376869-1c76ff00-5914-11ea-9553-5e14c695d00f.png)

조회할 때도 JPA를 통해 객체를 직접 조회하면 된다.

> *그림 1.8 JPA 조회*

![image](https://user-images.githubusercontent.com/43429667/75377121-8a232b00-5914-11ea-8319-53ae68cd5ade.png)



ORM 프레임워크는 단순히 SQL을 개발자 대신 생성해서 데이터베이스에 전달해주는 것뿐만 아니라 앞서 이야기한 다양한 
패러다임의 불일치 문제들도 해결해주어 객체 측면에서는 정교한 객체 모델링을 할 수 있고 관계형 데이터베이스는 데이터베이스에 맞도록 모델링하면 된다. 
**덕분에 개발자는 데이터 중심인 관계형 데이터베이스를 사용해도 객체지향 애플리케이션에 집중할 수 있다.**



### 1.3.1 JPA 소개

과거의 자바 진영에서는 EJB<sup>Enterprise Java Beans</sup>라는 기술 표준을 만들었는데 그 안에는 엔티티 빈이라는 ORM 기술도 포함되어 있었다. 하지만 너무 복잡하고 기술 성숙도도 떨어졌으며 자바 엔터프라이즈 애플리케이션 서버에서만 동작했다. 
이때 하이버네이트<sup>hibernate.org</sup>라는 오픈소스 ORM 프레임워크가 등장했는데 EJB의 ORM 기술과 비교해서 가볍고 기술 성숙도도 높았다. 또한 자바 엔터프라이즈 애플리케이션 서버 없이도 동작했기때문에 많은 개발자들이 사용하기 시작했다.

결국 EJB 3.0에서 하이버네이트를 기반으로 새로운 자바 ORM 기술 표준이 만들어졌는데 이것이 바로 **JPA**이다.



> *그림 1.9 JPA 표준 인터페이스와 구현체*

![image](https://user-images.githubusercontent.com/43429667/75411693-73f08b80-5963-11ea-82bc-fcc8fafb29b6.png)

**JPA는 자바 ORM 기술에 대한 API 표준 명세다.** 쉽게 말해서 인터페이스를 모아둔 것이다. 따라서 JPA를 사용하려면 JPA를 구현한 ORM 프레임워크를 선택해야 한다. 하이버네이트, EclipseLink, DataNucleus 중 **하이버네이트**가 가장 대중적이다.

JPA라는 표준 덕분에 특정 구현 기술에 대한 의존도를 줄일 수 있고 다른 구현 기술로 손쉽게 이동할 수 있다는 장점이 있다.

JPA 버전별 특징을 간략하게 정리하자면,

- JPA 1.0(JSR 220) 2006년: 초기 버전이다. 복합 키와 연관관계 기능이 부족했다.
- JPA 2.0(JSR 317) 2009년: 대부분의 ORM 기능을 포함하고 JPA Criteria가 추가되었다.
- JPA 2.1(JSR 338) 2013년: 스토어드 프로시저 접근, 컨버터<sup>Converter</sup>, 엔티티 그래프 기능이 추가되었다.



### 1.3.2 왜 JPA를 사용해야 하는가?

- **생산성**

  JPA를 사용하면 컬렉션에 객체를 저장하듯이 JPA에게 저장할 객체를 전달하면 된다.
  지루하고 반복적인 일은 JPA가 대신 처리해준다.

  `jpa.persist(member);`

  `Member member = jpa.find(memberId);`

  더 나아가 JPA에는 `CREATE TABLE`같은 DDL 문을 자동으로 생성해주는 기능도 있다. 
  이런 기능들을 사용하면 데이터베이스 설계 중심의 패러다임을 객체 설계 중심으로 역전시킬 수 있다.



- **유지보수**

  SQL을 직접 다루면 엔티티에 필드 하나만 추가해도 관련된 코드들을 모두 변경해야 했다.
  반면에 JPA는 이런 과정을 대신 처리해주므로 필드를 추가하거나 삭제해도 수정해야 할 코드가 줄어든다.

  또한 패러다임 불일치 문제를 해결해주어 객체지향 언어가 가진 장점들을 활용하여 
  유지보수하기 좋은 도메인 모델을 편리하게 설계할 수 있다.



- **패러다임의 불일치 해결**

  JPA는 상속, 연관관계, 객체 그래프 탐색, 비교하기와 같은 패러다임의 불일치 문제를 해결해준다.



- **성능**

  JPA는 애플리케이션과 데이터베이스 사이에서 다양한 성능 최적화 기회를 제공한다.
  JPA는 애플리케이션과 데이터베이스 사이에서 동작하므로 최적화 관점에서 시도해볼 수 있는 것들이 많다.

  `String memberId = "helloId";`

  `Member member1 = jpa.find(memberId);`

  `Meber member2 = jpa.find(memberId);`

  JDBC를 사용해서 해당 코드를 직접 작성했다면 회원을 조회할 때마다 SELECT SQL을 사용해서 데이터베이스와
  두 번 통신했을 것이다. JPA는 한 번만 데이터베이스에 전달하고 두 번째부터는 조회한 객체를 재사용한다.



- **데이터 접근 추상화와 벤더 독립성**

  관계형 데이터베이스는 같은 기능도 벤더마다 사용법이 다른 경우가 많다. 애플리케이션은 처음 선택한 데이터베이스 기술에
  종속되고 다른 데이터베이스로 변경하기는 매우 어렵다.

  JPA는 애플리케이션과 데이터베이스 사이에 **추상화된 데이터 접근 계층**을 제공해서 특정 데이터베이스 기술에 종속되지 않도록 한다. 만약 데이터베이스를 변경하면 JPA에게 다른 데이터베이스를 사용한다고 알려주기만 하면 된다.



- **표준**

  JPA는 자바 진영의 ORM 표준이다. 앞서 이야기 했듯이 표준을 사용하면 다른 구현 기술로 손쉽게 변경할 수 있다.



## 정리

지금까지 SQL을 직접 다룰 때 발생하는 다양한 문제와 객체지향 언어와 관계형 데이터베이스 사이의 패러다임 불일치 문제를
설명했다. 그리고 JPA가 각 문제를 어떻게 해결하는지 알아보았다. JPA에 관한 자세한 내용은 차근차근 살펴보기로 하고, 우선은
다음 장부터 테이블 하나를 등록/수정/삭제/조회하는 간단한 JPA 애플리케이션을 만들어보자.

