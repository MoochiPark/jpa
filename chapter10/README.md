# Chapter 10. 객체지향 쿼리 언어

> ***이장의 내용***
>
> - *객체지향 쿼리 소개*
> - *JPQL*
> - ~~*Criteria*~~
> - *QueryDSL*
> - 네이티브 SQL
> - 객체지향 쿼리 심화





## 10.1 객체지향 쿼리 소개

- 테이블이 아닌 객체를 대상으로 검색하는 객체지향 쿼리다.
- SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다.





## 10.2 JPQL

- JPQL<sup>Java Persistence Query Language</sup>은 엔티티 객체를 조회하는 객체지향 쿼리다.
- JPQL은 결국 SQL로 변환된다.

> Criteria나 QueryDSL도 결국 JPQL을 만들어주는 빌더 역할을 할 뿐이므로 JPQL을 잘 알아야 한다.



### 10.2.1 기본 문법과 쿼리 API

JPQL도 SQL과 비슷하게 SELECT, UPDATE, DELETE 문을 사용할  수 있다.
엔티티를 저장할 때는 em.persist() 메서드를 사용하면 되므로 INSERT 문은 없다.



#### SELECT 문

`SELECT m FROM Member AS m WHERE m.username = 'Hello' `

- **대소문자 구분**

  JPQL 키워드를 제외한 엔티티와 속성은 대소문자를 구분한다.

- **엔티티 이름**

  Member는 클래스 명이 아니라 엔티티 명이다. 기본값인 클래스 명을 엔티티 명으로 사용하는 것을 추천한다.

- **별칭은 필수**

  Member AS m처럼 JPQL은 별칭을 필수로 사용해야 한다. AS는 생략할 수 있다.



#### TypedQuery, Query

작성한 JPQL을 실행하려면 쿼리 객체를 만들어야 한다.

반환할 타입을 명확하게 지정할 수 있으면 TypedQuery 객체를 사용하고,
반환 타입을 명확하게 지정할 수 없으면 Query 객체를 사용하면 된다.



> *TypeQuery 사용*

```java
TypedQuery<Member> query =
  em.createQuery("SELECT m FROM Member m", Member.class);

List<Member> resultList = query.getResultList();
```

`em.createQuery()`의 두 번째 파라미터에 반환할 타입을 지정하면 TypeQuery를 반환하고 지정하지 않으면 Query를 반환한다. 여기선 조회 대상이 Member 엔티티이므로 대상 타입이 명확하다.

> *Query 사용*

```java
Query query = 
  em.createQuery("SELECT m.username, m.age from Member m");

List resultList = query.getResultList();
```

여기선 조회 대상이 String 타입과 Integer 타입이므로 조회 대상 타입이 명확하지 않다.
Query 객체는 SELECT 절의 조회 대상이 둘 이상이면 Object[]를 반환하고 하나면 Object를 반환한다.

일반적으로 타입을 변환할 필요가 없는 TypedQuery를 사용하는 것이 더 편리하다.



#### 결과 조회

다음 메서드들을 호출하면 실제 쿼리를 실행해서 데이터베이스를 조회한다.

- query.getResultList(): 결과를 리스트로 반환한다. 결과가 없으면 빈 컬렉션을 반환한다.
- query.getSingleResult(): 결과가 정확히 하나일 때 사용한다.
  - 결과가 없으면 NoResultException 발생
  - 1개보다 많으면 NonUniqueResultException 발생





### 10.2.2 파라미터 바인딩

JDBC는 위치 기준 파라미터 바인딩만 지원하지만 JPQL은 이름 기준 파라미터 바인딩도 지원한다.

- **이름 기준 파라미터**

  파라미터를 이름으로 구분하는 방법. 앞에 `:`를 사용한다.

  ```java
  String usernameParam = "User1";
  
  TypedQuery<Member> query = 
    em.createQuery("select m from Member m where m.username = :username",
                  Member.class);
  
  query.setParameter("username", usernameParam);
  List<Member> resultList = query.getResultList();
  ```

  추가로 JPQL API는 대부분 메서드 체인 방식으로 설계되어 다음과 같이 작성할 수도 있다.

  ```java
  List<Member> members = 
    em.createQuery("select m from Member m where m.username = :username",
                  Member.class)
    .setParameter("username", usernameParam)
    .getResultList();
  ```

  

- **위치 기준 파라미터**

  `?` 다음에 위치 값을 주면 된다. 위치 값은 1부터 시작한다.

  ```java
  List<Member> members =
    em.createQuery("select m from Member m where m.username = ?1", Member.class)
    .setParameter(1, usernameParam)
    .getResultList();
  ```



이름 기준 파라미터 바인딩 방식을 사용하는 것이 더 명확한 방법이다.



### 10.2.3 프로젝션

SELECT 절에 조회할 대상을 지정하는 것을 프로젝션이라 한다.



- **엔티티 프로젝션**

  `SELECT m FROM Member m`

  `SELECT m.team FROM Member m`

  조회한 엔티티는 영속성 컨텍스트에서 관리된다.



- **임베디드 타입 프로젝션**

  임베디드 타입은 엔티티와 거의 비슷하게 사용되지만 조회의 시작점이 될 수 없다는 제약이 있다.

  ```java
  String query = "SELECT o.address FROM Order o";
  List<Address> addresses = em.createQuery(query, Address.class)
    													.getResultList();
  ```

  임베디드 타입은 엔티티 타입이 아닌 값 타입이다. 따라서 영속성 컨텍스트에서 관리되지 않는다.



- **스칼라 타입 프로젝션**

  숫자, 문자, 날짜와 같은 기본 데이터 타입을 스칼라 타입이라 한다. 통계 쿼리도 주로 스칼라 타입으로 조회한다.



- **NEW 명령어**

  ```java
  TypedQuery<UserDTO> query =
    em.createQuery("SELECT new jpabook.jpql.UserDTO(m.username, m.age) FROM Member m",
                   UserDTO.class);
  List<UserDTO> resultList = query.getResultList();
  ```

  NEW 명령어를 사용한 클래스로 지루한 객체 변환 작업을 줄일 수 있다. 사용 시 다음 2가지를 주의해야 한다.

  1. 패키지 명을 포함한 전체 클래스 명을 입력해야 한다.
  2. 순서와 타입이 일치하는 생성자가 필요하다.



### 10.2.4 페이징 API

페이징 처리용 SQL은 지루하고 반복적인데다가 데이터베이스마다 처리하는 SQL이 다르다.

JPA는 페이징을 두 API로 추상화했다.

- setFirstResult(int startPosition): 조회 시작 위치 (0부터 시작)
- setMaxResults(int maxResult): 조회할 데이터 수

```java
List<Member> members = 
  em.createQuery("SELECT m FROM Member m ORDER BY m.username DESC",
                Member.class)
  .setFirstResult(10)
  .setMaxResults(20)
  .getResultList();
```



### 10.2.5 집합과 정렬

```sql
select
  count(m),
  sum(m.age),
  avg(m.age),
  max(m.age),
  min(m.age)
from Member m
```



#### 참고 사항

- DISTINCT를 집합 함수 안에 사용해서 중복된 값을 제거하고 나서 집합을 구할 수 있다.

  `select count(distinct m.age) from Member m`

- DISTINCT를 COUNT에서 사용할 때 임베디드 타입은 지원하지 않는다.





#### GROUP BY, HAVING

다음 코드는 그룹별 통계 데이터 중에서 평균나이가 10살 이상인 그룹을 조회한다.

```sql
select t.name, count(m.age), sum(m.age), avg(m.age), max(m.age), min(m.age)
from Member m LEFT JOIN m.team t
GROUP BY t.name
HAVING avg(m.age) >= 10
```





### 10.2.6 JPQL 조인

JPQL도 조인을 지원하는데 SQL 조인과 기능은 같고 문법만 약간 다르다.



#### 내부 조인

INNER JOIN을 사용한다. INNER는 생략 가능하다.

```java
String teamName = "팀A";
String query = "SELECT m FROM Member m INNER JOIN m.team t "
  + "WHERE t.name = :teamName";

List<Member> members = em.createQuery(query, Member.class)
  .setParameter("teamName", teamName)
  .getResultList();
```

JPQL 조인의 가장 큰 특징은 **연관 필드**를 사용한다는 것이다. 

- `FROM Member m`: 회원을 선택하고 m이라는 별칭을 주었다.
- `Member m JOIN m.team t`: 회원이 가지고 있는 연관 필드로 팀과 조인한다. 조인한 팀에는 t라는 별칭을 주었다.

만약 조인한 두 개의 엔티티를 조회하려면 다음과 같이 JPQL을 작성하면 된다.

```sql
select m, t
from Member m join m.team t
```



#### 외부 조인

```sql
select m
from Member m left [outer] join m.team t 
```

outer는 생략 가능해서 보통 left 조인으로 사용한다.



#### 컬렉션 조인

일대다, 다대다 관계처럼 컬렉션을 사용하는 곳에 조인하는 것을 컬렉션 조인이라 한다.

팀 → 회원은 일대다 조인이면서 컬렉션 값 연관 필드<sup>t.members</sup>를 사용한다.

`select t, m from Team t left join t.members m`

팀과 팀이 보유한 회원 목록을 컬렉션 값 연관 필드로 외부 조인했다.



#### 세타 조인

> 카테시안곱에서 선택연산이 비교 연산자가 사용되는 것. 특별히 = 연산이 사용되는 경우를 동등<sup>equi</sup> 조인이라 한다.

WHERE 절을 사용해서 세타 조인을 할 수 있다. 세타조인은 내부 조인만 지원한다.
세타 조인을 사용하면 전혀 관계 없는 엔티티도 조인할 수 있다.

```sql
// JPQL
select count(m) from Member m, Team t
where m.username = t.name

// SQL
SELECT COUNT(M.ID)
FROM MEMBER M CROSS JOIN TEAM T
WHERE M.USERNAME=T.NAME
```



#### JOIN ON 절<sup>JPA 2.1</sup>

ON 절을 사용하면 조인 대상을 필터링하고 조인할 수 있다. 참고로 내부 조인의 결과는 ON 절 = WHERE 절이므로
보통 외부 조인에서만 사용된다.

```sql
// JPQL
select m, t from Member m
left join m.team t on t.name = 'A'
  
// SQL
select m.*, t.* from Member m
left join Team t on m.team_id and t.name='A'
```



### 10.2.7 페치 조인

페치<sup>fetch</sup> 조인은 SQL에서 이야기하는 조인의 종류가 아니라 JPQL에서 성능 최적화를 위해 제공하는 기능으로
연관된 엔티티나 컬렉션을 한 번에 같이 조회하는 기능이다. join fetch 명령어로 사용할 수 있다.



#### 엔티티 페치 조인

회원 엔티티를 조회하면서 연관된 팀 엔티티도 함께 조회하는 JPQL을 보자.

```sql
select m
from Member m join fetch m.team
```

여기선 회원과 팀을 함께 조회한다. 페치 조인은 별칭을 사용할 수 없지만 하이버네이트 구현체에선 사용할 수 있도록 구현되었다.

> *페치 조인 사용*

```java
String jpql = "select m from Member m join fetch m.team";

List<Member> members = em.createQuery(jpql, Member.class)
  .getResultList();
```

> *실행된 SQL*

```sql
select m.*, t.*
from member m
inner join team t on m.team_id=t.id
```

엔티티페치 조인에서 회원 엔티티만 선택했는데 SQL을 보면 회원과 연관된 팀도 함께 조회된 것을 확인할 수 있다.

회원과 팀을 지연 로딩으로 설정해도 회원을 조회할 때 페치 조인으로 함께 조회했으므로 연관된 팀 엔티티는
프록시가 아닌 실제 엔티티다. 따라서 연관된 팀을 사용해도 지연 로딩이 발생하지 않는다.
또한 실제 엔티티이므로 회원 엔티티가 준영속 상태가 되어도 연관된 팀을 조회할 수 있다.



#### 컬렉션 페치 조인

> *JPQL*

```sql
select t
from Team t join fetch t.members
where t.name = '팀A'
```

> *실행된 SQL*

```sql
SELECT T.*, M.*
FROM TEAM T
INNER JOIN MEMBER M ON T.ID=M.TEAM_ID
WHERE T.NAME='팀A'
```

마찬가지로 팀만 선택했는데 팀과 연관된 회원도 함께 조회한 것을 볼 수 있다.

```java
teamname = 팀A, team = Team@0x100
->username = 회원1, member = Member@0x200
->username = 회원2, member = Member@0x300
teamname = 팀A, team = Team@0x100
->username = 회원1, member = Member@0x200
->username = 회원2, member = Member@0x300
```



TEAM 테이블에서 '팀A'는 하나지만 MEMBER 테이블과 조인하면서 결과가 증가<sup>ex) 팀A에 속한 회원이 2명</sup>해서 같은 '팀A'가 2건
조회되었다. 따라서 중복 제거가 필요하다.



#### 페치 조인과 DISTINCT

JPQL의 DISTINCT 명령어는 SQL에 DISTINCT를 추가하고 애플리케이션에서 한 번 더 중복을 제거한다.

```sql
select distinct t
from Team t join fetch t.members
where t.name = '팀A'
```

`select distinct t`는 팀 엔티티의 중복을 제거하라는 것이다. 따라서 이제 '팀A'는 하나만 조회된다. 

```java
teamname = 팀A, team = Team@0x100
->username = 회원1, member = Member@0x200
->username = 회원2, member = Member@0x300
```



#### 페치 조인과 일반 조인의 차이

JPQL은 결과를 반환할 때 연관관계까지 고려하지 않는다. 단지 SELECT 절에 지정한 엔티티만 조회할 뿐이다.
만약 회원 컬렉션을 지연 로딩으로 설정하면 프록시나 아직 초기화하지 않은 컬렉션 래퍼를 반환한다.
즉시 로딩으로 설정하면 회원 컬렉션을 즉시 로딩하기 위해 쿼리를 한 번 더 실행한다.

반면에 페치 조인을 사용하면 연관된 엔티티도 같이 조회한다.



#### 페치 조인의 특징과 한계

페치 조인을 사용하면  SQL 한 번으로 연관된 엔티티들을 조회할 수 있어서 성능을 최적화할 수 있다.

페치 조인은 글로벌 로딩 전략보다 우선한다. 
예를 들어 지연 로딩으로 설정해도 JPQL에서 페치 조인을 사용하면 페치 조인을 적용해서 함께 조회한다.

**최적화를 위해 글로벌 로딩 전략을 즉시 로딩으로 설정하면 애플리케이션 전체에서 항상 즉시 로딩이 일어난다.**
**일부는 빠를 수 있어도 전체로 보면 자주 사용하지 않는 엔티티를 자주 로딩하므로 오히려 성능에 악영향을 미칠 수 있다.**
**따라서 되도록 지연 로딩을 사용하고 최적화가 필요하면 페치 조인을 적용하는 것이 효과적이다.**

페치 조인은 다음과 같은 한계가 있다.

- **페치 조인 대상에는 별칭을 줄 수 없다.**

- **둘 이상의 컬렉션을 페치할 수 없다.**
- **컬렉션을 페치 조인하면 페이징 API를 사용할 수 없다.**





### 10.2.8 경로 표현식

경로 표현식이란 쉽게 표현해서 `.`을 찍어 객체 그래프를 탐색하는 것이다.



#### 용어 정리

- **상태 필드**<sup>state field</sup>: 단순히 값을 저장하기 위한 필드
- **연관 필드**<sup>association field</sup>: 연관관계를 위한 필드, 임베디드 타입 포함
  - **단일 값 연관 필드**: @ManyToOne, @OneToOne, 대상이 엔티티
  - **컬렉션 값 연관 필드**: @OneToMany, @ManyToMany, 대상이 컬렉션



> *상태 필드 연관 필드 예제*

```java
@Entity
public class Member {
  
  @Id @GeneratedValue
  private Long id;
  
  @Column(name = "name")
  private String username; // 상태 필드
  private Integer age;     // 상태 필드
  
  @ManyToOne(..)
  private Team team;       // 단일 값 연관 필드
  
  @OneToMany(..)
  private List<Order> orders; // 컬렉션 값 연관 필드
  
}
```



#### 경로 표현식과 특징

- **상태 필드 경로**: 경로 탐색의 끝이다. 더는 탐색할 수 없다.

- **단일 값 연관 경로**: 묵시적으로 내부 조인이 일어난다. 계속 탐색할 수 있다.

- **컬렉션 값 연관 경로**: 묵시적으로 내부 조인이 일어난다. 더는 탐색할 수 없다. 

  단 FROM 절에서 조인을 통해 별칭을 얻으면 탐색할 수 있다.



- **명시적 조인**: JOIN을 직접 적어주는 것

- **묵시적 조인**: 경로 표현식에 의해 조인이 일어나는 것, 내부 조인만 할 수 있다.

  `select m.team from Member m`



- **컬렉션 값 연관 경로 탐색**

  ```sql
  select t.members from Team t // 성공
  select t.members.username from Team t // 실패
  
  select m.username from Team t join t.members m // 이처럼 해야함
  select t.members.size from Team t // size 사용 가능 COUNT로 적절히 변환 됨
  ```



- #### **묵시적 조인 시 주의사항**

  - **항상 내부 조인이다.**
  - 컬렉션은 경로 탐색의 끝이다. 탐색하려면 명시적으로 조인을 해서 별칭을 얻어야 한다.



​		조인이 성능상 차지하는 부분은 아주 크다. 따라서 성능이 중요하면 분석하기 쉽도록 명시적 조인을 사용하자.





### 10.2.9 서브 쿼리

JPQL에선 서브 쿼리를 WHERE, HAVING 절에서만 사용할 수 있고 하이버네이트는 추가적으로 SELECT 절의 서브 쿼리도 허용한다.

> 나이가 평균보다 많은 회원 예제

```sql
select m from Member m
where m.age > (select avg(m2.age) from Member m2)
```





#### 서브 쿼리 함수

- [NOT] EXISTS : 서브 쿼리에 결과가 존재하면 참
- { ALL | ANY | SOME } : 
  - ALL: 조건을 모두 만족하면 참
  - ANY 혹은 SOME: 조건을 하나라도 만족하면 참
- [NOT] IN : 서브 쿼리의 결과 중 하나라도 같은 것이 있으면 참





### 10.2.10 조건식

#### 

#### 타입 표현

| 종류        | 설명                                                         | 예제                                                 |
| ----------- | ------------------------------------------------------------ | ---------------------------------------------------- |
| 문자        |                                                              | 'HELLO'<br />'She''s'                                |
| 숫자        |                                                              | 10L<br />10D<br />10F                                |
| 날짜        | DATE {d 'yyyy-mm-dd'}<br />TIME {t 'hh-mm-ss'}<br />DATETIME {ts 'yyyy-mm-dd hh:mm:ss.f'} | {d '2012-03-24'}<br />{ts '2012-03-24 10-11-11.123'} |
| Boolean     | TRUE, FALSE                                                  |                                                      |
| Enum        | 패키지명을 포함한 전체 이름을 사용해야 한다.                 | jpabook.MemberType.Admin                             |
| 엔티티 타입 | 엔티티의 타입을 표현한다. 주로 상속과 관련해서 사용한다.     | TYPE(m) = Member                                     |



#### Between

- X [NOT] BETWEEN A AND B: X는 A ~ B 사이의 값이면 참 (A, B값 포함)



#### 컬렉션 식

- **빈 컬렉션 비교 식**

  - {컬렉션 값 연관 경로} IS [NOT] EMPTY

- **컬렉션의 멤버 식**: 엔티티나 값이 컬렉션에 포함되어 있으면 참

  - {엔티티나 값} [NOT] MEMBER [OF] {컬렉션 값 연관 경로}

    `select t from Team t where :memberParam member of t.members`



#### CASE 식

- 기본 CASE
- 심플 CASE
- COALESCE
- NULLIF



### 10.2.11 다형성 쿼리

### 10.2.12 사용자 정의 함수 호출<sup>JPA 2.1</sup>

.

.

.



### 10.2.15 Named 쿼리: 정적 쿼리

- **동적 쿼리**: em.createQuery("select ..") 처럼 JPQL을 문자로 완성해서 직접 넘기는 것을 동적 쿼리라 한다.
  런타임에 특정 조건에 따라 JPQL을 동적으로 구성할 수 있다.
- **정적 쿼리**: 미리 정의한 쿼리에 이름을 부여해서 필요할 떄 사용할 수 있는데 이 것을 Named 쿼리라 한다.
  Named 쿼리는 한 번 정의하면 변경할 수 없는 정적인 쿼리다.



Named 쿼리는 애플리케이션 로딩 시점에 JPQL 문법을 체크하고 미리 파싱해둔다. 
따라서 오류를 빨리 확인할 수 있고, 사용하는 시점에는 파싱된 결과를 재사용하므로 성능상 이점도 있다.

Named 쿼리는 @NamedQuery 애노테이션을 사용해서 자바 코드에 작성하거나 XML 문서에 작성할 수 있다.



#### 애노테이션에 정의

> *정의*

```java
@Entity
@NamedQuery(
  name = "Member.findByUsername",
  query = "select m from Member m where m.username = :username")
public class Member {
  ...
}
```



> *사용*

```java
List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
  .setParameter("username", "회원1")
  .getResultList();
```

하나의 엔티티에 2개 이상의 Named 쿼리를 정의하려면 @NamedQueries를 사용하면 된다.





#### Named 쿼리를 XML에 정의

JPA에서 애노테이션으로 작성할 수 있는 것은 XML로도 작성할 수 있다.

만약 XML과 애노테이션에서 같은 설정이 있으면 **XML이 우선권을 가진다.**





## 10.4 QueryDSL

Criteria는 문자가 아닌 코드로 JPQL을 작성하므로 문법 오류를 컴파일 단계에서 잡을 수 있고 자동완성 기능의 도움을 받을 수 있는 등 여러 가지 장점이 있지만 너무 복잡하고 어렵다. QueryDSL도 Criteria처럼 JPQL 빌더 역할을 하므로 대체할 수 있다.


메이븐 설정을 마치고 콘솔에서 `mvn compile`을 입력하면 `outputDirectory`에 지정한 target/generated-sources 위치에 Q로 시작하는 쿼리 타입들이 생성된다.

### 시작

```java
public void queryDSL() {
  EntityManager em = emf.crateEntityManager();
  
  JPAQuery query = new JPAQuery(em);
  QMember qMember = new QMember("m"); // 생성되는 JPQL의 별칭 m
  
  List<Member> members =
    query.from(qMember)
    .where(qMember.name.eq("회원1"))
    .orderBy(qMember.name.desc())
    .list(qMember);
}
```

- QueryDSL을 사용하려면 우선 JPAQuery 객체를 생성해야 하는데 이때 엔티티 매니저를 생성자에 넘겨준다.
- 다음으로 사용할 쿼리 타입을 생성하는데 생성자에는 별칭을 주면 된다. 이 별칭을 JPQL에서 별칭으로 사용한다.
- 그 다음 코드들은 보기만해도 쉽게 이해할 수 있을 것이다.



#### 기본 Q 생성

쿼리 타입은 사용하기 편하도록 기본 인스턴스를 보관하고 있다.

```java
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAccount extends EntityPathBase<Account> {

    public static final QAccount account = new QAccount("account");
    
    ...
```

하지만 같은 엔티티끼리 조인하거나 서브쿼리에 사용하면 같은 별칭이 되므로 이때는 별칭을 직접 지정해서 사용해야 한다.





### 프로젝션과 결과 반환

select 절에 조회 대상을 지정하는 것을 프로젝션이라 했다.



#### 프로젝션 대상이 하나

```javascript
QItem item = QItem.item;
List<String> result = query.from(item).list(item.name);
```

프로젝션 대상이 하나면 해당 타입으로 반환한다.



#### 여러 컬럼 반환과 튜플

프로젝션 대상으로 여러 필드를 선택하면 QueryDSL은 기본으로 Tuple이라는 Map과 비슷한 내부 타입을 사용한다.
조회 결과는 `tuple.get()` 메서드에 조회한 쿼리 타입을 지정하면 된다.

```java
QItem item = QItem.item;

List<Tuple> result = query.from(item).list(item.name, item.price);

for (Tuple tuple : result) {
  System.out.println("name = " + tuple.get(item.name));
  System.out.println("price = " + tuple.get(item.price));
}
```



#### 빈 생성<sup>DTO</sup>

쿼리 결과를 엔티티가 아닌 특정 객체로 받고 싶으면 빈 생성 기능을 사용한다. (더 자주 사용됨)
QueryDSL은 객체를 생성하는 다양한 방법을 제공한다.

- 프로퍼티 접근
- 필드 직접 접근
- 생성자 사용



원하는 방법을 지정하기 위해 Projections를 사용하면 된다. 

> *예제 ItemDTO*

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
  
  private String username;
  private int price;
  
}
```



> *프로퍼티 접근*<sup>Setter</sup>

```java
QItem item = QItem.item;
List<ItemDTO> result = query.from(item).list(
  Projections.bean(ItemDTO.class, item.name.as("username"), item.price));
```

`bean()` 메서드는 Setter를 사용해서 값을 채운다. `as`를 사용해서 쿼리 결과인 name을 ItemDTO가 가지고 있는 프로퍼티인 
username으로 변경했다. 이처럼 쿼리 결과와 매핑할 프로퍼티 이름이 다르면  `as`를 사용해서 별칭을 주면 된다.



> 필드 직접 접근

```java
QItem item = QItem.item;
List<ItemDTO> result = query.from(item).list(
  Projections.fields(ItemDTO.class, item.name.as("username"), item.price));
```

`fields()` 메서드는 필드에 직접 접근해서 값을 채워준다. 필드를 `private`로 설정해도 동작한다.



> *생성자 사용*

```java
QItem item = QItem.item;
List<ItemDTO> result = query.from(item).list(
  Projections.constructor(ItemDTO.class, item.name, item.price));
```

지정한 프로젝션과 파라미터 순서가 같은 생성자가 필요하다.



#### DISTINCT

`query.distinct().from(item)....`



### 수정, 삭제 배치 쿼리

QueryDSL도 수정, 삭제같은 배치 쿼리를 지원한다. JPQL 배치 쿼리와 같이 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리한다는 점을 유의하자.

> *수정 배치 쿼리*

```java
QItem item = QItem.item;
JPAUpdateClause updateClause = new JPAUpdateClause(em, item);
long count = updateClause.where(item.name.eq("시골 개발자의 JPA 책"))
  .set(item.price, item.price.add(100))
  .execute();
```

> *삭제 배치 쿼리*

```java
QItem item = QItem.item;
JPADeleteClause deleteClause = new JPADeleteClause(em, item);
long count = deleteClause.where(item.name.eq("시골개발자의 JPA 책"))
  .execute()
```





### 동적 쿼리

BooleanBuilder를 사용하면 특정 조건에 따른 동적 쿼리를 편리하게 생성할 수 있다.

```java
SearchParam param = new SearchParam();
param.setName("시골개발자");
param.setPrice(10000);

QItem item = QItem.item;

BooleanBuilder builder = new BooleanBuilder();
if (StringUtils.hasText(param.getName())) {
  builder.and(item.name.contains(param.getName()));
}
if (param.getPrice() != null) {
  builder.and(item.price.gt(param.getPrice()));
}
List<Item> result = query.from(item)
  .where(builder)
  .list(item);
```



### 메서드 위임

메서드 위임<sup>Delegate Methods</sup> 기능을 사용하면 쿼리 타입에 검색 조건을 직접 정의할 수 있다.

> *검색 조건 정의*

```java
public class ItemExpression {
  
  @QueryDelegate(Item.class)
  public static BooleanExpression isExpensive(QItem item, Integer price) {
    return item.price.gt(price);
  }
  
}
```

메서드 위임 기능을 사용하기 위해 우선 static 메서드를 만들고 QueryDelegate 애노테이션으로 이 기능을 적용할 엔티티를
지정한다. 메서드의 첫 번째 파라미터에는 대상 엔티티의 쿼리 타입을 지정하고 나머지는 필요한 파라미터를 정의한다.

> *쿼리 타입에 생성된 결과*

```java
public class QItem extends EntityPathBase<Item> {
  ...
  public com.mysema.query.types.expr.BooleanExpression isExpensive(Integer price) {
    return ItemExpression.isExpensive(this, price);
  }
  
}
```

이제 메서드 위임 기능을 사용해보자.

`query.from(item).where(item.isExpensive(30000)).list(item);`

필요하다면 String, Date 같은 자바 기본 내장 타입에도 메서드 위임 기능을 사용할 수 있다.





## 10.5 네이티브 SQL

JPQL은 표준 SQL이 지원하는 대부분의 문법과 SQL 함수들을 지원하지만 특정 데이터베이스 종속적 기능은 지원하지 않는다.

JPQL에서 특정 데이터베이스에 종속적인 기능을 지원하는 방법은 다음과 같다.

- **특정 데이터베이스만 사용하는 함수**
  - JPQL에서 네이티브 SQL 함수를 호출할 수 있다 <sup>JPA 2.1</sup>.
  - 하이버네이트는 데이터베이스 방언에 각 데이터베이스에 종속적인 함수들을 정의해두었다.
- **특정 데이터베이스만 지원하는  SQL 쿼리 힌트**
  - 하이버네이트를 포함한 몇몇 JPA 구현체들이 지원한다.
- **인라인 뷰, UNION, INTERSECT**
  - 하이버네이트는 지원하지 않지만 일부 JPA 구현체들이 지원한다.
- **스토어드 프로시저**
  - JPQL에서 스토어드 프로시저를 호출할 수 있다 <sup>JPA 2.1</sup>



JDBC API와의 차이점이라면 **네이티브 SQL은 JPA가 지원하는 영속성 컨텍스트의 기능을 그대로 사용**할 수 있다.



네이티브 쿼리 API는 다음 3가지가 있다.



- **결과 타입 정의**

  `public Query createNativeQuery(String sqlString, Class resultClass);`



- **결과 타입을 정의할 수 없을 때**

  `public Query createNativeQuery(String sqlString);`



- **결과 매핑 사용**

  `public Query createNativeQuery(String sqlString, String resultSetMapping);`



네이티브 SQL도 JPQL을 사용할 때와 마찬가지로 Named 쿼리를 사용할 때 Query.TypeQuery를 반환한다.
따라서 JPQL API를 그대로 사용할 수 있다. 





## 10.6 객체지향 쿼리 심화



### 10.6.1 벌크 연산

수백개 이상의 엔티티를 하나씩 처리하기에는 시간이 너무 오래걸린다. 이럴 때 여러 건을 한 번에 수정하거나 삭제하는 연산이다.

> *UPDATE 벌크 연산*

```java
String qlString =
  "update Product p " +
  "set p.price = p.price * 1.1 " +
  "where p.stickAmount < : stockAmount";

int resultCount = em.createQuery(qlString)
  .setParameter("stockAmount", 10)
  .executeUpdate();
```

> *DELETE 벌크 연산*

```java
String qlString =
  "delete from Product p" +
  "where p.price < :price";

int resultCount = em.createQuery(qlString)
  .setParameter("price", 100)
  .executeUpdate();
```

JPA 표준은 아니지만 하이버네이트는 INSERT 벌크 연산도 지원한다.
다음 코드는 100원 미만의 모든 상품을 ProductTemp에 저장한다.

> *INSERT 벌크 연산*

```java
String qlString = 
  "insert into ProductTemp(id, name, price, stockAmount) " +
  "select p.id, p.name, p.price, p.stockAmount from Product p " +
  "where p.price < :price";

int resultCount = em.createQuery(qlString)
  .setParameter("price", 100)
  .executeUpdate();
```



#### 벌크 연산의 주의점

벌크 연산을 사용할 때는 벌크 연산이 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리한다는 점에 주의해야 한다.

1. 가격이 1000원인 상품A를 조회했다. 조회된 상품 A는 영속성 컨텍스트에서 관리된다.
2. 벌크 연산으로 모든 상품의 가격을 10% 상승시켰다. 따라서 가격은 1100원이 되어야 한다.
3. 벌크 연산을 수행한 후에 가격을 출력하면 1100원이 아니라 1000원이 출력된다.



벌크 연산은 영속성 컨텍스트를 통하지 않고 데이터베이스에 직접 쿼리하므로 영속성 컨텍스트에 있는 상품 A와
데이터베이스에 있는 상품A의 가격이 다를 수 있다. 이런 문제를 해결하는 방법을 살펴보자.

- **em.refresh() 사용**

  벌크 연산을 수행한 직후에 정확한 상품 A 엔티티를 사용해야 한다면 `em.refresh(productA)`로 다시 조회하면 된다.

- **벌크 연산 먼저 실행**

  벌크 연산을 먼저 실행하고 나서 상품 A를 조회하면 이미 변경된 상품 A를 조회하게 된다.

- **벌크 연산 수행 후 영속성 컨텍스트 초기화**

  영속성 컨텍스트를 초기화하면 이후 엔티티를 조회할 때 벌크 연산이 적용된 데이터베이스에서 엔티티를 조회한다.



가능하면 벌크 연산을 가장 먼저 실행하는 것이 좋고 상황에따라 초기화하는 것도 필요하다.



#### find() vs JPQL

em.find() 메서드는 엔티티를 영속성 컨텍스트에서 먼저 찾고 없으면 데이터베이스에서 찾는다.
해당 엔티티가 영속성 컨텍스트에 있으면 메모리에서 바로 찾으므로<sup>1차 캐시</sup> 성능상 이점이 있다.

그에 비해 **JPQL은 항상 데이터베이스에 SQL을 실행해서 결과를 조회한다.**



JPQL의 특징을 정리하자면

- JPQL은 항상 데이터베이스를 조회한다.
- JPQL로 조회한 엔티티는 영속 상태다.
- 영속성 컨텍스트에 이미 존재하는 엔티티가 있으면 기존 엔티티를 반환한다.



### 10.6.3 JPQL과 플러시 모드

플러시는 영속성 컨텍스트의 변경 내역을 데이터베이스에 동기화하는 것이다. JPA는 플러시가 일어날 때 영속성 컨텍스트에 등록, 수정, 삭제한 엔티티를 찾아서 INSERT, UPDATE, DELETE SQL을 만들어 데이터베이스에 반영한다.

플러시를 호출하려면  `em.flush()`를 직접 사용할수도 있지만 보통 플러시 모드에 따라 커밋하기 직전이나 
쿼리 실행 직전에 자동으로 호출된다. 플러시 모드는 FlushModeType.AUTO가 기본값이므로 JPA는 트랙젝션 커밋 직전이나
쿼리 실행 직전에 자동으로 플러시를 호출한다.



> *플러시 모드 설정*

```java
em.setFlushMode(FlushModeType.COMMIT); // 커밋 시에만 플러시

// 가격을 1000 -> 2000으로 변경
product.setPrice(2000);

// 1.em.flush() 직접 호출

// 가격이 2000인 상품 조회
Product product2 =
  em.createQuery("select p from Product p where p.price = 2000", Product.class)
  .setFlushMode(FlushModeType.AUTO) // 2. setFlushMode()
  .getSingleResult();
```

JPQL은 영속성 컨텍스트에 있는 데이터를 고려하지 않고  데이터베이스에서 데이터를 조회한다.
따라서 JPQL을 실행하기 전에 영속성 컨텍스트의 내용을 데이터베이스에 반영해야 한다.

플러시 모드의 기본값은 AUTO이므로 일반적인 상황에서는 위 내용을 고려하지 않아도 된다.
그렇다면 왜 COMMIT 모드를 사용하는 것일까?



#### 플러시 모드와 최적화

COMMIT 모드는 트랜잭션을 커밋할 때만 플러시하고 쿼리를 실행할 때는 플러시하지 않는다. 따라서 데이터 무결성에 심각한 
피해를 줄 수 있는데, 그럼에도 플러시가 너무 자주 일어나는 상황에 이 모드를 사용하면 플러시 횟수를 줄여서 성능을 최적화할 수 있다.







