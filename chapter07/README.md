# Chapter 07. 고급 매핑

이 장에서 배울 고급 매핑은 다음과 같다.

- **상속 관계 매핑**: 객체의 상속 관계를 어떻게 데이터베이스에 매핑하는지 배운다.
- **@MappedSuperclass**: 등록일, 수정일 같이 여러 엔티티에서 공통으로 사용하는 매핑 정보만 상속 받고 싶으면
  이 기능을 사용하면 된다.
- **복합 키와 식별 관계 매핑**: 데이터베이스의 식별자가 하나 이상일 때 매핑하는 방법을 다룬다.
  그리고 데이터베이스 설계에서 이야기하는 식별 관계와 비식별 관계에 대해서도 다룬다.
- **조인 테이블**: 테이블은 외래 키 하나로 연관관계를 맺을 수 있지만 연관관계를 관리하는 연결 테이블을 두는 방법도 있다.
  여기서는 이 연결 테이블을 매핑하는 방법을 다룬다.
- **엔티티 하나에 여러 테이블 매핑하기**: 보통 엔티티 하나에 테이블 하나를 매핑하지만 엔티티 하나에 여러 테이블을 매핑하는 방법도 있다. 





## 7.1 상속 관계 매핑

관계형 데이터베이스에는 객체지향 언어에서 다루는 상속이라는 개념이 없다. 
대신 슈퍼타입 서브타입 관계<sup>Super-Type Sub-Type Relationship</sup>라는 모델링 기법이 객체의 상속 개념과 가장 유사하다.
ORM에서의 상속 관계 매핑은 객체의 상속 구조와 데이터베이스의 슈퍼타입 서브타입 관계를 매핑하는 것이다.

> *슈퍼타입 서브타입 논리 모델*

![image](https://user-images.githubusercontent.com/43429667/76974938-2ae59300-6975-11ea-92b0-3a7843f0e2d2.png)



> *객체 상속 모델*

![image](https://user-images.githubusercontent.com/43429667/76975202-929bde00-6975-11ea-87cf-e748307619cf.png)

슈퍼타입 서브타입 논리 모델을 실제 물리 모델인 테이블로 구현할 때는 3가지 방법이 있다.

- **각각의 테이블로 변환**: 각각의 테이블로 만들고 조회할 때 조인을 사용. JPA에서는 **조인 전략** 이라고 한다.
- **통합 테이블로 변환**: 테이블 하나만 사용해서 통합한다. JPA에선 **단일 테이블 전략**이라 한다.
- **서브타입 테이블로 변환**: 서브 타입마다 하나의 테이블을 만든다. JPA에선 **구현 클래스마다 테이블 전략**이라 한다.



위 그림의 객체 상속 모델을 위 3가지 방법으로 매핑해보자.



### 7.1.1 조인 전략

조인 전략<sup>Joined Strategy</sup>은 엔티티 각각을 모두 테이블로 만들고 자식 테이블이 부모 테이블의 기본 키를 받아서 기본 키 + 외래 키로 사용하는 전략이다. 따라서 조회할 때 조인을 자주 사용한다. 
이 전략을 사용할 때 주의할 점은 객체는 타입으로 구분할 수 있지만 테이블은 타입의 개념이 없으므로 타입을 구분하는 컬럼을
추가해야 한다. (여기서는 DTYPE 컬럼을 구분 컬럼으로 사용한다.)

> *JOINED TABLE*

![image](https://user-images.githubusercontent.com/43429667/77292212-334a1f00-6d23-11ea-8595-90ed127328ef.png)

> *조인 전략 매핑 코드*

<script src="https://gist.github.com/3d05ad5c2159600649c4bfb5dd3f294e.js"></script>

<script src="https://gist.github.com/bc16f95894c7e6041565828c87821f37.js"></script>

매핑 정보를 분석해보자.

- @Inheritance(strategy = InheritanceType.JOINED): 상속 매핑은 부모 클래스에 @Inheritance를 사용해야 한다.
  그리고 매핑 전략을 조인 전략으로 지정해 주었다.
- @DiscriminatorColumn(name = "DTYPE"): 부모 클래스에 구분 컬럼을 지정한다. 이 컬럼으로 저장된 자식 테이블을 
  구분할 수 있다. 기본값이 DTYPE이므로 @DiscriminatorColumn으로 줄여서 사용해도 된다.
- @DiscriminatorValue("M"): 엔티티를 저장할 때 구분 컬럼에 입력할 값을 지정한다. 만약 영화 엔티티를 저장하면 구분 컬럼인 DTYPE에 M이 저장된다.



기본 값으로 자식 테이블은 부모 테이블의 ID 컬럼명을 그대로 사용하는데, 만약 자식 테이블의 기본 키 컬럼명을 변경하고 싶으면
@PrimaryKeyJoinColumn을 사용하면 된다.

<script src="https://gist.github.com/d55bff911b1cde6710ed08d443f0a9cf.js"></script>

BOOK 테이블의 item_id 기본 키 컬럼명을 book_id로 변경했다.

조인 전략을 정리해보자.

- **장점**
  - 테이블이 정규화된다.
  - 외래 키 참조 무결성 제약조건을 활용할 수 있다.
  - 저장공간을 효율적으로 사용한다.
- **단점**
  - 조회할 때 조인이 많이 사용되므로 성능이 저하될 수 있다.
  - 조회 쿼리가 복잡하다.
  - 데이터를 등록하는 INSERT를 두 번 실행한다.

- **특징**
  - JPA 표준 명세는 구분 컬럼을 사용하도록 하지만 하이버네이트를 포함한 몇몇 구현체는 구분 컬럼<sup>@DiscriminatorColumn</sup>없이도 동작한다.
- **관련 애노테이션**
  - @PrimaryKeyJoinColumn, @DiscriminatorColumn, @DiscriminatorValue



### 7.1.2 단일 테이블 전략

단일 테이블 전략<sup>Single-Table Strategy</sup>은 이름 그래도 테이블을 하나만 사용한다. 그리고 구분 컬럼<sup>DTYPE</sup>으로 어떤 자식 데이터가 저장되었는지 저장한다. 조회할 때 조인을 사용하지 않으므로 일반적으로 가장 빠르다. 

> *SINGLE TABLE*

![image](https://user-images.githubusercontent.com/43429667/77297547-cf2c5880-6d2c-11ea-9ba2-51b30498d7b7.png)

이 전략을 사용할 때 주의점은 자식 엔티티가 매핑한 컬럼은 모두 null을 허용해야 한다는 점이다.
Book 엔티티를 저장하면 ITEM 테이블의 AUTHOR, ISBN 컬럼을 제외한 컬럼은 사용하지 않으므로 null이 입력되기 때문이다.

> *단일 테이블 전략 매핑*

<script src="https://gist.github.com/ccaa73e246a7504591c188ae5c7f365a.js"></script>

<script src="https://gist.github.com/29f826813d947edd502a91f415a1fbb5.js"></script>

단일 테이블 전략은 테이블 하나에 모든 것을 통합하므로 구분 컬럼을 필수로 사용해야 한다. 단일 테이블 전략의 장단점은
하나의 테이블을 사용하는 특징과 관련이 있다.

- **장점**
  - 조인이 필요 없으므로 일반적으로 조회 성능이 빠르다.
  - 조회 쿼리가 단순하다.
- **단점**
  - 자식 엔티티가 매핑한 컬럼은 모두 null을 허용해야 한다.
  - 단일 테이블에 모든 것을 저장하므로 테이블이 커질 수 있다. 상황에 따라서 조회 성능이 오히려 느려질 수 있다.
- **특징**
  - 구분 컬럼을 꼭 사용해야 한다. 따라서 @DiscriminatorColumn을 꼭 설정해야 한다.
  - @DiscriminatorValue를 지정하지 않으면 기본으로 엔티티 이름을 사용한다. <sup>예: Movie, Album, Book</sup>





### 7.1.3 구현 클래스마다 테이블 전략

구현 클래스마다 테이블 전략<sup>Table-per-Concrete-Class Strategy</sup>은 자식 엔티티마다 테이블을 만든다. 그리고 자식 테이블 각각에
필요한 컬럼이 모두 있다.

> *CONCRETE TABLE*

![image](https://user-images.githubusercontent.com/43429667/77299032-30552b80-6d2f-11ea-8cef-2dee90e7f672.png)

> *구현 클래스마다 테이블 전략 매핑*

<script src="https://gist.github.com/11f10b383c251993401c88d366993c4b.js"></script>

<script src="https://gist.github.com/4d38aebe3b4e58f6bda12d9e4ed73721.js"></script>

구현 클래스마다 테이블 전략은 자식 엔티티마다 테이블을 만든다. 일반적으로 추천하지 않는 전략이다.

- **장점**
  - 서브 타입을 구분해서 처리할 때 효과적이다.
  - not null 제약조건을 사용할 수 있다.
- **단점**
  - 여러 자식 테이블을 함께 조회할 때 성능이 느리다<sup>SQL에 UNION을 사용해야 한다</sup>.
  - 자식 테이블을 통합해서 쿼리하기 어렵다.
- **특징**
  - 구분 컬럼을 사용하지 않는다.

이 전략은 데이터베이스 설계자, ORM 전문가 모두 추천하지 않는 전략이다. 조인이나 단일 테이블 전략을 고려하자.





## 7.2 @MappedSuperclass

지금까지 학습한 상속 관계 매핑은 부모 클래스와 자식 클래스를 모두 데이터베이스 테이블과 매핑했다.

부모 클래스는 테이블과 매핑하지 않고 부모 클래스를 상속받는 자식 클래스에게 매핑 정보만 제공하고 싶으면 @MappedSuperclass를 사용하면 된다.

@MappedSuperclass는 비교하자면 추상 클래스와 비슷한데 @Entity는 실제 테이블과 매핑되지만 
@MappedSuperclass는 실제 테이블과는 매핑되지 않는다. 이것은 단순히 매핑 정보를 상속할 목적으로만 사용된다.

예제를 통해 알아보자.

> *@MappedSuperclass 설명 테이블*

![image](https://user-images.githubusercontent.com/43429667/77319675-943d1b80-6d52-11ea-8be5-674d17cbdbb6.png)

> *@MappedSuperclass 설명 객체*

![image](https://user-images.githubusercontent.com/43429667/77320267-98b60400-6d53-11ea-8f16-727647d65879.png)

회원과 판매자는 서로 관계가 없는 테이블과 엔티티다. 테이블은 그대로 두고 객체 모델의 공통 속성을 부모 클래스로 모으고
객체 상속 관계로 만들어보자.

<script src="https://gist.github.com/bffe8fa2c916009a56a45295ddeb4790.js"></script>

<script src="https://gist.github.com/4af03b2d3cc7f0963c6f24e8eac4d723.js"></script>

<script src="https://gist.github.com/d578e463254466071e4ff8bc20d0c6c5.js"></script>

여기서 BaseEntity는 테이블과 매핑할 필요가 없고 자식 엔티티에게 공통으로 사용되는 매핑 정보만 제공하면 된다.
따라서 @MappedSuperclass를 사용했다. 부모로부터 물려받은 매핑 정보를 재정의하려면 @AttributeOverride나 Overrides를 사용하고, 연관관계를 재정의하려면 @AssociationOverride, Overrides를 사용한다.

```java
@Entity
@AttributeOverride(name = "id", column = @Column(name = "member_id"))
public class Member extends BaseEntity {...}
```

부모에게 상속받은 id 속성의 컬럼명을 member_id로 재정의했다.

둘 이상을 재정의 하려면 @AttributeOverrides를 사용하면 된다.

```java
@Entity
@AttributeOverrides({
  @AttributeOverride(name = "id", column = @Column(name = "member_id"))
  @AttributeOverride(name = "name", column = @Column(name = "member_name"))
})
public class Member extends BaseEntity {...}
```

@MappedSuperclass의 특징을 보자.

- 테이블과 매핑되지 않고 자식 클래스에 엔티티의 매핑 정보를 상속하기 위해 사용한다.
- @MappedSuperclass로 지정한 클래스는 엔티티가 아니므로 영속성 관리 대상이 아니다.
- 이 클래스를 직접 생성해서 사용할 일은 거의 없으므로 추상 클래스로 만드는 것을 권장한다.



정리하자면 @MappedSuperclass는 테이블과는 관계가 없고 단순히 엔티티가 공통으로 사용하는 매핑 정보를 모아주는 
역할을 할 뿐이다. 

@MappedSuperclass를 사용하면 등록일자, 수정일자, 등록자, 수정자 같은 여러 엔티티에서 공통으로 사용하는 속성을
효과적으로 관리할 수 있다. 

> 엔티티는 엔티티이거나 @MappedSuperclass로 지정한 클래스만 상속받을 수 있다.





## 7.3 복합 키와 식별 관계 매핑

복합 키를 매핑하는 방법과 식별 관계, 비식별 관계를 매핑하는 방법을 알아보자.



### 7.3.1 식별 관계 vs 비식별 관계

데이터베이스 테이블 사이에 관계는 외래 키가 기본 키에 포함되는지 여부에 따라 식별 관계와 비식별 관계로 구분한다.
두 관계의 특징을 이해하고 어떻게 매핑하는지 알아보자.

- 식별 관계<sup>identifying Relationship</sup>
- 비식별 관계<sup>Non-identifying Relationship</sup>





### 식별 관계

식별 관계는 부모 테이블의 기본 키를 내려받아서 자식 테이블의 기본 키 + 외래 키로 사용하는 관계다.

![image](https://user-images.githubusercontent.com/43429667/77387772-9e522f00-6dd1-11ea-81f9-d31d62a65f5c.png)

PARENT 테이블의 기본 키 PRENT_ID를 받아서 CHILD 테이블의 기본 키 + 외래 키로 사용한다.



### 비식별 관계

비식별 관계는 부모 테이블의 기본 키를 받아서 자식 테이블의 외래 키로만 사용하는 관계다.



> *필수적 비식별 관계*

![image](https://user-images.githubusercontent.com/43429667/77387947-23d5df00-6dd2-11ea-9238-db5553001b0b.png)

> *선택적 비식별 관계*

![image](https://user-images.githubusercontent.com/43429667/77387989-40721700-6dd2-11ea-8871-a08d30ee0a35.png)

PARENT 테이블의 기본 키 PARENT_ID를 받아서 CHILD 테이블의 외래 키로만 사용한다.

비식별 관계는 외래 키에 NULL을 허용하는지에 따라 필수적 비식별 관계와 선택적 비식별 관계로 나눈다.

- **필수적 비식별 관계**<sup>Mandatory</sup>: 외래 키에 NULL을 허용하지 않는다. 연관관계를 필수적으로 맺어야 한다.
- **선택적 비식별 관계**<sup>Optional</sup>: 외래 키에 NULL을 허용한다. 연관관계를 맺을지 말지 선택할 수 있다.



데이터베이스 테이블을 설계할 때 식별 관계나 비식별 관계 중 하나를 선택해야 한다.
최근에는 비식별 관계를 주로 사용하고 꼭 필요한 곳에만 식별 관계를 사용하는 추세다. JPA는 두 관계 모두 지원한다.

식별 관계와 비식별 관계를 어떻게 매핑하는지 알아보자. 먼저 복합 키를 사용하는 비식별 관계부터 보자.



### 7.3.2 복합 키: 비식별 관계 매핑

기본 키를 구성하는 컬럼이 하나면 다음처럼 단순하게 매핑한다.

```java
@Entity
public class Hello {
  @Id
  private String id;
}
```

둘 이상의 컬럼으로 구성된 복합 기본 키는 다음처럼 매핑하면 될 것 같지만 해보면 매핑 오류가 발생한다.
JPA에서 식별자를 둘 이상 사용하려면 별도의 식별자 클래스를 만들어야 한다.

```java
@Entity
public class Hello {
  @Id
  private String id1;
  @Id
  private String id2;  // 실행 시점에서 매핑 예외 발생
}
```

JPA는 영속성 컨텍스트에 엔티티를 보관할 때 엔티티의 식별자를 키로 사용한다. 
그리고 식별자를 구분하기 위해 equals와 hashCode를 사용해서 동등성 비교를 한다. 

그런데 식별자 필드가 하나일 때는 보통 자바의 기본 타입을 사용하므로 문제가 없지만, 
식별자 필드가 2개 이상이면 별도의 식별자 클래스를 만들고 그곳에 equals와 hahCode를 구현해야 한다. 

JPA는 복합 키를 지원하기 위해 두 가지 방법을 제공한다.

- @IdClass: 관계형 데이터베이스에 가까운 방법
- @EmbededId: 객체지향에 가까운 방법



### @IdClass

![image](https://user-images.githubusercontent.com/43429667/77388754-4832bb00-6dd4-11ea-89ee-dd7104ff135f.png)

위 복합 키 테이블은 비식별 관계고 PARENT는 PARENT_ID1, 2를 묶은 복합 기본 키를 사용한다.

따라서 복합 키를 매핑하기 위해 식별자 클래스를 별도로 만들어야 한다.

<script src="https://gist.github.com/cee8c5b527bb669195052cb612823dff.js"></script>

먼저 각각의 기본 키 컬럼을 @Id로 매핑했다. 그리고 @IdClass를 사용해서 ParentId 클래스를 식별자 클래스로 지정했다.

<script src="https://gist.github.com/1a0b219f395deb0a2aed337e72b60190.js"></script>

@IdClass를 사용할 때 식별자 클래스는 다음 조건을 만족해야 한다.

- **식별자 클래스의 속성명과 엔티티에서 사용하는 식별자의 속성명이 같아야 한다.**
- Serializable 인터페이스를 구현해야 한다.
- equals, hashCode를 구현해야 한다.
- 기본 생성자가 있어야 한다.
- 식별자 클래스는 public이어야 한다.

실제 어떻게 사용하는지 알아보자. 먼저 복합 키를 사용하는 엔티티를 저장해보자.

<script src="https://gist.github.com/1aeea0251e28361b0f92bba6d9e4c669.js"></script>

em.persist(parent)를 호출하면 영속성 컨텍스트에서 엔티티를 등록하기 직전에 내부에서 Parent.id1, id2를 사용해서
식별자 클래스인 ParentId를 생성하고 영속성 컨텍스트의 키로 사용한다.

복합 키로 조회해보자.

<script src="https://gist.github.com/d7155c41eaf960ab6311cf2b09572caa.js"></script>

식별자 클래스인 ParentId를 사용해서 엔티티를 조회한다. 이제 자식 클래스를 추가해보자.

<script src="https://gist.github.com/736daed7b198f9df45e5c89e6b38c476.js"></script>

부모 테이블의 기본 키 컬럼이 복합 키이므로 자식 테이블의 외래 키도 복합 키다. 따라서 외래 키 매핑 시 여러 컬럼을 매핑해야 하므로 @JoinColumns를 사용하고 각각의 외래 키 컬럼을 @JoinColumn으로 매핑한다.

> 예제처럼 @JoinColumn의 name 속성과 referencedColumnName 속성의 값이 같으면 referencedColumnName은 생략 가능하다.



### @EmbededId

좀 더 객체지향적인 방법인 @EmbededId를 알아보자.

<script src="https://gist.github.com/60e53d6eb1cccf51ed932b8d3958840f.js"></script>

Parent 엔티티에서 식별자 클래스를 직접 사용하고 @Embeded 애노테이션을 적어주면 된다.

> *식별자 클래스*

<script src="https://gist.github.com/16973ce8bc95558bc00d861dbf6f6f37.js"></script>

@IdClass와는 다르게 @EmbededId를 적용한 식별자 클래스는 식별자 클래스에 기본 키를 직접 매핑한다.

@EmbededId를 적용한 식별자 클래스는 다음 조건을 만족해야 한다.

- @Embeddable 애노테이션을 붙여주어야 한다.
- Serializable 인터페이스를 구현해야 한다.
- *equals, hashCode를 구현해야 한다.*
- *기본 생성자가 있어야 한다.*
- 식별자 클래스는 public이어야 한다.

@EmbededId를 사용한 코드로 엔티티를 저장해보자.

<script src="https://gist.github.com/181c85722d5e66143706534d74a740ab.js"></script>

parentId를 직접 생성해서 사용하였다. 조회도 해보자.

<script src="https://gist.github.com/c9be31bb50e284fb75af9ec4607b000b.js"></script>

조회 코드도 식별자 클래스 parentId를 직접 사용한다.



### 복합 키와 equals(), hashCode()

복합 키는 equals와 hashCode를 필수로 구현해야 한다.

```java
ParentId id1 = new ParentId();
id1.setId1("myId1");
id1.setId2("myId2");

ParentId id2 = new ParentId();
id2.setId1("myId1");
id2.setId2("myId2");

id1.equals(id2) -> ??
```

id1, id2 인스턴스 둘 다 같은 값을 가지고 있지만 인스턴스는 다르다.

equals()를 적절히 오버라이딩했다면 참이겠지만 자바의 모든 클래스는 Object 클래스를 상속받는데 이 클래스가 제공하는
기본 equals()는 인스턴스 참조 값 비교인 ==비교<sup>동일성 비교</sup>를 하기 때문이다.

영속성 컨텍스트는 엔티티의 식별자를 키로 사용해서 엔티티를 관리하는데 이 식별자를 비교할 때 equals()와 hashCode()를
사용한다. 따라서 식별자 객체의 동등성<sup>equals 비교</sup>이 지켜지지 않으면 예상과 다른 엔티티가 조회되거나 엔티티를 찾을 수 없는 등
엔티티를 관리하는데에 문제가 발생한다.

따라서 복합 키는 equals()와 hashCode()를 필수로 구현해야 한다. 



### @IdClass vs @EmbededId

@IdClass와 @EmbededId는 각각 장단점이 있으므로 본인의 취향에 맞는 것을 일관성 있게만 사용하면 된다.
@EmbededId가 @IdClass와 비교해서 더 객체지향적이고 중복도 없어서 좋아보이긴 하지만 특정 상황에 JPQL이 조금 더 길어질 수 있다.

```java
em.createQuery("select p.id.id1, p.id.id2 from Parent p"); // @EmbededId
em.createQuery("select p.id1, p.id2 from Parent p");       // @IdClass
```

> *복합 키에는 @GenerateValue를 사용할 수 없다. 복합 키를 구성하는 여러 컬럼 중 하나에도 사용할 수 없다.*



### 7.3.3 복합 키: 식별 관계 매핑

![image](https://user-images.githubusercontent.com/43429667/77393665-dca31a80-6de0-11ea-8df6-2b465087b0ac.png)

그림을 보면 부모, 자식, 손자까지 계속 기본 키를 전달하는 식별 관계다.
식별 관계에서 자식 테이블은 부모 테이블의 기본 키를 포함해서 복합 키를 구성해야 하므로 @IdClass나 @EmbededId를 
사용해서 식별자를 매핑해야 한다.



### @IdClass와 식별 관계



> *부모*

<script src="https://gist.github.com/7a502349e7f4be7e3b9343a11d9619ba.js"></script>

> *자식*

<script src="https://gist.github.com/df0e7ae0e2625d9b0e7e68743b0fb6a3.js"></script>

> *자식 ID*

<script src="https://gist.github.com/fa441c88eff1a1dc70dcb2b6caf9d6b2.js"></script>

> *손자*

<script src="https://gist.github.com/104fae3a4b1a9ecea568dfc108a1bb35.js"></script>

> *손자 ID*

<script src="https://gist.github.com/39d1c825e4302272d46d9e952a586c33.js"></script>

식별 관계는 기본 키와 외래 키를 같이 매핑해야 한다. 
따라서 식별자 매핑인 @Id와 연관관계 매핑인 ManyToOne을 같이 사용하면 된다.

```java
  @Id
  @ManyToOne
  @JoinColumn(name = "parent_id")
  public Parent parent;
```

Child 엔티티의 parent를 보면 @Id로 기본 키를 매핑하면서 @ManyToOne과 JoinColumn으로 외래 키를 같이 매핑한다.



### @EmbededId와 식별 관계

@EmbededId로 식별 관계를 구성할 때는 @MapsId를 사용해야 한다. 

> *부모*

<script src="https://gist.github.com/568b43b7747878d2e6b9588828938ce0.js"></script>

> *자식*

<script src="https://gist.github.com/1895ad6b6b105d7bff4cd07fba9c5c98.js"></script>

> *자식 ID*

<script src="https://gist.github.com/65b9e275d2eca33c872433c29809075d.js"></script>

> *손자*

<script src="https://gist.github.com/8cfd067f1244db1a0fc167f8b75c3548.js"></script>

> *손자 ID*

<script src="https://gist.github.com/7bbb927b6a8c752d4ca1c686d1a6de55.js"></script>

@EmbededId는 식별 관계로 사용할 연관관계의 속성에 @MapsId를 사용하면 된다. 
@MapsId는 외래 키와 매핑한 연관관계를 기본 키에도 매핑하겠다는 뜻이다. 
@MapsId의 속성 값은 @EmbededId를 사용한 식별자 클래스의 기본 키 필드를 지정하면 된다.



### 7.3.4 비식별 관계로 구현

방금 예를 들었던 식별 관계 테이블을 복합 키를 사용하지 않는 비식별 관계로 변경해보자.

![image](https://user-images.githubusercontent.com/43429667/77396439-242ca500-6de7-11ea-80f6-fda4c55a6208.png)

이렇게 복합 키를 사용하지 않는 비식별 관계로 만든 테이블을 매핑해보자.

<script src="https://gist.github.com/eff96ee4c92ed0aef91bbcd60fb109ce.js"></script>

<script src="https://gist.github.com/195bbd901d03e00a5303e9e1af6e417e.js"></script>

<script src="https://gist.github.com/ed98af5047c6f1b76a6b7e48a71c10fa.js"></script>

식별 관계의 복합 키를 사용한 코드와 비교하면 매핑도 쉽고 코드도 단순하다. 
그리고 복합 키가 없으므로 복합 키 클래스를 만들지 않아도 된다.



### 7.3.5 일대일 식별 관계

> *식별관계 일대일*

![image](https://user-images.githubusercontent.com/43429667/77398126-407e1100-6dea-11ea-94e9-ade9aed83b47.png)

일대일 식별 관계는 자식 테이블의 기본 키 값으로 부모 테이블의 기본 키 값만 사용한다. 
그래서 부모 테이블의 기본 키가 복합 키가 아니면 자식 테이블의 기본 키는 복합 키로 구성하지 않아도 된다.

> *부모*

<script src="https://gist.github.com/2b74f9a1e29f26bf4bd7501134009575.js"></script>

>*자식*

<script src="https://gist.github.com/733ad477ed902cc689ac4478a941bb25.js"></script>

BoardDetail처럼 식별자가 단순히 컬럼 하나면 @MapsId를 사용하고 속성 값은 비워두면 된다.
이때 @MapsId는 @Id를 사용해서 식별자로 지정한 BoardDetail.boardId와 매핑된다.

일대일 식별 관계를 사용하는 코드를 보자.

<script src="https://gist.github.com/f340750ce21ecfce7c7c8a36cf73a5a4.js"></script>

### 7.3.6 식별, 비식별 관계의 장단점

데이터베이스 설계 관점에서 보면 다음과 같은 이유로 식별 관계 보다는 비식별 관계를 선호한다.

- 식별 관계는 부모 테이블의 기본 키를 자식 테이블로 전파하면서 자식 테이블의 기본 키 컬럼이 점점 늘어난다.
  결국 조인할 때 SQL이 복잡해지고 기본 키 인덱스가 불필요하게 커질 수 있다.
- 식별 관계는 2개 이상의 컬럼을 합해서 복합 기본 키를 만들어야 하는 경우가 많다.
- 식별 관계를 사용할 때 기본 키로 비즈니스 의미가 있는 자연 키 컬럼을 조합하는 경우가 많다.
  반면에 비식별 관계는 대리 키를 주로 사용한다.
  식별 관계의 자연 키 컬럼들이 자식에 손자까지 전파되면 변경하기 힘들다.
- 식별 관계는 부모 테이블의 기본 키를 자식 테이블의 기본 키로 사용하므로 비식별 관계보다 테이블 구조가 유연하지 못하다.



객체 관계 매핑의 관점에서 보면 다음과 같은 이유로 비식별 관계를 선호한다.

- 일대일 관계를 제외하고 식별 관계는 2개 이상의 컬럼을 묶은 복합 기본 키를 사용한다.
  JPA에서 복합 키는 별도의 복합 키 클래스를 만들어서 사용해야 하므로 번거롭다.
- 비식별 관계의 기본 키는 주로 대리키를 사용하는데 JPA는 대리 키를 생성하기 위한 편리한 방법을 제공한다.



물론 식별 관계가 가지는 장점도 있다. 기본 키 인덱스를 활용하기 좋고, 특정 상황에 조인 없이 하위 테이블만으로 검색할 수 있다.

기본 키 인덱스를 활용하는 예를 보자.

- **부모 아이디가 A인 모든 자식 조회**

```sql
SELECT * FROM CHILD
WHERE PARENT_ID = 'A'
```

- **부모 아이디가 A고 자식 아이디가 B인 자식 조회**

```sql
SELECT * FROM CHILD
WHERE PARENT_ID = 'A' AND CHILD_ID = 'B'
```

별도의 인덱스를 생성할 필요 없이 기본 키 인덱스만으로 사용했다.

이처럼 식별 관계가 가지는 장점도 있으므로 꼭 필요한 곳에는 적절하게 사용하는 것도 좋다.

내용을 정리해보자면,
ORM 신규 프로젝트 진행시 추천하는 방법은 될 수 있으면 **비식별 관계를 사용하고 기본 키는 Long 타입의 대리키를 사용하는 것이다.** 대리 키는 비즈니스와 아무 관련이 없으므로 비즈니스가 변경되어도 유연한 대처가 가능하다는 장점이 있다.
그리고 식별자 컬럼이 하나여서 쉽게 매핑할 수 있다.

그리고 선택적 비식별 관계보다는 필수적 비식별 관계를 사용하는 것이 좋은데, 선택적인 비식별 관계는 NULL을 허용하므로
조인할 때에 외부 조인을 사용해야 한다. 반면에 필수적 관계는 NOT NULL로 항상 관계를 보장하므로 내부조인만 사용해도 된다.





## 7.4 조인 테이블

데이터베이스 테이블의 연관관계를 설계하는 방법은 크게 2가지이다.

- 조인 컬럼 사용<sup>외래 키</sup>
- 조인 테이블 사용<sup>테이블 사용</sup>



- **조인 컬럼 사용**

  테이블 간에 관계는 주로 조인 컬럼이라 부르는 외래 키 컬럼을 사용해서 관리한다.

  예를 들어 회원과 사물함 관계에서 회원이 사물함을 사용하기 전까지는 둘 사이의 관계가 없으므로 회원의 사물함 컬럼은
  null을 입력해두어야 한다. 이렇게 외래 키에 null을 허용하는 관계를 선택적 비식별 관계라 한다.

  때문에 외부 조인<sup>OUTER JOIN</sup>을 사용해야 하는데, 내부 조인을 사용하면 사물함과 관계가 없는 회원은 조회가 되지 않는다.

- **조인 테이블 사용**

  조인 테이블이라는 별도의 테이블을 사용해서 연관관계를 관리한다. 조인 테이블의 가장 큰 단점은 테이블을 하나 추가해야 한다는 점이다. 따라서 관리해야하는 테이블이 늘어나고 회원과 사물함을 조인하려면 조인 테이블까지 추가로 조인해야 한다.

  따라서 기본은 조인 컬럼을 사용하고 필요하다고 판단되면 조인 테이블을 사용하자.



조인 테이블에서 배울 내용은 다음과 같다.

- 객체와 테이블을 매핑할 때 조인 컬럼은 @JoinColumn으로 매핑하고 조인 테이블은 @JoinTable로 매핑한다.
- 조인 테이블은 주로 다대다 관계를 풀어내기 위해 사용하지만 일대일, 일대다, 다대일 관계에서도 사용한다.



일대일, 일대다, 다대일, 다대다 관계를 조인 테이블로 매핑해보자.

> 조인 테이블을 연결 테이블, 링크 테이블로도 부른다.





### 7.4.1 일대일 조인 테이블

일대일 관계를 만들려면 조인 테이블의 외래 키 컬럼 각각에 총 2개의 유니크 제약조건을 걸어야 한다.

> *조인 컬럼*

![image](https://user-images.githubusercontent.com/43429667/77403412-91463780-6df3-11ea-807b-d2e09cd936ad.png)

> *조인 테이블*

![image](https://user-images.githubusercontent.com/43429667/77403617-ceaac500-6df3-11ea-972c-b3e1a7ed9e1b.png)



> *일대일 조인 테이블 매핑*

<script src="https://gist.github.com/f01bed9fa587ebfb4080398350a105d3.js"></script>

<script src="https://gist.github.com/ae1560066404035262992d8dcef403db.js"></script>

부모 엔티티를 보면 @JoinColumn 대신에 @JoinTable을 사용했다.

@JoinTable의 속성은 다음과 같다.

- name: 매핑할 조인 테이블 이름
- joinColumns: 현재 엔티티를 참조하는 외래 키
- inverseJoinColumns: 반대방향 엔티티를 참조하는 외래 키

양방향으로 매핑하려면 다음 코드를 추가하면 된다.

```java
public class Child {
  ...
  @OneToOne(mappedBy="child")
  private Parent parent;
}
```



### 7.4.2 일대다 조인 테이블

일대다 관계를 만들려면 조인 테이블의 컬럼 중 다<sup>N</sup>와 관련된 컬럼인 child_id에 유니크 제약조건을 걸어야 한다.<sup>child_id는 기본 키이므로 유니크 제약조건이 걸려있다.</sup> 

> *조인 컬럼*

![image](https://user-images.githubusercontent.com/43429667/77403281-5f34d580-6df3-11ea-86b5-5a42cd873334.png)

> *조인 테이블*

![image](https://user-images.githubusercontent.com/43429667/77404544-3b728f00-6df5-11ea-9988-68d60ba04ecf.png)



> *일대다 단방향 조인 테이블 매핑*

<script src="https://gist.github.com/da175f8a4bc70cae4459b36f20d9d315.js"></script>

<script src="https://gist.github.com/a655686a9e8865bc365bfa901b8ad618.js"></script>



### 7.4.3 다대일 조인 테이블

다대일은 일대다에서 방향만 반대이므로 조인 테이블 모양은 일대다에서 설명한 그림과 같다.
다대일, 일대다 양방향 관계로 매핑해보자.

> *다대일 양방향 조인 테이블*

<script src="https://gist.github.com/7189bdf05a89368fc4fa727bf16b328e.js"></script>

<script src="https://gist.github.com/4ffbbe64c9c31caa4639e7bb8ccdbdff.js"></script>



### 7.4.4 다대다 조인 테이블

다대다 관계를 만들려면 조인 테이블의 두 컬럼을 합해서 하나의 복합 유니크 제약조건을 걸어야 한다.<sup>parent_id, chilid_id는 복합 기본 키이므로 유니크 제약조건이 걸려 있다.</sup>

> *다대다 조인 테이블*

![image](https://user-images.githubusercontent.com/43429667/77406633-51358380-6df8-11ea-8944-c8d150f5f4d2.png)



> *다대다 조인 테이블 매핑*

<script src="https://gist.github.com/601db90c0e4e8638744e2ca7271acb00.js"></script>

<script src="https://gist.github.com/1d73f6696ae10aa9716f53596d60ab55.js"></script>

> *조인 테이블에 컬럼을 추가하면 @JoinTable 전략을 사용할 수 없다. 대신에 새로운 엔티티를 만들어서 조인 테이블과*
> *매핑해야 한다.*



## 7.5 엔티티 하나에 여러 테이블 매핑

잘 사용하지는 않지만 @SecondaryTable을 사용하면 한 엔티티에 여러 테이블을 매핑할 수 있다.

![image](https://user-images.githubusercontent.com/43429667/77407723-f43acd00-6df9-11ea-8974-c55694618694.png)



> *하나의 엔티티에 여러 테이블 매핑*

<script src="https://gist.github.com/11776ce50bcace5918bffd9cf0e8cfeb.js"></script>

Board 엔티티는 @Table을 사용해서 BOARD 테이블과 매핑했다. 
그리고 @SecondaryTable을 사용해서 BOARD_DETAIL 테이블을 추가로 매핑했다.

@SecondaryTable의 속성은 다음과 같다.

- @Secondary.name: 매핑할 다른 테이블의 이름이다.
- @Secondary.pkJoinColumns: 매핑할 다른 테이블의 기본 키 컬럼 속성이다.

```java
@Column(table = "board_detail")
private String content;
```

content 필드는 @Column(table = "board_detail")를 사용해서 board_detail 테이블의 컬럼에 매핑했다.
title 필드처럼 테이블을 지정하지 않으면 기본 테이블인  board에 매핑된다.

더 많은 테이블을 매핑하려면 @SecondaryTables를 사용하면 된다.

```java
@SecondaryTables({
  @SecondaryTable(name = "board_detail"),
  @SecondaryTable(name = "bard_file")
})
```

이 방법은 항상 두 테이블을 조회하므로 최적화가 힘들다. 반면에 일대일 매핑은 원하는 부분만 조회할 수 있고 
필요할 때 둘을 함께 조회하면 된다.





다음 장에서는 객체 그래프를 자유롭게 탐색할 수 있도록 도와주는 지연 로딩과 프록시에 대해 알아보겠다. 
그리고 이 장에서 다룬 객체 연관관계를 더 편리하게 관리할 수 있는 방법들도 알아볼 것이다.



[실전 예제 - 상속 관계 매핑](https://github.com/MoochiPark/jpa/tree/master/chapter07/src)