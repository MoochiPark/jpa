# Chapter 04. 엔티티 매핑



JPA를 사용하는 데 가장 중요한 일은 엔티티와 테이블을 정확히 매핑하는 것이다.
따라서 매핑 애노테이션을 숙지하고 사용해야 한다. JPA는 다양한 매핑 애노테이션을 지원하는데 크게 4가지로 분류할 수 있다.

- **객체와 테이블 매핑**: @Entity, @Table
- **기본 키 매핑**: @Id
- **필드와 컬럼 매핑**: @Column
- **연관관계 매핑**: @ManyToOne, @JoinColumn



먼저 객체와 테이블 매핑 애노테이션부터 알아보자.



## 4.1 @Entity

JPA를 사용해서 테이블과 매핑할 클래스는 @Entity 애노테이션을 필수로 붙여야 한다.
@Entity가 붙은 클래스는 JPA가 관리하는 것으로, 엔티티라 부른다.

> *표 4.1 @Entity 속성 정리*

| 속성 | 기능                                                         | 기본값                                                      |
| ---- | ------------------------------------------------------------ | ----------------------------------------------------------- |
| name | JPA에서 사용할 엔티티 이름을 지정한다. 보통 기본값인 클래스 이름을 사용한다. 만약 다른 패키지에 이름이 같은 엔티티 클래스가 있다면 이름을 지정해서 충돌하지 않도록 해야 한다. | 설정하지 않으면 클래스 이름을 그대로 사용한다 (예: Member). |



@Entity 적용 시 주의사항은 다음과 같다.

- 기본 생성자는 필수다<sup>파라미터가 없는 public 또는 protected 생성자</sup>.
- final 클래스, enum, interface, inner 클래스에는 사용할 수 없다.
- 저장할 필드에 final을 사용하면 안 된다.



JPA가 엔티티 객체를 생성할 때 기본 생성자를 사용하므로 이 생성자는 반드시 있어야 한다. 자바는 생성자가 하나도 없으면 자동으로 기본 생성자를 만든다. 따라서 생성자를 하나 이상 만들면 기본 생성자를 자동으로 만들지 않기 때문에 직접 만들어야 한다.



## 4.2 @Table

@Table은 엔티티와 매핑할 테이블을 지정한다. 생략하면 매핑한 엔티티 이름을 테이블 이름으로 사용한다.

> *표 4.2 @Table 속성 정리*

| 속성                    | 기능                                                         | 기본값                  |
| ----------------------- | ------------------------------------------------------------ | ----------------------- |
| name                    | 매핑할 테이블 이름                                           | 엔티티 이름을 사용한다. |
| catalog                 | catalog 기능이 있는 데이터베이스에서 catalog를 사용한다.     |                         |
| schema                  | schema 기능이 있는 데이터베이스에서 schema를 사용한다.       |                         |
| uniqueConstraints (DDL) | DDL 생성 시에 유니크 제약조건을 만든다. 2개 이상의 복합 유니크 제약조건도 만들 수 있다. 참고로 이 기능은 스키마 자동 생성 기능을 사용해서 DDL을 만들 때만 사용된다. |                         |

DDL 생성 기능은 조금 뒤에 알아보자.



## 4.3 다양한 매핑 사용

JPA 시작하기 장에서 개발하던 회원 관리 프로그램에 다음 요구사항이 추가되었다고 해보자.

- 회원은 일반 회원과 관리자로 구분해야 한다.
- 회원 가입일과 수정일이 있어야 한다.
- 회원을 설명할 수 있는 필드가 있어야 한다. 이 필드는 길이 제한이 없다.



> *예제 4.1 회원 엔티티*

```java
@Entity
@Table(name = "MEMBER")
public class Member {

  @Id
  @Column(name = "ID")
  private String id;

  @Column(name = "NAME")
  private String username;

  private Integer age;
  
  // 추가
  @Enumerated(EnumType.STRING)
  private RoleType roleType;            --- 1
  
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;              --- 2

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModifiedDate;        --- 2

  @Lob
  private String description;           --- 3
  
  ...
```

1. roleType: enum을 사용해서 회원의 타입을 구분했다. 이처럼 enum을 사용하려면 Enumrated 애노테이션으로 매핑해야 한다. 

2. createdDate, lastModifiedDate: 자바의 날짜 타입은 @Temporal을 사용해서 매핑한다.

3. description: 회원을 설명하는 필드는 길이 제한이 없다. 따라서 데이터베이스의 VARCHAR가 아닌 CLOB 타입으로 저장해야 한다. @Lob을 사용하면 CLOB, BLOB<sup>postgresql의 TEXT</sup>타입을 매핑할 수 있다.



지금까지는 테이블을 먼저 생성하고 그 다음에 엔티티를 만들었지만 이번에는 데이터베이스 스키마 자동 생성을 사용해서 엔티티만 만들고 테이블은 자동으로 생성되도록 해보자.



## 4.4 데이터베이스 스키마 자동 생성

JPA는 데이터베이스 스키마를 자동으로 생성하는 기능을 지원한다. 클래스의 매핑정보를 보면 어떤 테이블에 어떤 칼럼을
사용하는지 알 수 있다. JPA는 이 매핑정보와 데이터베이스 방언을 사용해서 데이터베이스 스키마를 생성한다. 

스키마 자동 생성 기능을 사용해보자. 먼저 persistence.xml에 다음 속성을 추가하자.

`<property name="hibernate.hbm2ddl.auto" value="create"/>`

이 속성을 추가하면 **애플리케이션 실행 시점에 데이터베이스 테이블을 자동으로 생성한다.** 



> `<property name="hibernate.show_sql" value="true"/>`를 추가하면 콘솔에 실행되는 DDL을 출력할 수 있다.

```sql
Hibernate: 
    
    drop table if exists MEMBER cascade

Hibernate: 
    
    create table MEMBER (
       ID varchar(255) not null,
        age int4,
        createdDate timestamp,
        description text,
        lastModifiedDate timestamp,
        roleType varchar(255),
        NAME varchar(255),
        primary key (ID)
    )
```

실행된 결과를 보면 기존 테이블을 삭제하고 다시 생성한 것을 알 수 있다. 

자동 생성되는 DDL은 데이터베이스 방언마다 달라진다.

스키마 자동 생성 기능이 만든 DDL은 운영 환경에서 사용할 만큼 완벽하지는 않으므로 개발 환경에서 사용하거나 매핑을 어떻게
해야하는지 참고하는 정도로만 사용하는 것이 좋다.

그래도 이 기능을 사용하여 생성된 DDL을 보면 엔티티와 테이블이 어떻게 매핑되는지 쉽게 이해할 수 있어 훌륭한 학습 도구이다.



> *표 4.3 hibernate.hbm2ddl.auto 속성*

| 옵션        | 설명                                                         |
| ----------- | ------------------------------------------------------------ |
| create      | 기존 테이블을 삭제하고 새로 생성한다. DROP + CREATE          |
| create-drop | create 속성에 추가로 애플리케이션이 종료할 때 생성한  DDL을 제거한다. DROP + CREATE + DROP |
| update      | 데이터베이스 테이블과 엔티티 매핑정보를 비교해서 변경 사항만 수정한다. |
| validate    | 데이터베이스 테이블과 엔티티 매핑정보를 비교해서 차이가 있으면 경고를 남기고 애플리케이션을 수행하지 않는다. 이 설정은 DDL을 수정하지 않는다. |
| none        | 자동 생성 기능을 사용하지 않으려면 none과 같은 유효하지 않은 옵션 값을 주면 된다. |

> ***HBM2DDL 주의사항***
>
> 운영 서버에서 create, create-drop, update처럼 DDL을 수정하는 옵션은 절대 사용하면 안 된다.
> 이 옵션들은 운영 중인 데이터베이스의 테이블이나 컬럼을 삭제할 수 있다.
>
> 개발 환경에 따른 추천 전략은
>
> - 개발 초기 단계는 create 또는 update
> - 초기화 상태로 자동화된 테스트를 진행하는 개발자 환경과 CI 서버는 create 또는 create-drop
> - 테스트 서버는 update 또는 validate
> - 스테이징과 운영 서버는 validate 또는 none
>
> ***참고***
>
> JPA 2.1부터 스키마 자동 생성 기능을 표준으로 지원한다. 하지만 하이버네이트의 update, validate 속성은 지원하지 않는다. `<property name="javax.persistence.schema-generation.database.action" value="create"/>`
>
>  지원 옵션 : none, create, drop-and-create, drop



#### 이름 매핑 전략 변경하기

자바 언어는 관례상 roleType과 같이 카멜 표기법을 주로 사용하고 데이터베이스는 관례상 role_type과 같은 스네이크 표기법을
주로 사용한다. 따라서 이렇게 사용하려면 @Column의 name 속성을 사용해서 매핑해주어야 한다.

```java
@Column(name="role_type")
String roleType
```



hibernate.ejb.naming_strategy 속성을 사용하면 이름 매핑 전략을 변경할 수 있다. 이를 직접 구현해서 변경해도 되지만,
하이버네이트는 org.hibernate.cfg.improveNamingStrategy 클래스를 제공한다. 이 클래스는 테이블 명이나 컬럼 명이
생략되면 자바의 카멜 표기법을 테이블의 스네이크 표기법으로 매핑한다.

```groovy
<property name="hibernate.ejb.naming_strategy"
  value="org.hibernate.cfg.ImprovedNamingStrategy" />
```

이 속성을 사용해서 엔티티를 생성해보자.

```sql
Hibernate: 
    
    create table member (
       id varchar(255) not null,
        age int4,
        created_date timestamp,
        description text,
        last_modified_date timestamp,
        role_type varchar(255),
        name varchar(255),
        primary key (id)
    )
```

하지만 하이버네이트 5 부터는 위 방식을 지원하지 않는다.

따라서 다음과 같이 변경했다.



> *build.gradle*

`implementation 'org.apache.commons:commons-lang3:3.9'` 추가

> *persistence.xml*

`<property name="hibernate.physical_naming_strategy" value="io.wisoft.daewon.namingstrategy.PhysicalNamingStrategyImpl" />` 추가

> *CustomPhysicalNamingStrategy.java*

```java
package io.wisoft.daewon.namingstrategy;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class PhysicalNamingStrategyImpl implements PhysicalNamingStrategy {

  @Override
  public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return name;
  }

  @Override
  public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return name;
  }

  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    final LinkedList<String> parts = splitAndReplace(name.getText());
    return jdbcEnvironment.getIdentifierHelper().toIdentifier(
        join(parts),
        name.isQuoted()
    );
  }

  @Override
  public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return name;
  }

  @Override
  public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    final LinkedList<String> parts = splitAndReplace(name.getText());
    return jdbcEnvironment.getIdentifierHelper().toIdentifier(
        join(parts),
        name.isQuoted()
    );
  }

  private LinkedList<String> splitAndReplace(String name) {
    LinkedList<String> result = new LinkedList<>();
    for (String part : StringUtils.splitByCharacterTypeCamelCase(name)) {
      if (part == null || part.trim().isEmpty()) {
        continue;
      }
      result.add(part.toLowerCase(Locale.ROOT));
    }
    return result;
  }

  private String join(List<String> parts) {
    boolean firstPass = true;
    String separator = "";
    StringBuilder joined = new StringBuilder();
    for (String part : parts) {
      joined.append(separator).append(part);
      if (firstPass) {
        firstPass = false;
        separator = "_";
      }
    }
    return joined.toString();
  }

}
```





## 4.5 DDL 생성 기능

회원 이름은 필수로 입력되어야 하고, 10자를 초과하면 안 된다는 제약 조건이 추가되었다. 
스키마 자동 생성하기를 통해 만들어지는 DDL에 이 제약조건을 추가해보자.

> *예제 4.4 추가 코드*

```java
@Entity
@Table(name = "MEMBER")
public class Member {

  @Id
  @Column(name = "ID")
  private String id;

  @Column(name = "NAME", nullable = false, length = 10) // 추가
  private String username;
  ...
```

@Culumn 매핑정보의 nullable 속성 값을 false로 지정하면 자동 생성되는 DDL에 not null 제약 조건을 추가할 수 있다.
그리고 length 속성 값으로 문자의 크기를 지정할 수 있다.

```sql
  ...
  name varchar(10) not null,
  ...
```



이번엔 유니크 제약조건을 만들어주는 @Table의 uniqueConstraints 속성을 알아보자.

> *예제 4.6 유니크 제약조건*

```java
@Entity(name = "Member")
@Table(name = "MEMBER", uniqueConstraints = {@UniqueConstraint(
    name = "NAME_AGE_UNIQUE",
    columnNames = {"NAME", "AGE"} )})
public class Member {
  ...
```

> *생성된 DDL*

```sql
Hibernate: 
    
    alter table member 
       add constraint NAME_AGE_UNIQUE unique (name, age)
```

DDL을 보면 유니크 제약조건이 추가되었다. 앞의 기능들을 포함해서 **이런 기능들은 단지 DDL을 자동 생성할 때만 사용되고**
**JPA의 실행 로직에는 영향을 주지 않는다.** 따라서 스키마 자동 생성 기능을 사용하지 않는다면 사용할 이유가 없다.

그래도 이 기능을 사용하면 애플리케이션 개발자가 엔티티만 보고도 쉽게 다양한 조건을 파악할 수 있다는 장점이 있다.

다음은 데이터베이스의 기본 키를 어떻게 매핑하는지 알아보자.



## 4.6 기본 키 매핑

기본 키<sup>primary key</sup> 매핑을 알아보자.

> *기본 키 매핑 시작*

```java
public class Member {

  @Id
  @Column(name = "ID")
  private String id;
  ...
```

지금까지 @Id 애노테이션만 사용해서 회원의 기본 키를 애플리케이션에서 직접 할당했다. 기본 키를 애플리케이션에서 직접
할당하는 것이 아니라 데이터베이스가 생성해주는 값,  예를 들어 postgresql의 SERIAL을 사용하려면 어떻게 해야할까?

데이터베이스마다 기본 키를 생성하는 방식이 다른데, JPA는 이 문제들을 어떻게 해결하는지 보자.
JPA가 제공하는 데이터베이스 기본 키 생성 전략은 다음과 같다.

- **직접 할당**: 기본 키를 애플리케이션에서 직접 할당한다.
- **자동 생성**: 대리 키 사용 방식
  - **IDENTITY**: 기본 키 생성을 데이터베이스에 위임한다.
  - **SEQUENCE**: 데이터베이스 시퀀스를 사용해서 기본 키를 할당한다.
  - **TABLE**: 키 생성 테이블을 사용한다.



 자동 생성 전략이 다양한 이유는 데이터베이스 벤더마다 지원하는 방식이 다르기 때문이다. 

기본 키를 직접 할당하려면 @Id만 사용하면 되고, 자동 생성 전략을 사용하려면 @Id에 @GeneratedValue를 추가하고 원하는 키 생성 전략을 선택하면 된다. 

> 키 생성 전략을 사용하려면 persistence.xml에 `<property name="hibernate.id.new_generator_mappings" value="true"/>` 속성을 반드시 추가해주어야 한다.



### 4.6.1 기본 키 직접 할당 전략

```java
  @Id
  @Column(name = "ID")
  private String id;
```

@Id 적용 가능 자바 타입은 다음과 같다.

- 자바 기본형
- 자바 래퍼형
- String
- java.util.Date
- java.sql.Date
- BigDecimal
- BigInteger

기본 키 직접 할당 전략은 em.persist()로 엔티티를 저장하기 전에 애플리케이션에서 기본 키를 직접 할당하는 방법이다.

```java
Board board = new Board();
board.setId("id1"); // 기본 키 직접 할당
em.persist(board);
```





### 4.6.2 IDENTITY 전략

IDENTITY는 기본 키 생성을 데이터베이스에 위임하는 전략이다. 주로 MySQL, PostgreSQL, SQL Server 등에서 사용한다.
예를 들어 PostgreSQL의 SERIAL 기능은 데이터베이스가 기본 키를 자동으로 생성해준다. 

```sql
CREATE TABLE board (
  ID SERIAL PRIMARY KEY,
  DATA VARCHAR(255)
);

INSERT INTO BOARD(DATA) VALUES('A');
INSERT INTO BOARD(DATA) VALUES('B');
```

이제 데이터베이스에 값을 저장할 때 ID 컬럼을 비워두면 데이터베이스가 순서대로 값을 채워준다.
지금처럼 식별자가 생성되는 경우에는 @GeneratedValue 어노테이션을 사용하고 식별자 생성 전략을 선택해야 한다.

```java
  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
```



```java
  public static void main(String... args) {

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    
    logic(em);
    
    tx.commit();
  }

  private static void logic(final EntityManager em) {
    Member member = new Member();
    member.setUsername("pdw");
    em.persist(member);
    System.out.println("member.id = " + member.getId());
  }

```

**실행 결과**

```java
meber.id = 1
```



> ***주의***
>
> 엔티티가 영속 상태가 되려면 식별자가 반드시 필요하다. 그런데 IDENTITY 식별자 생성 전략은 엔티티를 데이터베이스에 저장해야 식별자를 구할 수 있으므로 em.persist()를 호출하는 즉시 INSERT SQL이 데이터베이스에 전달된다.
> 따라서 이 전략은 트랜잭션을 지원하는 쓰기 지연이 동작하지 않는다.



### 4.6.3 SEQUENCE 전략

데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트다. SEQUENCE 전략은 이 시퀀스를 사용해서 기본 키를 생성한다. 이 전략은 시퀀스를 지원하는 Oracle, PostgreSQL 등에서 사용할 수 있다.

`CREATE SEQUENCE member_seq START WITH 1 INCREMENT BY 1;`

> *시퀀스 매핑 코드*

```java
@Entity(name = "Member")
@SequenceGenerator(
    name = "MEMBER_SEQ_GENERATOR",
    sequenceName = "MEMBER_SEQ",
    initialValue = 1, allocationSize = 1)
public class Member {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, 
                  generator = "MEMBER_SEQ_GENERATOR")
  private Long id;
  ...
```

- @SequenceGenerator를 사용해서 MEMBER_SEQ_GENERATOR라는 시퀀스 생성기를 등록했다.
- sequenceName 속성의 이름으로 MEMBER_SEQ를 지정했는데 JPA는 이 시퀀스 생성기를 실제 데이터베이스의
  MEMBER_SEQ 시퀀스와 매핑한다.
- 키 생성 전략을 `GenerationType.SEQUENCE`로 설정하고  `generator = "MEMBER_SEQ_GENERATOR"`로 방금 등록한
  시퀀스 생성기를 선택했다. 



시퀀스 사용 코드는 IDENTITY 전략과 같지만 내부 동작 방식은 다르다. SEQUENCE 전략은 em.persist()를 호출할 때 먼저
데이터베이스 시퀀스를 사용해서 식별자를 조회한다. 그리고 조회한 식별자를 엔티티에 할당한 후에 엔티티를 영속성 컨텍스트에 저장한다. 이후 트랜잭션을 커밋해서 플러시가 일어나면 엔티티를 데이터베이스에 저장한다.

반대로 이전에 설명한 IDENTITY는 먼저 엔티티를 데이터베이스에 저장한 후에 식별자를 조회해서 엔티티의 식별자에 할당한다.



### @SequenceGenerator

> *표 4.5 @SequenceGenerator*

| 속성            | 기능                                                         | 기본값             |
| --------------- | ------------------------------------------------------------ | ------------------ |
| name            | 식별자 생성기 이름                                           | 필수               |
| sequenceName    | 데이터베이스에 등록되어 있는 시퀀스 이름                     | hibernate_sequence |
| initialValue    | DDL 생성 시에만 사용됨. 시퀀스 DDL을 생성할 때 처음 시작하는 수를 지정한다. | 1                  |
| allocationSize  | 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용됨)        | 50(주의)           |
| catalog, schema | 데이터베이스 catalog, schema 이름                            |                    |



매핑할 DDL은 다음과 같다.

`CREATE SEQUENCE [sequenceName] START WITH [initialValue] INCREMENT BY [allocationSize];`



> ***SEQUENCE 전략과 최적화***
>
> 1. 식별자를 구하려고 데이터베이스 시퀀스를 조회한다.
>
>    `SELECT nextval('member_seq');`
>
> 2. 조회한 시퀀스를 기본 키 값으로 사용해 데이터베이스에 저장한다.
>
>    `INSERT INTO MEMBER ...`
>
> JPA는 시퀀스에 접근하는 횟수를 줄이기 위해 @SequenceGenerator.allocationSize를 사용한다.
> 간단히 설명하면 여기에 설정한 값만큼 한 번에 시퀀스 값을 증가시키고 나서 그만큼 메모리에 시퀀스 값을 할당한다.
> 예를 들어 allocationSize 값이 50이면 시퀀스를 한 번에 50 증가시킨 다음에 1~50까지는 메모리에서 식별자를 할당한다.
> 그리고 51이 되면 시퀀스 값을 100으로 증가시킨 다음에 51~100까지 메모리에서 식별자를 할당한다.
>
> 이 최적화 방법은 시퀀스 값을 선점하므로 여러 JVM이 동시에 동작해도 기본 키 값이 충돌하지 않는 장점이 있다.





### 4.6.4 테이블 전략

TABLE 전략은 키 생성 전용 테이블을 만들고 여기에 이름과 값으로 사용할 컬럼을 만들어 시퀀스를 흉내내는 전략이다.
이 전략은 테이블을 사용하기 때문에 모든 데이터베이스에 적용할 수 있다.

> *TABLE 전략 키 생성 DDL*

```sql
create table MY_SEQUENCES (
  sequence_name varchar(255) primary key, --- 시퀀스 이름
  next_val bigint --- 시퀀스 값
)
```



> *TABLE 전략 매핑 코드*

```java
@Entity
@TableGenerator(
  name = "MEMBER_SEQ_GENERATOR",
  table = "MY_SEQUENCES",
  pkColumnValue = "MEMBER_SEQ", allocationSize = 1)

...
  @Id
  @GeneratorValue(strategy = GenerationType.TABLE,
                 generator = "MEMBER_SEQ_GENERATOR")
  private Long id;
  ...
```



TABLE 전략은 시퀀스 대신에 테이블을 사용한다는 것만 제외하면 SEQUENCE 전략과 내부 동작방식이 같다.

이제 키 생성기를 사용할 때마다 next_val 컬럼 값이 증가한다. 참고로 테이블에 값이 없어도 JPA가 INSERT 하면서 초기화하므로 값을 미리 넣어둘 필요는 없다.



#### @TableGenerator

> *표 4.5 @TableGenerator*

| 속성                  | 기능                                                         | 기본값              |
| --------------------- | ------------------------------------------------------------ | ------------------- |
| name                  | 식별자 생성기 이름                                           | 필수                |
| table                 | 키생성 테이블명                                              | hibernate_sequences |
| pkColumnName          | 시퀀스 컬럼명                                                | sequence_name       |
| valueColumnsName      | 시퀀스 값 컬럼 명                                            | next_val            |
| pkColumnValue         | 키로 사용할 값 이름                                          | 엔티티 이름         |
| initialValue          | DDL 생성 시에만 사용됨. 시퀀스 DDL을 생성할 때 처음 시작하는 수를 지정한다. | 0                   |
| allocationSize        | 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용됨)        | 50                  |
| catalog, schema       | 데이터베이스 catalog, schema 이름                            |                     |
| uniqueConstraint(DDL) | 유니크 제약 조건을 지정할 수 있다.                           |                     |



> *매핑할 DDL, 테이블 명 {table}*

| {pkColumnName}  | {valueColumnName} |
| --------------- | ----------------- |
| {pkColumnValue} | {initialValue}    |



> ***TABLE 전략과 최적화***
>
> TABLE 전략은 값을 조회하면서 SELECT, 다음값으로 증가시키기 위해 UPDATE를 사용한다. 
> 이 전략은  SEQUENCE 전략과 비교해서 데이터베이스와 한 번 더 통신하는 단점이 있다. 
> 마찬가지로 TABLE 전략을 최적화하려면 @TableGenerator.allocationSize를 사용하면 된다.





### 4.6.5 AUTO 전략

데이터베이스의 종류도 많고 기본 키를 만드는 방법도 다양하다. GenerationType.AUTO는 선택한 데이터베이스 방언에 따라
IDENTITY, SEQUENCE, TABLE 전략 중 하나를 자동으로 선택한다. (PostgreSQL은 SEQUENCE를 선택한다.)

> *AUTO 전략 매핑 코드*

```java
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
```

@GeneratedValue.strategy의 기본값은 AUTO다. 따라서 다음과 같이 사용해도 결과는 같다.

```java
  @Id @GeneratedValue
  private Long id;
```



AUTO 전략의 장점은 데이터베이스를 변경해도 코드를 수정할 필요가 없다는 것이다. 특히 키 생성 전략이 아직 확정되지 않은
개발 초기나 프로토타입 개발 시 편리하게 사용할 수 있다.

AUTO를 사용할 때 SEQUENCE나 TABLE 전략이 선택되면 시퀀스나 키 생성용 테이블을 미리 만들어 두어야 한다.
만약 스키마 자동 생성을 이용한다면 하이버네이트가 기본값을 사용해서 적절한 시퀀스나 키 생성용 테이블을 만들어 줄 것이다.





### 4.6.6 기본 키 매핑 정리

영속성 컨텍스트는 엔티티를 식별자 값으로 구분하므로 엔티티를 영속 상태로 만들려면 식별자 값이 반드시 있어야 했다.
em.persist()를 호출한 직후에 발생하는 일을 식별자 할당 전략별로 정리해보자.

- **직접 할당**: em.persist()를 호출하기 전에 애플리케이션에서 직접 식별자 값을 할당해야 한다.
  만약 식별자 값이 없으면 예외가 발생한다.

- **SEQUENCE**: 데이터베이스 시퀀스에서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다.

- **TABLE**: 데이터베이스 시퀀스 생성용 테이블에서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다.

- **IDENTITY**: 데이터베이스에 엔티티를 저장해서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다.

  (테이블에 데이터를 저장해야 식별자 값을 획득할 수 있다.)



> ***권장하는 식별자 선택 전략***
>
> 데이터베이스 기본 키는 다음 3가지 조건을 모두 만족해야 한다. 
>
> 1. null값은 허용하지 않는다.
> 2. 유일해야 한다.
> 3. 변해선 안 된다.
>
> 테이블의 기본 키를 선택하는 전략은 크게 2가지가 있다.
>
> - 자연 키<sup>natural key</sup>
>   - 비즈니스에 의미가 있는 키
>   - 예: 주민등록번호, 이메일, 전화번호
> - 대리 키<sup>surrogate key</sup>
>   - 비즈니스와 관계 없는 임의로 만들어진 키, 대체 키로도 불린다.
>   - 예: 시퀀스, serial, 키생성 테이블 사용
>
> **자연 키 보다는 대리 키를 권장한다**
>
> 예를 들어 자연 키인 전화번호를 기본 키로 선택한다면 그 번호가 유일할 순 있지만, 전화번호가 없을 수도 있고 변경될 수도 있다. 따라서 기본 키로 적절하지 않다. 주민번호도 3가지 조건을 만족해보이지만 현실과 비즈니스 규칙은 생각보다 쉽게
> 변한다. 주민등록번호 조차도 여러 가지 이유로 변경될 수 있다.
>
> **JPA는 모든 엔티티에 일관된 방식으로 대리키 사용을 권장한다**
>
> 비즈니스 요구사항은 계속해서 변하는데 테이블은 한 번 정의하면 변경하기 어렵다. 
> 그런 면에서 대리 키가 일반적으로 좋은 선택이라 생각된다.







## 4.8 정리

이 장을 통해 객체와 테이블 매핑, 기본 키 매핑, 필드와 컬럼 매핑에 대해 알아보았다. 그리고 데이터베이스 스키마 자동 생성하기
기능도 알아보았는데, 이 기능을 사용하면 엔티티 객체를 먼저 만들고 테이블은 자동으로 생성할 수 있었다.

JPA는 다양한 기본 키 매핑 전략을 지원한다. 기본 키를 애플리케이션에서 직접 할당하는 방법부터 데이터베이스가 제공하는 기본 키를 사용하는 SEQUENCE, IDENTITY, TABLE 전략에 대해서도 알아보았다.

회원 엔티티는 다른 엔티티와 관계가 없었는데, 회원이 특정 팀에 속해있는 연관관계가 있는 엔티티들을 어떻게 매핑하는지는
다음 장을 통해 알아볼 것이다.



[실전 예제 - 작은 쇼핑몰 만들기](https://github.com/MoochiPark/jpa/tree/master/chapter04/src)