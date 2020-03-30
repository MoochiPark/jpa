# Chapter 08. 프록시와 연관관계 관리

- **프록시와 즉시 로딩, 지연 로딩**
- **영속성 전이와 고아 객체**



## 8.1 프록시

엔티티를 조회할 때 연관된 엔티티들이 항상 사용되는 것은 아니다.

<script src="https://gist.github.com/4cb261f3a2b6b480e51ba60536cb51ec.js"></script>

<script src="https://gist.github.com/34ea562a8e17e0d1b7f7e1ac0ed24c12.js"></script>

<script src="https://gist.github.com/e4d4bcfb38254af00ea11b796a92d556.js"></script>

- printUserAndTeam() 메서드는 memberId로 회원 엔티티를 찾아서 연관된 팀의 이름도 출력한다.
- 반면 printUser() 메서드는 회원 엔티티만 출력하고 연관된 팀 엔티티는 전혀 사용하지 않는다.

- printUser() 메서드는 회원 엔티티만 사용하므로 em.find()로 회원 엔티티를 조회할 때 회원과 연관된 팀 엔티티까지
  데이터베이스에서 함께 조회해 두는 것은 효율적이지 않다.

JPA는 위 문제를 해결하려고 엔티티가 실제 사용될 때까지 데이터베이스 조회를 지연하는 **지연로딩**이라는 방법을 제공한다.
지연 로딩이 가능하려면 실제 엔티티 객체 대신에 조회를 지연할 수 있는 가짜 객체 **프록시 객체**가 필요하다.



### 8.1.1 프록시 기초

EntityManager.find()는 영속성 컨텍스트에 엔티티가 없으면 데이터베이스를 조회한다.

```java
Member meber = em.find(Member.class, "member1");
```

이렇게 직접 조회하면 사용하든 안하든 데이터베이스를 조회한다. 엔티티를 실제 사용하는 시점까지 데이터베이스 조회를
미루고 싶다면 EntityManager.getReference()를 사용하면 된다.

```java
Member member = em.getReference(Member.class, "member1");
```

이 메서드를 호출할 때 JPA는 데이터베이스를 조회하지 않고 실제 엔티티 객체도 생성하지 않는다.
대신에 데이터베이스 접근을 위임한 프록시 객체를 반환한다.



> *프록시 조회*

# 그림



- **프록시의 특징**

  프록시 클래스는 실제 클래스를 상속 받아서 만들어지므로 실제 클래스와 겉 모양이 같다.
  사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 된다.

  프록시 객체는 실제 객체에 대한 참조<sup>target</sup>를 보관한다. 프록시 객체의 메서드를 호출하면 
  프록시 객체는 실제 객체의 메서드를 호출한다.

- **프록시 객체의 초기화**

  프록시 객체는 member.getName() 처럼 실제 사용될 떄 데이터베이스를 조회해서 실제 엔티티 객체를 생성하는데
  이것을 프록시 객체의 초기화라 한다. 

  ```java
  // MemberProxy 반환
  Member member = em.gerReference(Member.class, "id1");
  member.getName(); // 1.getName()
  ```

  > *프록시 클래스 예상 코드*

  ```java
  class MemberProxy extends Member {
    
    Member target = null; // 실제 엔티티 참조
    
    public String getName() {
      if (target == null) {
        // 2. 초기화 요청
        // 3. DB 조회
        // 4. 실제 엔티티 생성 및 참조 보관
        this.target = ...;
      }
      // 5. target.getName();
      return target.getName();
    }
    
  }
  ```

  > *프록시 초기화*

  # 그림

  1. 프록시 객체에 member.getName()을 호출해서 실제 데이터를 조회한다.
  2. 프록시 객체는 실제 엔티티가 생성되어 있지 않으면 영속성 컨텍스트에 실제 엔티티 생성을 요청하는데 이것을 초기화라 한다.
  3. 영속성 컨텍스트는 데이터베이스를 조회해서 실제 엔티티 객체를 생성한다.
  4. 프록시 객체는 생성된 실제 엔티티 객체의 참조를 Member target 필드에 보관한다.
  5. 프록시 객체는 실제 엔티티 객체의 getName()을 호출해서 결과를 리턴한다.



- **프록시의 특징**
  - 프록시 객체는 처음 사용할 때 한 번만 초기화된다.
  - 프록시 객체를 초기화해도 실제 엔티티로 바뀌는 것은 아니다. 초기화 되면 실제 엔티티에 접근 가능하다.
  - 프록시 객체는 원본 엔티티를 상속받은 객체이므로 타입 체크 시에 주의해서 사용해야 한다.
  - 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 데이터베이스를 조회할 필요가 없으므로 em.getReference()를 호출해도 프록시가 아닌 실제 엔티티를 반환한다.
  - 초기화는 영속성 컨텍스트의 도움을 받아야 가능하다. 따라서 준영속 상태의 프록시를 초기화하면 문제가 발생한다.



- **준영속 상태와 초기화**

  ```java
  // MemberProxy 반환
  Member member = em.getReference(Member.class, "id1");
  transaction.commit();
  em.close(); // 영속성 컨텍스트 종료
  
  member.getName(); // 준영속 상태에서 초기화 시도, 
  									// hibernate.LazyInitializationException 발생
  ```





### 8.1.2 프록시와 식별자

엔티티를 프록시로 조회할 때 식별자<sup>PK</sup> 값을 파라미터로 전달하는데 프록시 객체는 이 식별자 값을 보관한다.

```java
Team team = em.getReference(Team.class, "team1"); // 식별자 보관
team.getId(); // 초기화되지 않음
```

단 엔티티 접근 방식을 프로퍼티<sup>@Access(AccessType.PROPERTY)</sup>로 설정한 경우에만 초기화하지 않는다.
접근 방식을 필드<sup>@Access(AccessType.FIELD)</sup>로 설정하면 JPA는 getId()가 id만 조회하는 메서드인지 다른 필드까지 활용하는
메서드인지 알지 못하므로 프록시 객체를 초기화한다.

프록시는 연관관계를 설정할 때 유용하게 사용할 수 있다.

```java
Member member = em.find(Member.class, "member1");
Team team = em.getReference(Team.class, "team1"); // SQL을 실행하지 않음
member.setTeam(team);
```

연관관계를 설정할 때는 식별자 값만 사용하므로 프록시를 사용하면 데이터베이스 접근 횟수를 줄일 수 있다.
연관관계를 설정할 때는 엔티티 접근 방식이 필드여도 프록시를 초기화하지 않는다.



### 8.1.3 프록시 확인

JPA가 제공하는 `PersistenceUnitUtil.isLoaded(Object entity)` 메서드를 사용하면 프록시 인스턴스의 
초기화 여부를 확인할 수 있다. 

```java
boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(entity);
```

조회한 엔티티가 프록시로 조회한 것인지 확인하려면 클래스명을 직접 출력해보면 된다. 



## 8.2 즉시 로딩과 지연 로딩

JPA는 개발자가 연관된 엔티티의 조회 시점을 선택할 수 있도록 두 가지 방법을 제공한다.

- **즉시 로딩**: 엔티티를 조회할 때 연관된 엔티티도 함께 조회한다.
  - 설정 방법: @ManyToOne(fetch = FetchType.EAGER)



- **지연 로딩**: 연관된 엔티티를 실제 사용할 때 조회한다.
  - 설정 방법: @ManyToOne(fetch = FetchType.LAZY)





### 8.2.1 즉시 로딩

> *즉시 로딩 설정*

```java
@Entity
public class Member {
  
  ...
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "team_id")
  private Team team;
  ...
    
}
```

> *즉시 로딩 실행 코드*

```java
Member member = em.find(Member.class, "member1");
Team team = member.getTeam();
```

이때 회원과 팀 두 테이블을 조회해야 하므로 쿼리를 2번 실행할 것 같지만, 대부분의 JPA 구현체는 **즉시 로딩을 최적화 하기 위해 가능하면 조인 쿼리를 사용한다.** 이후 getTeam()을 호출하면 이미 로딩된 팀1 엔티티를 반환한다.



### 8.2.2 지연 로딩

> *지연 로딩 설정*

```java
@Entity
public class Member {
  
  ...
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id")
  private Team team;
  ...
    
}
```

> *지연 로딩 실행 코드*

```java
Member member = em.find(Member.class, "member1");
Team team = member.getTeam();
team.getName();
```

`em.find(Member.class, "member1")`를 호출하면 회원만 조회하고 팀은 조회하지 않는다. 대신에 조회한 회원의 team 멤버 변수에 프록시 객체를 넣어둔다.

이 프록시 객체는 실제 사용될 때까지 데이터 로딩을 미룬다. 실제 사용될 때 데이터베이스를 조회해서 프록시 객체를 초기화한다.



### 8.2.3 즉시, 지연 로딩 정리

- **지연 로딩**<sup>LAZY</sup>: 연관된 엔티티를 프록시로 조회한다. 프록시를 실제 사용할 때 초기화하면서 데이터베이스를 조회한다.
- **즉시 로딩**<sup>EAGER</sup>: 연관된 엔티티를 즉시 조회한다. 하이버네이트는 가능하면 조인을 사용해서 한 번에 조회한다.





## 8.3 지연 로딩 활용



![image](https://user-images.githubusercontent.com/43429667/77869988-33926f00-727b-11ea-8687-95a43fea6dce.png)

- 회원은 팀 하나에만 소속할 수 있다. (N:1)
- 회원은 여러 주문내역을 가진다. (1:N)
- 주문내역은 상품정보를 가진다.(N:1)



애플리케이션 로직을 분석해본 결과,

- Member와 Team은 자주 함께 사용되었다. 그래서 즉시 로딩으로 설정했다.
- Order와 Product도 마찬가지다.
- Member와 Order는 가끔 사용되었다. 그래서 지연 로딩으로 설정했다.



### 8.3.1 프록시와 컬렉션 래퍼

하이버네이트는 엔티티를 영속 상태로 만들 때 엔티티에 컬렉션이 있으면 컬렉션을 추적하고 관리할 목적으로 원본 컬렉션을 
하이버네이트가 제공하는 내장 컬렉션으로 변경하는데 이것을 컬렉션 래퍼라고 한다.

```java
Member meber = em.find(Member.class, "member1");
List<Order> orders = member.getOrders();
System.out.println("orders = " + orders.getClass().getName());
// 결과: orders = org.hibernate.collection.internal.PersistentBag
```

엔티티를 지연 로딩하면 프록시 객체를 사용해서 지연 로딩을 수행하지만 컬렉션은 컬렉션 래퍼가 지연 로딩을 처리해준다.
컬렉션은 `member.getOrders().get(0)`처럼 컬렉션에서 실제 데이터를 조회할 때 데이터베이스를 조회해서 초기화한다.



### 8.3.2 JPA 기본 페치 전략

- @ManyToOne, @OneToOne: 즉시 로딩<sup>FetchType.EAGER</sup>
- @OneToMany, @ManyToMany: 지연 로딩<sup>FetchType.LAZY</sup>



연관된 엔티티가 하나면 즉시 로딩, 컬렉션이면 지연 로딩을 사용한다. 컬렉션을 로딩하는 것은 비용이 많이 들고 너무 많은 데이터를 로딩할 수 있기 때문이다.



### 8.3.3 컬렉션에 FetchType.EAGER 사용 시 주의점



- **컬렉션을 하나 이상 즉시 로딩하는 것은 권장하지 않는다.** 일대다 조인은 결과 데이터가 다<sup>N</sup>의 수만큼 증가하게 된다.
  문제는 서로 다른 컬렉션을 2개 이상 조인하면 SQL 실행 결과가 N * M이 되면서 너무 많은 데이터를 반환해 앱 성능이 저하될 수 있다. JPA는 이렇게 조회된 결과를 메모리에서 필터링해서 반환한다. 따라서 2개 이상의 컬렉션을 즉시 로딩으로 
  설정하는 것은 권장하지 않는다.
- **컬렉션 즉시 로딩은 항상 외부 조인**<sup>OUTER JOIN</sup>**을 사용한다.** 다대일 관계인 회원 테이블과 팀 테이블을 조회할 때 회원 테이블의 외래 키에 not null 제약조건을 걸어두면 모든 회원은 팀에 소속되므로 항상 내부 조인을 해도 되지만 반대로 팀 테이블에서 회원 테이블로 일대다 관계를 조인할 때 회원이 한 명도 없는 팀을 내부 조인하면 팀까지 조회되지 않는 문제가 생긴다.
  데이터 제약조건으로도 이런 상황을 막을 수 없기 때문에 JPA는 일대다 관계를 즉시 로딩할 때 항상 외부 조인을 사용한다.



- **ManyToOne, @OneToOne**
  - (optional = false): 내부 조인
  - (optional = true): 외부 조인
- **OneToMany, @ManyToMany**
  - (optional = false): 외부 조인
  - (optional = true): 외부 조인





## 8.4 영속성 전이: CASCADE

특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶으면 영속성 전이<sup>transitive persistence</sup> 기능을 사용하면 된다. JPA는 CASCADE 옵션으로 영속성 전이를 제공한다. 

쉽게 말해서 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장되는 것이다.



### 8.4.1 영속성 전이: 저장

```java
@Entity
public class Parent {
  ...
  @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
  private List<Child> children = new ArrayList<>();
  ...
}
```

이 옵션을 적용하면 간편하게 부모와 자식 엔티티를 한 번에 영속화할 수 있다.

```java
private static void saveWithCascade(final EntityManager em) {
  Child child1 = new Child();
  Child child2 = new Child();
  
  Parent parent = new Parent();
  child1.setParent(parent);
  child2.setParent(parent);
  parent.getChildren().add(child1);
  parent.getChildren().add(child2);
  
  em.persist(parent); // 부모 저장, 연관된 자식들도 저장
}
```

영속성 전이는 연관관계를 매핑하는 것과는 관련이 없다. 단지 엔티티를 영속화할 때 연관된 엔티티를 같이 영속화하는 것이다.
그래서 예제에서도 양방향 연관관계를 직접 추가한 다음 영속 상태로 만든 것을 확인할 수 있다.



### 8.4.2 영속성 전이: 삭제

`CascadeType.REMOVE`로 설정하고 부모 엔티티만 삭제하면 연관된 자식 엔티티도 함께 삭제된다.

```java
Parent findParent = em.find(Parent.class, 1L);
em.remove(findParent);
```

코드를 실행하면 DELETE SQL을 3번 실행하고 부모는 물론 연관된 자식도 모두 삭제한다. 삭제 순서는 외래 키 제약조건을 고려해서 자식을 먼저 삭제하고 부모를 삭제한다.

만약 `CascadeType.REMOVE` 옵션이 없다면 부모 엔티티만 삭제된다. 하지만 데이터베이스의 부모 로우를 삭제하는 순간 자식 테이블에 걸려 있는 외래 키 제약조건으로 인해, 외래 키 무결성 예외가 발생한다.



### 8.4.3 CASCADE의 종류

> *CascadeType enum 클래스*

```java
public enum CascadeType {
  ALL,      // 모두 적용
  PERSIST,  // 영속
  MERGE,    // 병합
  REMOVE,   // 삭제
  REFRESH,
  DETACH
}
```

`cascade = {CascadeType.PERSIST, CascadeType.REMOVE}` 처럼 사용할 수 있다.

참고로 CascadeType.PERSIST, REMOVE는 `em.persist(), em.remove()`를 실행할 때가 아니라 플러시를 호출할 때
전이가 발생한다.



## 8.5 고아 객체

JPA는 부모 엔티티와 연관관계가 끊긴 자식 엔티티를 자동 삭제하는 기능을 제공하는데 이것을 고아<sup>ORPHAN</sup> 객체 제거라 한다.

**부모 엔티티의 컬렉션에서 자식 엔티티의 차몾만 제거하면 자식 엔티티가 자동으로 삭제**되도록 해보자.

> *고아 객체 제거 기능 설정*

```java
@Entity
public class Parent {
  
  @Id @GeneratedValue
  private Long id;
  
  @OneToMany(mappedBy = "parent", orphanRemoval = true)
  private List<Child> children = new ArrayList<>();
  ...
}
```

```java
Parent parent1 = em.find(Parent.class, id);
parent1.getChildren().remove(0); // 자식 엔티티를 컬렉션에서 제거
```

orphanRemoval 옵션으로 인해 컬렉션에서 엔티티를 제거하면 데이터베이스의 데이터도 삭제된다.
고아 객체 제거 기능은 영속성 컨텍스트를 플러시할 때 적용되므로 플러시 시점에 DELETE SQL이 실행된다.

고아 객체 제거는 **참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능**이다.
따라서 이 기능은 특정 엔티티가 개인 소유하는 엔티티에만 적용해야 한다. 만약 삭제한 엔티티를 다른 곳에서도 참조한다면
문제가 발생할 수 있다. 이러한 이유로 oprphanRemoval은 @OneToOne, @OneToMany에만 사용할 수 있다.



고아 객체 제거는 개념적으로 볼 때 부모를 제거하면 자식은 고아가 되므로 부모를 제거하면 자식도 같이 제거된다.
이것은 CascadeType.REMOVE를 설정한 것과 같다.



## 8.6 영속성 전이 + 고아 객체, 생명 주기

일반적으로 엔티티는 `EntityManager.persist()`를 통해 영속화되고 `EntityManager.remove()`를 통해 제거된다.
이것은 엔티티 스스로 생명주기를 관리한다는 뜻이다. 여기서 `CascadeType.ALL `+ `orphanRemoval = true` 를 동시에 
활성화하면 부모 엔티티를 통해서 자식의 생명주기를 관리할 수 있다.

- 자식을 저장하려면 부모에 등록만 하면 된다<sup>CASCADE</sup>.

  ```java
  Parent parent = em.find(Parent.class, parentId);
  parent.addChild(child1);
  ```

- 자식을 삭제하려면 부모에서 제거하면 된다<sup>orphanRemoval</sup>

  ```java
  Parent parent = em.find(Parent.class, parentId);
  parent.getChildren().remove(removeChild);
  ```





## 8.7 정리

이 장에서 다룬 주요 내용을 정리해보자.

- JPA 구현체들은 객체 그래프를 마음껏 탐색할 수 있도록 지원하는데 이때 프록시 기술을 사용한다.
- 객체를 조회할 때 연관된 객체를 즉시 로딩하는 방법을 즉시 로딩이라 하고, 연관된 객체를 지연해서 로딩하는 방법을 
  지연 로딩이라 한다.
- 객체를 저장하거나 삭제할 때 연관된 객체도 함께 저장하거 삭제할 수 있는데 이것을 영속성 전이라 한다.
- 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제하려면 고아 객체 제거 기능을 사용하면 된다.



[실전 예제 - 연관관계 관리](https://github.com/MoochiPark/jpa/tree/master/chapter08/src)