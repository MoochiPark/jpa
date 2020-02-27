# Chapter 02. JPA 시작

> ***실습 환경***
>
> - *MacOS Catalina*
>
> - *Jetbrains IntelliJ IDEA*
> - *gradle*
> - *postgresql*



#### *라이브러리와 프로젝트 구조*

교재에서는 메이븐을 사용해 필요 라이브러리를 관리하지만 더 간편한 그레이들<sup>gradle</sup>로 진행할 예정이다.

JPA 구현체로 하이버네이트를 사용하기 위한 핵심 라이브러리는 다음과 같다. (하이버네이트 버전 5.4.12.Final 사용)

- `hibernate-core`: 하이버네이트 라이브러리
- `hibernate-entitymanager`: 하이버네이트가 JPA 구현체로 동작하도록 JPA 표준을 구현한 라이브러리
- `hibernate-jpa-2.1-api`: JPA 표준 API를 모아둔 라이브러리

- `postgresql`: postgresql 데이터베이스 라이브러리



> *build.gradle*

```groovy
...
  dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.12'
    implementation 'org.hibernate:hibernate-core:5.4.12.Final'
    implementation 'org.hibernate:hibernate-entitymanager:5.4.12.Final'
    implementation 'org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.2.Final'
    implementation 'org.postgresql:postgresql:42.2.10.jre7'
}
...
```



## 2.4 객체 매핑 시작

먼저 다음 SQL을 입력하여 MEMBER 테이블을 생성하자.

> *예제 2.1 회원 테이블*

```sql
CREATE TABLE member (
    id varchar(255) primary key,
    name varchar(255),
    age integer not null
)
```

다음으로 애플리케이션에서 사용할 회원 클래스를 만들자.

> *예제 2.5 회원 클래스*

```java
package io.wisoft.daewon.jpa.start;

public class Member {

  private String id;
  private String username;
  private Integer age;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }
  
}
```

JPA를 사용하려 가장 먼저 회원 클래스와 회원 테이블을 매핑해야 한다. 

> *표 2.1 매핑 정보*

| 매핑 정보       | 회원 객체 | 회원 테이블 |
| --------------- | --------- | ----------- |
| 클래스와 테이블 | Member    | MEMBER      |
| 기본 키         | id        | ID          |
| 필드와 컬럼     | username  | NAME        |
| 필드와 컬럼     | age       | AGE         |

다음 처럼 회원 클래스에 JPA가 제공하는 매핑 애노테이션을 추가하자.

> *예제 2.6 매핑 정보가 포함된 회원 클래스*

```java
import javax.persistence.*;

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
  ...
```

> *그림 2.10 클래스와 테이블 매핑*

![image](https://user-images.githubusercontent.com/43429667/75422203-63e6a500-597f-11ea-9fe1-6f5fe2ca5fa1.png)

회원 클래스에 매핑 정보를 표시하는 어노테이션을 몇 개 추가했다. 회원 클래스에 사용한 매핑 어노테이션을 하나씩 살펴보자.



- **@Entity**

  이 클래스를 테이블과 매핑한다고 JPA에게 알려준다. @Entity가 사용된 클래스를 엔티티 클래스라 한다.



- **@Table**

  엔티티 클래스에 매핑할 테이블 정보를 알려준다. 여기선 name 속성을 사용해서 Member 엔티티를 MEMBER 테이블에
  매핑했다. 이 어노테이션을 생략하면 클래스 이름을 테이블 이름으로 매핑한다.(더 정확히는 엔티티 이름을 사용한다.)



- **@Id**

  엔티티 클래스의 필드를 테이블의 기본 키<sup>primary key</sup>에 매핑한다. @Id가 사용된 필드를 식별자 필드라 한다.



- **@Column**

  필드를 컬럼에 매핑한다. 여기선 name 속성을 사용해서 Member 엔티티의 username 필드를 MEMBER 테이블의 
  NAME 컬럼에 매핑했다.



- **매핑 정보가 없는 필드**

  age 필드에는 매핑 어노테이션이 없다. 이렇게 생략하면 필드명을 사용해서 컬럼명으로 매핑한다.
  여기서는 필드명이 age이므로 age 컬럼으로 매핑한다. (대소문자를 구분하지 않는다고 가정)



매핑 정보 덕분에 JPA는 어떤 엔티티를 어떤 테이블에 저장해야 하는지 알 수 있게된다.
다음으로 JPA를 실행하기 위한 기본 설정 파일인 persistence.xml을 알아보자.



## 2.5 persistence.xml 설정

JPA는 persistence.xml을 사용해서 필요한 설정 정보를 관리한다. 
이 설정 파일이 META-INF/persistence.xml 클래스 패스 경로에 있으면 별도의 설정 없이 JPA가 인식할 수 있다.

> *예제 2.7 JPA 환경설정 파일 persistence.xml*

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.1">
    <persistence-unit name="jpabook">
        <properties>
            <!-- 필수 속성 -->
            <property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver"/>
            <property name="javax.persistence.jdbc.user" value="voipmttw"/>
            <property name="javax.persistence.jdbc.password" 
                      value="WZUPulq6BK9d2Ho8ywTU-i2m2lhACQj8"/>
            <property name="javax.persistence.jdbc.url"
                      value="jdbc:postgresql://satao.db.elephantsql.com:5432/voipmttw"/>
            <property name="hibernate.dialect" 
                      value="org.hibernate.dialect.PostgresPlusDialect"/>
            
            <!-- 옵션 -->
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
            <property name="hibernate.use_sql_comments" value="true"/>
            <property name="hibernate.id.new_generator_mappings" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

- `<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.1">`

  설정 파일은 persistence로 시작한다. 이곳에 XML 네임스페이스와 사용할 버전을 지정한다.

- `    <persistence-unit name="jpabook">`

  JPA 설정은 영속성 유닛<sup>persistence-unit</sup>이라는 것부터 시작하는데 일반적으로 연결할 데이터베이스당 하나의 영속성 유닛을
  등록한다. 그리고 영속성 유닛에는 고유한 이름을 부여하는데 여기서는 `jpabook`이라는 이름을 사용했다.

- **JPA 표준 속성**

  - javax.persistence.jdbc.driver: JDBC 드라이버
  - javax.persistence.jdbc.user: 데이터베이스 접속 아이디
  - javax.persistence.jdbc.password: 데이터베이스 접속 비밀번호
  - javax.persistence.jdbc.url: 데이터베이스 접속 URL

- **하이버네이트 속성**

  - hibernate.dialect: 데이터베이스 방언<sup>Dialect</sup> 설정



이름이 javax.persistence로 시작하는 속성은 JPA 표준 속성으로 특정 구현체에 종속되지 않는다.

반면에 hibernate로 시작하는 속성은 하이버네이트 전용 속성이므로 하이버네이트에서만 사용할 수 있다.



### 2.5.1 데이터베이스 방언

JPA는 특정 데이터베이스에 종속적이지 않은 기술이다. 따라서 다른 데이터베이스로 손쉽게 교체할 수 있다.
그런데 각 데이터베이스가 제공하는 SQL 문법과 함수는 다음처럼 조금씩 다른 문제점이 있다.

- **데이터 타입**: 가변 문자 타입으로 MySQL은  VARCHAR, 오라클은 VARCHAR2를 사용한다.
- **다른 함수명**: 문자열을 자르는 함수로 SQL 표준은 SUBSTRING()을 사용하지만 오라클은 SUBSTR()을 사용한다.
- **페이징 처리**: MySQL은 LIMIT를 사용하지만 오라클은 ROWNUM을 사용한다.



이처럼 SQL 표준을 지키지 않거나 특정 데이터베이스만의 고유한 기능을 JPA에서는 **방언<sup>Dialent</sup>**이라 한다.

하이버네이트를 포함한 대부분의 JPA 구현체들은 이런 문제를 해결하려고 다양한 데이터베이스 방언클래스를 제공한다.
따라서 데이터베이스가 변경되어도 애플리케이션 코드를 변경할 필요 없이 데이터베이스 방언만 교체하면 된다.

- **PostgreSQL 방언**: `org.hibernate.dialect.PostgresPlusDialect`



- `hibernate.show_sql`: 하이버네이트가 실행한 SQL을 출력한다.
- `hibernate.format_sql`: 하이버네이트가 실행한 SQL을 출력할 때 보기 쉽게 정렬한다.
- `hibernate.use_sql_comments`: 쿼리를 출력할 때 주석도 함께 출력한다.
- `hibernate.id.generator_mappings`: JPA 표준에 맞춘 새로운 키 생성 전략을 사용한다. (4.6절에서 자세히)



## 2.6 애플리케이션 개발

객체 매핑과 persistence.xml로 JPA 설정도 완료했다. 이제 JPA 애플리케이션을 개발해보자.

> *예제 2.8 시작 코드*

```java
public class JpaMain {

  public static void main(String... args) {
    // [엔티티 매니저 팩토리] - 생성
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
    // [엔티티 매니저] - 생성
    EntityManager em = emf.createEntityManager();
    // [트랜잭션] - 획득
    EntityTransaction tx = em.getTransaction();
    
    try {
      tx.begin();     // [트랜잭션] - 시작
      logic(em);      // 비즈니스 로직 실행
      tx.commit();    // [트랜잭션] - 커밋
    } catch (Exception e) {
      tx.rollback();  // [트랜잭션] - 롤백
    } finally {
      em.close();     // [엔티티 매니저] - 종료
    }
    emf.close();      // [엔티티 매니저 팩토리] - 종료  
  }
  
  private static void logic(final EntityManager em) {
    ...
  }
  
}
```

코드는 크게 3부분으로 나뉘어 있다.

- 엔티티 매니저 설정
- 트랜잭션 관리
- 비즈니스 로직



### 2.6.1 엔티티 매니저 설정

![image](https://user-images.githubusercontent.com/43429667/75432955-92ba4680-5992-11ea-89e1-221b23ee72cf.png)

- **엔티티 매니저 팩토리 생성**

  JPA를 시작하려면 우선 persistence.xml의 설정 정보를 사용해서 엔티티 매니저 팩토리를 생성해야 한다. 
  이때 Persistence 클래스를 사용하는데 이 클래스는 엔티티 매니저 팩토리를 생성해서 JPA를 사용할 수 있게 준비한다.

  `EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");`

  