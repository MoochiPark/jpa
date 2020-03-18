# Chapter 06. 다양한 연관관계 매핑

이번 장에서는 다양한 연관관계를 다룬다. 그 전에 앞 장의 내용을 정리해보자.

- 다중성
- 단방향, 양방향
- 연관관계의 주인



먼저 연관관계가 있는 두 엔티티가 일대일 관계인지 일대다 관계인지 다중성을 고려해야 한다.
다음으로 두 엔티티 중 한쪽만 참조하는 단방향 관계인지 서로 참조하는 양방향 관계인지 고려해야 한다.
마지막으로 양방향 관계면 연관관계의 주인을 정해야 한다.

- **다중성**

  연관관계에는 다음과 같은 다중성이 있다.

  - 다대일<sup>@ManyToOne</sup>
  - 일대다<sup>@OneToMany</sup>
  - 일대일<sup>@OneToOne</sup>
  - 다대다<sup>@ManyToMany</sup>

  다중 성을 판단하기 어려울 떄는 반대방향을 생각해보면 된다. 참고로 일대다의 반대방향은 항상 다대일이고,
  일대일의 반대방향은 항상 일대일이다.
  보통 다대일과 일대다 관계를 가장 많이 사용하고 다대다 관계는 실무에서 거의 사용하지 않는다.

- **단방향, 양방향**

  테이블은 외래 키 하나로 조인을 사용해서 양방향으로 쿼리가 가능하므로 사실상 방향이라는 개념이 없다.
  반면에 객체는 참조용 필드를 가지고 있는 객체만 연관된 객체를 조회할 수 있다.
  객체 관계에서 한 쪽만 참조하는 것을 단방향 관계라 하고, 양쪽이 서로 참조하는 것을 양방향 관계라고 한다.

- **연관관계의 주인**

  테이블의 연관관계를 관리하는 포인트는 외래 키 하나다. 반면에 엔티티를 양방향으로 매핑하면 A → B, B → A 2곳에서
  서로 참조한다. 따라서 객체의 연관관계를 관리하는 포인트는 2곳이다.

  JPA는 두 객체 연관관계 중 하나를 정해서 데이터베이스 외래 키를 관리하는데 이것을 연관관계의 주인이라 한다.
  따라서 A → B 또는 B → A 둘 중 하나를 정해서 외래 키를 관리해야 한다.
  외래 키를 가진 테이블과 매핑한 엔티티가 외래 키를 관리하는게 효율적이므로 보통 이곳을 연관관계의 주인으로 선택한다.
  주인이 아닌 방향은 외래 키를 변경할 수 없고 읽기만 가능하다.

  연관관계의 주인은 mappedBy 속성을 사용하지 않는다. 연관관계의 주인이 아니면 mappedBy 속성을 사용하고
  연관관계의 주인 필드 이름을 값으로 입력해야 한다.



지금부터 다중성과 단방향, 양방향을 고려한 가능한 모든 연관관계를 하나씩 알아보자.

- 다대일: 단방향, 양방향
- 일대다: 단방향, 양방향
- 일대일: 주 테이블 단방향, 양방향
- 일대일: 대상 테이블 단방향, 양방향
- 다대다: 단방향, 양방향

참고로 다중성은 왼쪽을 연관관계의 주인으로 정했다. 다대일 양방향이라 하면 다<sup>N</sup>가 연관관계의 주인이다.



## 6.1 다대일

다대일 관계의 반대 방향은 항상 일대다 관계고 일대다 관계의 반대 방향은 항상 다대일 관계다.
데이터베이스 테이블의 일, 다 관계에서 외래키는 항상 다쪽에 있다. 따라서 객체 양방향 관계에서 연관관계의 주인은 항상
다쪽이다. 예를들어 회원<sup>N</sup>과 팀<sup>1</sup>이 있으면 회원 쪽이 연관관계의 주인이다.



### 6.1.1 다대일 단방향 [N:1]

회원, 팀 엔티티 코드를 통해 다대일 단방향 연관관계를 알아보자.

> *회원 엔티티*

<script src="https://gist.github.com/2be13eb5cb1bbaa0584d36bd018a4180.js"></script>

> *팀 엔티티*

<script src="https://gist.github.com/d9cddd4241cc8105ba8786c37719e660.js"></script>

회원은 Member.team으로 팀 엔티티를 참조할 수 있지만 반대로 팀에는 회원을 참조하는 필드가 없다.
따라서 회원과 팀은 다대일 단방향 연관관계이다.

```java
@ManyToOne
@JoinColumn(name = "team_id")
private Team team;
```

@JoinColumn(name = "team_id")를 사용해서 Member.team 필드를 team_id 외래 키와 매핑했다.
따라서 Member.team 필드로 회원 테이블의 team_id 외래 키를 관리한다. 





### 6.1.2 다대일 양방향 [N:1, 1:N]

다대일 양방향의 객체 연관관계에서 실선이 연관관계의 주인<sup>Member.team</sup>이고 점선<sup>Team.members</sup>은 주인이 아니다.

> *다대일 양방향*

![image](https://user-images.githubusercontent.com/43429667/76959933-e5699b80-695d-11ea-9cc9-f09b1ee4cb91.png)

> *다대일 양방향 회원 엔티티*

<script src="https://gist.github.com/7dc350c5264e1cb52136dba33232c776.js"></script>

> *다대일 양방향 팀 엔티티*

<script src="https://gist.github.com/518842055fc8506ed5e03231d494c72f.js"></script>

- **양방향은 외래 키가 있는 쪽이 연관관계의 주인이다.**

  일대다와 다대일 연관관계는 항상 다<sup>N</sup>에 외래 키가 있다. 여기서는 다쪽인 MEMBER 테이블이 외래 키를 가지고 있으므로
  Member.team이 연관관계의 주인이다. JPA는 외래 키를 관리할 때 연관관계의 주인만 사용한다. 주인이 아닌 Team.members는 조회를 위한 JPQL이나 객체 그래프를 탐색할 때 사용한다.



- **양방향 연관관계에서는 항상 서로를 참조해야 한다.**

  어느 한 쪽만 참조하면 양방향 연관관계가 성립하지 않는다. 항상 서로를 참조하게 하려면 연관관계 편의 메서드를 작성하는 것이 좋다. 편의 메서드는 한 곳 또는 양쪽 다 작성할 수 있는데, 양쪽에 다 작성할 경우 무한루프에 빠지므로 주의해야 한다.





## 6.2 일대다

일대다 관계는 다대일 관계의 반대 방향이다. 일대다 관계는 엔티티를 하나 이상 참조할 수 있으므로 자바 컬렉션 중에 하나를 사용해야 한다.



### 6.2.1 일대다 단방향 [1:N]

팀은 회원들을 참조하지만 반대로 회원은 팀을 참조하지 않으면 둘의 관계는 단방향이다. 

> *일대다 단방향*

![image](https://user-images.githubusercontent.com/43429667/76960444-d0d9d300-695e-11ea-979e-70077bddd54c.png)

일대다 단방향 관계는 약간 특이한데 팀 엔티티의 Team.members로 회원 테이블의 team_id 외래 키를 관리한다.
보통 자신이 매핑한 테이블의 외래 키를 관리하는데, 이 매핑은 반대쪽 테이블에 있는 외래 키를 관리한다.
 그럴 수 밖에 없는 것이 일대다 관계에서 외래 키는 항상 다쪽 테이블에 있다. 하지만 다 쪽인 Member 엔티티에는 외래 키를
매핑할 수 있는 참조 필드가 없다. 대신에 반대쪽인 Team 엔티티에만 참조 필드인 members가 있다. 따라서 반대편 테이블의 외래 키를 관리하는 특이한 모습이 나타난다.

> *일대다 단방향 팀 엔티티*

<script src="https://gist.github.com/ec836ac5f69267a5c16b7cd5f0d1531e.js"/>

> *일대다 단방향 회원 엔티티*

<script src="https://gist.github.com/9377008f291abfba8c3b7751cc9e14ab.js"></script>

일대다 단방향 관계를 매핑할 때는 @JoinColumn을 명시해야 한다. 그렇지 않으면 JPA는 연결 테이블을 중간에 두고 연관관계를 관리하는 조인 테이블 전략을 기본으로 사용해서 매핑한다. 조인 테이블은 7장에서 다룬다.

- **일대다 단방향 매핑의 단점**

  본인 테이블에 외래 키가 있으면 엔티티의 저장과 연관관계 처리를 INSERT 한 번으로 끝낼 수 있지만, 다른 테이블에 있다면
    연관관계 처리를 위한 UPDATE를 추가로 실행해야 한다. 

  ```java
  Member member1 = new Member("member1");
  Member member2 = new Member("member2");
  
  Team team1 = new Team("team1");
  team.getMembers().add(member1);
  team.getMembers().add(member2);
  
  em.persist(member1); // INSERT-member1
  em.persist(member2); // INSERT-member2
  em.persist(team1);   // INSERT-team1, UPDATE-member1.fk, member2.fk
  
  transaction.commit();
  ```

  위 예제를 실행한 결과 SQL은 다음과 같다.

  ```sql
  INSERT INTO member (member_id, username) VALUES (null, ?)
  INSERT INTO member (member_id, username) VALUES (null, ?)
  INSERT INTO team (team_id, name) VALUES (null, ?)
  UPDATE member SET team_id=? WHERE member_id=?;
  UPDATE member SET team_id=? WHERE member_id=?;
  ```

  Member 엔티티는 Team 엔티티를 모르고 연관관계에 대한 정보는 Team.members가 관리하므로 Member 엔티티를 저장할 떄는 MEMBER 테이블의 team_id 외래 키에 아무 값도 저장되지 않는다. 대신 Team 엔티티를 저장할 때 Team.members의 참조 값을 확인해서 회원 테이블에 있는 team_id 외래 키를 업데이트한다.



- **일대다 단방향 매핑보다는 다대일 양방향 매핑을 사용하자**

  이  경우 다른 테이블의 외래 키를 관리해야 하므로 성능 문제도 있지만 관리가 어려워진다.





### 6.2.2 일대다 양방향 [1:N, N:1]

일대다 양방향 매핑은 존재하지 않는다. 대신 다대일 양방향 매핑을 사용해야 한다.

더 정확히 말하자면 양방향 매핑에서 @OneToMany는 연관관계의 주인이 될 수 없다. 관계형 데이터베이스 특성상
일대다, 다대일 관계는 항상 다 쪽이  외래 키가 있다. 따라서 연관관계의 주인은 @ManyToOne을 사용한 곳이다.
이런 이유로 @ManyToOne에는 mappedBy 속성이 없다.

일대다 양방향 매핑이 완전히 불가능한 것은 아니지만 되도록 다대일 양방향 매핑을 사용하자.







## 6.3 일대일 [1:1]

일대일 관계는 양쪽이 서로 하나의 관계만 가진다. 예를 들어 회원과 사물함의 관계다.

일대일 관계는 다음과 같은 특징이 있다.

- 일대일 관계는 그 반대도 일대일 관계다.
- 주 테이블이나 대상 테이블 중 어느 곳이나 외래 키를 가질 수 있다.



일대일 관계는 주 테이블이나 대상 테이블 중 누가 외래 키를 가질 지 선택해야 한다.

- **주 테이블에 외래 키**

  주객체가 대상 객체를 참조하는 것처럼 주 테이블에 외래 키를 두고 대상 테이블을 참조한다.
  외래 키를 객체 참조와 비슷하게 사용할 수 있어 객체지향 개발자들이 선호하는 방식이다.
  이 방법의 장점은 주 테이블이 외래 키를 가지고 있으므로 주 테이블만 확인해도 대상 테이블과 
  관계가 있는지 알 수 있다.

- **대상 테이블에 외래 키**

  데이터베이스 개발자들은 보통 대상 테이블에 외래 키를 두는 것을 선호한다. 이 방법의 장점은 
  테이블 관계를 일대일에서 일대다로 변경할 때 테이블 구조를 그대로 유지할 수 있다.





### 6.3.1 주 테이블에 외래 키

일대일 관계를 구성할 때 객체지향 개발자들은 주 테이블에 외래 키가 있는 것을 선호한다. JPA도 주 테이블에 외래 키가 있으면
좀 더 편리하게 매핑할 수 있다. 주 테이블에 외래 키가 있는 단방향 관계를 보고 양방향 관계도 보자.



#### 단방향

회원과 사물함의 일대일 단방향 관계를 알아보자.

> *일대일 주 테이블에 외래 키, 단방향*

![image](https://user-images.githubusercontent.com/43429667/76960849-a3415980-695f-11ea-921c-b42569dae21c.png)

```java
@Entity
public class Member {
  
  @Id @GeneratedValue
  @Column(name = "member_id")
  private Long id;
  
  private String username;
  
  @OneToOne
  @JoinColumn(name = "locked_id")
  private Locker locker;
  ...
}

@Entity
public class Locker {
  
  @Id @GeneratedValue
  @Column(name = "locker_id")
  private Long id;
  
  private String name;
  ...
}
```

일대일 관계이므로 @OneToOne을 사용했고 데이터베이스에는 locker_id 외래 키에 유니크 제약 조건을 추가했다.
이 관계는 다대일 단방향<sup>@ManyToOne</sup>과 거의 비슷하다.



#### 양방향

> *일대일 주 테이블에 외래 키, 양방향*

```java
@Entity
public class Member {
  
  @Id @GeneratedValue
  @Column(name = "member_id")
  private Long id;
  
  private String username;
  
  @OneToOne
  @JoinColumn(name = "locker_id")
  private Locker locker;
  ...
}

@Entity
public class Locker {
  
  @Id @GeneratedValue
  @Column(name = "locker_id")
  private Long id;
  
  private String name;
  
  @OneToOne(mappedby = "locker")
  private Member member;
  ...
}
```

일대일 매핑에서 대상 테이블에 외래 키를 두고 싶으면 이렇게 양방향으로 매핑한다.  주 엔티티인 Member 엔티티 대신 
대상 엔티티인 Locker를 연관관계의 주인으로 만들어서 LOCKER 테이블의 외래 키를 관리하도록 했다.



## 6.4 다대다

관계형 데이터베이스는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없다. 그래서 보통 다대다 관계를 일대다, 다대일 관계로 풀어내는 연결 테이블을 사용한다. 예를 들어 회원들은 상품을 주문한다고 하자. 반대로 상품들은 회원들에 의해 주문될 때 둘은 
다대다 관계다. 

그래서 중간에 연결 테이블을 추가해야 한다.

> *N:M 다대다 연결 테이블*

![image](https://user-images.githubusercontent.com/43429667/76961196-498d5f00-6960-11ea-9991-586e2e09a3d0.png)

그런데 객체는 테이블과 다르게 객체 2개에서 컬렉션을 사용해 다대다 관계를 만들 수 있다.

@ManyToMany를 사용하면 이런 다대다 관계를 편리하게 매핑할 수 있다.



### 6.4.1 다대다: 단방향

> *다대다 단방향 회원 엔티티*

```java
@Entity
public class Member {
  
  @Id @Column(name = "member_id")
  private String id;
  
  private String username;
  
  @ManyToMany
  @JoinTable(name = "member_product",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
  private List<Product> products = new ArrayList<>();
  ...
}
```

> *다대다 단방향 상품 엔티티*

```java
@Entity
public class Product {
  
  @Id @Column(name = "product_id")
  private String id;
  
  private String name;
  ...
}
```

회원, 상품 엔티티를 @ManyToMany와 @JoinTable을 사용해서 연결 테이블을 바로 매핑했다.
따라서 회원과 상품을 연결하는 Member_Product 엔티티 없이 매핑을 완료할 수 있다.

연결 테이블을 매핑하는 @JoinTable의 속성을 정리해보자.

- @JoinTable.name: 연결 테이블을 지정한다.
- @JoinTable.joinColumns: 현재 방향인 회원과 매핑할 조인 컬럼 정보를 지정한다.
- @JoinTable.inverseJoinColumns: 반대 방향인 상품과 매핑할 조인 컬럼 정보를 지정한다.



@ManyToMany로 매핑한 덕분에 다대다 관계를 사용할 때는 연결 테이블을 신경 쓰지 않아도 된다.



> *다대다 관계 저장 예제*

```java
public void save() {
  Product productA = new Product();
  productA.setId("productA");
  productA.setName("상품A");
  em.persist(productA);
  
  Member member1 = new Member();
  member1.setId("member1");
  member1.setUsername("회원1");
  member1.getProducts().add(productA); // 연관관계 설정
  em.persist(member1);
}
```

회원1과 상품A의 연관관계를 설정했으므로 회원1을 저장할 때 연결 테이블에도 값이 저장된다.

> *실행 SQL*

```sql
INSERT INTO PRODUCT ...
INSERT INTO MEMBER ...
INSERT INTO MEMBER_PRODUCT ...
```

> *다대다 관계 탐색 예제*

```java
public void find() {
  Member member = em.find(Member.class, "member1");
  List<Product> products = member.getProducts(); //	객체 그래프 탐색
  products.forEach(p -> System.out.println("product.name = " + p.getName()));
}
```

member.getProducts()를 호출해서 상품 이름을 출력하면 다음 SQL이 실행된다.

```sql
SELECT * 
FROM MEMBER_PRODUCT MP, PRODUCT P
WHERE MP.PRODUCT_ID = P.PRODUCT_ID AND MP.MEMBER_ID=?
```

실행된 SQL을 보면 연결 테이블인 MEMBER_PRODUCT와 상품 테이블을 조인해서 연관된 상품을 조회한다.
@ManyToMany 덕분에 복잡한 다대다 관계를 애플리케이션에서는 아주 단순하게 사용할 수 있다.



### 6.4.2 다대다: 양방향

다대다 매핑이므로 역방향도 @ManyToMany를 사용한다. 그리고 양쪽 중에 원하는 곳에 mappedBy로 연관관계의 주인을
지정한다. 

> *역방향 추가*

```java
@Entity
public class Product {
  
  @Id
  private String id;
  
  @ManyToMany(mappedBy = "products") // 역방향 추가
  private List<Member> members;
  ...
}
```

다대다 양방향 연관관계는 다음처럼 설정한다.

```java
member.getProducts().add(product);
product.getMembers().add(member);
```

양방향 연관관계에서는 연관관계 편의 메서드를 사용하는 것이 편리하다.

```java
public void addProduct(final Product product) {
  ...
  product.add(product);
  product.getMembers().add(this);  
}
```

양방향 연관관계이므로 다음처럼 역방향으로 객체 그래프를 탐색할 수 있다.

```java
public void findInverse() {
  Product product = em.find(Product.class, "productA");
  List<Member> members = product.getMembers();
  members.forEach(m -> System.out.println("member = " + member.getUsername()));
}
```





### 6.4.3 다대다: 매핑의 한계와 극복, 연결 엔티티 사용

@ManyToMany를 사용하면 연결 테이블을 자동으로 처리해주므로 도메인 모델이 단순해지고 여러 가지로 편리하지만,
이 매핑을 실무에서 사용하는데에는 한계가 있다.

예를 들어 회원이 상품을 주문하면 보통은 연결 테이블에 주문 수량이나 주문한 날짜 같은 컬럼이 더 필요하다.

> *연결 테이블에 필드 추가*

![image](https://user-images.githubusercontent.com/43429667/76953756-9407df00-6952-11ea-9029-8e4b70fddd3a.png)

하지만 추가할 경우 더이상 @ManyToMany를 사용할 수 없다. 주문 엔티티나 상품 엔티티에는 추가한 컬럼들을
매핑할 수 없기 때문이다.

결국 연결 테이블을 매핑하는 연결 엔티티를 만들고 이곳에 추가한 컬럼들을 매핑해야 한다.
그리고 엔티티 간의 관계도 테이블처럼 다대다에서 일대다, 다대일 관계로 풀어야 한다.

> *다대다를 푸는 연결 엔티티*

![image](https://user-images.githubusercontent.com/43429667/76941228-fd7df280-693e-11ea-99af-5800de6d9d7b.png)

연결 테이블에 주문 수량과 주문 날짜 컬럼을 추가했다.

> *회원 엔티티*

```java
@Entity
public class Member {
  
  @Id @Column(name = "member_id")
  private String id;
  
  // 역방향
  @OneToMany(mappedBy = "member")
  private List<MemberProduct> memberProducts;
  
  ...
}
```

회원과 회원상품을 양방향 관계로 만들었다. 회원 상품<sup>MemberProduct</sup> 엔티티 쪽이 외래 키를 가지고 있으므로 연관관계의 주인이다.
따라서 주인의 아닌 회원의 Member.memberProducts에는 mappedBy를 사용했다.

> *상품 엔티티*

```java
@Entity
public class Product {
  
  @Id @Column(name = "product_id")
  private String id;
  
  private String name;
  
  ...
}
```

상품 엔티티에선 회원상품 엔티티로 객체 그래프 탐색이 필요치 않다고 판단하여 연관관계를 만들지 않았다.

다음으로 가장 중요한 회원상품 엔티티와 식별자 클래스를 보자.

> *회원상품 엔티티*

```java
@Entity
@IdClass(MemberProductId.class)
public class MemberProduct {
  
  @Id
  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member; // MemberProductId.member와 연결
  
  @Id
  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product; // MemberProductId.product와 연결
  
  private int orderAmount;
  
  ...
  
}
```

> *회원 상품 식별자 클래스*

```java
public class MemberProductId implements Serializable {
  
  private String member;  // MemberProduct.member와 연결
  private String product; // MemberProduct.product와 연결
  
  @Override
  public boolean equals(final Object o) {...}
  
  @Override
  public int hashCode() {...}
 
}
```

회원상품 엔티티를 보면 기본 키를 매핑하는 @Id와 외래 키를 매핑하는 @JoinColumn을 동시에 사용해서 기본 키와 외래 키를 한번에 매핑했다. 그리고 @IdClass를 사용해서 복합 기본 키를 매핑했다.

- **복합 기본 키**

  회원상품 엔티티는 기본 키가 member_id와 product_id로 이루어진 복합 기본 키다. JPA에서 복합 키를 사용하려면
  별도의 식별자 클래스를 만들어야 한다. 엔티티에 @IdClass를 사용해서 식별자 클래스를 지정하면 된다.

  복합 키를 위한 식별자 클래스는 다음과 같은 특징이 있다.

  - 복합 키는 별도의 식별자 클래스로 만들어야 한다.
  - equals와 hashCode 메서드를 구현해야 한다.
  - 기본 생성자가 있어야 한다.
  - 식별자 클래스는 public이어야 한다.
  - @IdClass를 사용하는 방법 외에 @EmbeddedId를 사용하는 방법도 있다.

- **식별 관계**

  회원상품은 회원과 상품의 기본 키를 받아서 자신의 기본 키로 사용한다. 
  이렇게 부모 테이블의 기본 키를 받아서 자신의 기본 키 + 외래 키로 사용하는 것을 데이터베이스 용어로 식별 관계라 한다.

이렇게 구성한 관계를 어떻게 저장하는지 보자.

> *저장 예제*

```java
public void save() {
  // 회원 저장
  Member member1 = new Member();
  member1.setId("member1");
  member1.setUsername("회원1");
  em.persist(member1);
  
  // 상품 저장
  Product productA = new Product();
  productA.setId("productA");
  productA.setName("상품1");
  em.persist(productA);
  
  // 회원상품 저장
  MemberProduct memberProduct = new MemberProduct();
  memberProduct.setMember(member1);    //주문 회원 - 연관관계 설정
  memberProduct.setProduct(productA);  //주문 상품 - 연관관계 설정
  memberProduct.setOrderAmount(2);     //주문 수량
  
  em.persist(memberProduct);
}
```

회원 상품 엔티티는 데이터베이스에 저장될 때 연관된 회원의 식별자와 상품의 식별자를 가져와서 자신의 기본 키 값으로 사용한다.

> *조회 예제*

```java
public void find() {
  // 기본 키 값 생성
  MemberProductId memberProductId = new MemberProductId();
  memberProductId.setMember("member1");
  memberProductId.setProduct("productA");
  
  MemberProduct memberProduct = em.find(MemberProduct.class, memberProductId);
  Member member = memberProduct.getMember();
  Product product = memberProduct.getProduct();
  
  System.out.println("member = " + member.getUsername());
  System.out.println("produdct = " + proudct.getName());
  System.out.println("orderAmount = " + memberProduct.getOrderAmount());
}
```

지금까지는 기본 키가 단순해서 기본 키를 위한 객체를 사용하는 일은 없었는데 복합 키가 되면 이야기가 달라진다.
복합 키는 항상 식별자 클래스를 만들어야 한다. em.find()를 보면 생성한 식별자 클래스로 엔티티를 조회한다.

복합 키를 사용하는 방법은 복잡하다.

- 식별자 클래스
- @IdClass 또는 @EmbeddedId 사용
- 식별자 클래스 내 equals, hashCode 구현



복합 키를 사용하지 않고 간단히 다대다 관계를 구현하는 방법을 알아보자.



### 6.4.4 다대다: 새로운 기본 키 사용

추천하는 기본 키 생성 전략은 데이터베이스에서 자동으로 생성해주는 대리 키를 Long 값으로 사용하는 것이다.
이것의 장점은 간편하고 거의 영구히 쓸 수 있으며 비즈니스에 의존하지 않는다.
그리고 ORM 매핑 시에 복합 키를 만들지 않아도 되므로 간단히 매핑할 수 있다.

이번엔 연결 테이블에 새로운 기본 키를 사용해보자. 그리고 회원상품<sup>MemberProduct</sup>보다는 주문<sup>Order</sup>가 어울리니 변경하자.

> ORDER는 일부 데이터베이스<sup>postgresql 포함</sup>에서 예약어로 잡혀 있으므로 ORDERS를 사용하기도 한다.



> *N:M 다대다 새로운 기본 키*

![image](https://user-images.githubusercontent.com/43429667/76956154-d6331f80-6956-11ea-940a-38fde0a4d832.png)

​	새로운 order_id라는 새로운 기본 키를 하나 만들고 member_id, product_id 컬럼은 외래 키로만 사용한다.

> *주문<sup>Orders</sup> 예제*

```java
@Entity
public class Orders {
  
  @Id @GeneratedValue
  @Column(name = "order_id")
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;
  
  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;
  
  private int orderAmount;
  ...
}
```

대리 키를 사용함으로써 이전에 보았던 식별 관계에 복합 키를 사용하는 것보다 매핑이 단순하고 이해하기 쉽다.
회원 엔티티와 상품 엔티티는 변경 사항이 없다.

> 회원, 상품 엔티티

```java
@Entity
public class Member {
  
  @Id @Column(name = "member_id")
  private String id;
  
  // 역방향
  @OneToMany(mappedBy = "member")
  private List<MemberProduct> memberProducts;
  
  ...
}

@Entity
public class Product {
  
  @Id @Column(name = "product_id")
  private String id;
  
  private String name;
  
  ...
}
```



> *저장, 조회 예제*

```java
public void save() {
  // 회원 저장
  Member member1 = new Member();
  member1.setId("member1");
  member1.setUsername("회원1");
  em.persist(member1);
  
  // 상품 저장
  Product productA = new Product();
  productA.setId("productA");
  productA.setName("상품1");
  em.persist(productA);
  
  // 회원상품 저장
  Orders orders = new Orders();
  orders.setMember(member1);    //주문 회원 - 연관관계 설정
  orders.setProduct(productA);  //주문 상품 - 연관관계 설정
  orders.setOrderAmount(2);     //주문 수량
  
  em.persist(orders);
}

public void find() {
  Long orderId = 1L;
  Orders orders = em.find(Orders.class, orderId);
  
  Member member = orders.getMember();
  Producut product = orders.getProduct();
  
  System.out.println("member = " + member.getUsername());
  System.out.println("produdct = " + proudct.getName());
  System.out.println("orderAmount = " + orders.getOrderAmount());
}
```

식별자 클래스를 사용하지 않으므로 코드가 단순해졌다. 이렇게 새로운 기본 키를 사용해서 다대다 관계를 풀어내는 것도 좋은 방법이다.



### 6.4.5 다대다 연관관계 정리

다대다 관계를 일대다 다대일 관계로 풀어내기 위해 연결 테이블을 만들 때 식별자를 어떻게 구성할지 선택해야 한다.

데이터베이스 설계에서는 다음처럼 분류한다.

- **식별 관계**: 받아온 식별자를 기본 키 + 외래 키로 사용한다.
- **비식별 관계**: 받아온 식별자는 외래 키로만 사용하고 새로운 식별자를 추가한다.



객체 입장에서보면 비식별 관계를 사용하는 것이 복합 키를 위한 식별자 클래스를 만들지 않아도 되므로 편리하게 ORM 매핑을 할 수 있다. 이런 이유로 식별 관계보다는 비식별 관계를 추천한다.<sup>7장에서 자세히</sup>



다음 장은 상속, 복합 키 같은 고급 매핑에 대해서 설명한다.



[실전 예제 - 다양한 연관관계 매핑](https://github.com/MoochiPark/jpa/tree/master/chapter06/src)