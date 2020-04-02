# Chapter 09. 값 타입

JPA의 데이터 타입을 가장 크게 분류하면 엔티티 타입과 값 타입으로 나눌 수 있다. 
엔티티 타입은 @Entity로 정의하는 객체이고, 값 타입은 int, Integer, String처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체를 말한다. 

엔티티 타입은 식별자를 통해 지속해서 추적할 수 있지만, 값 타입은 추적할 수 없다.

값 타입은 3가지로 분류할 수 있다.

- 기본 값 타입<sup>basic value type</sup>
  - 자바 기본 타입
  - 래퍼 클래스
- 임베디드 타입<sup>embeded type(복합 값 타입)</sup>
- 컬렉션 값 타입<sup>collection value type</sup>

임베디드 타입은 JPA에서 사용자가 직접 정의한 값 타입이다.





## 9.1 기본값 타입

> *기본값 타입*

```java
@Entity
public class Member {
  
  @Id @GeneratedValue
  private Long id;
  
  private String name;
  private int age;
  ...
}
```

Member 엔티티는 id라는 식별자 값도 가지고 생명주기도 있지만 값 타입인 name, age 속성은 식별자 값도 없고 생명주기도
Member 엔티티에 의존한다. 따라서 당연히 회원 엔티티 인스턴스르 제거하면 name, age 값도 제거된다.





## 9.2 임베디드 타입(복합 값 타입)

새로운 값 타입을 직접 정의해서 사용할 수 있는데, JPA에서 이것을 임베디드 타입<sup>embedded type</sup>이라 한다. 
임베디드 타입도 int, String처럼 값 타입이다.

> *기본 회원 엔티티*

```java
@Entity
public class Member {
  
  @Id @GeneratedValue
  private Long id;
  private String name;
  
  // 근무 기간
  @Temporal (TemporalType.DATE) Date startDate;
  @Temporal (TemporalType.DATE) Date endDate;
  
  // 집 주소
  private String city;
  private String street;
  private String zipcode;
  ...
}
```

위 회원 엔티티를 '이름, 근무 시작일, 근무 종료일, 주소 도시, 주소 번지, 주소 우편번호를 가진다.'고 표현하기보다
'회원 엔티티는 이름, 근무 기간, 집 주소를 가진다.'고 표현하는 것이 객체지향적이고 응집력이 있는 표현이다.

 [근무기간, 집주소]를 가지도록 임베디드 타입을 사용해보자.

> *값 타입 적용 회원 엔티티*

```java
@Entity
public class Member {
  
  @Id @GeneratedValue
  private Long id;
  private String name;
  
  // 근무 기간
  @Embedded Period workPeriod;
  
  // 집 주소
  @Embedded Address homeAddress;
  ...
}
```

> *기간 임베디드 타입*

```java
@Embeddable
public class Period {
  
  @Temporal (TemporalType.DATE) Date startDate;
  @Temporal (TemporalType.DATE) Date endDate;
  
  public boolean isWork(Date date) {
    //.. 값 타입을 위한 메서드 정의 가능
  }
}
```

> *주소 임베디드 타입*

```java
@Embeddable
public class Address {
  
  @Column(name = "city") // 매핑할 컬럼 정의 가능
  private String city;
  private String street;
  private String zipcode;
  ...
}
```

새로 정의한 값 타입들은 재사용할 수 있고 응집도도 아주 높다. 
또한 `isWork()`처럼 Period만 사용하는 의미있는 메서드도 만들 수 있다.

임베디드 타입을 사용하려면 2가지 애노테이션이 필요하다. 둘 중 하나는 생략해도 된다.

- @Embeddable: 값 타입을 정의하는 곳에 표시
- @Embedded: 값 타입을 사용하는 곳에 표시
- 임베디드 타입은 기본 생성자가 필수다.
- 임베디드 타입을 포함한 모든 값 타입은 엔티티의 생명주기에 의존하므로 엔티티와 임베디드 타입은 **컴포지션**<sup>구성</sup> **관계**다.

> 하이버네이트는 임베디드 타입을 컴포넌트<sup>components</sup>라 한다.



### 9.2.1 임베디드 타입과 테이블 매핑

임베디드 타입은 엔티티의 값일 뿐이다. 임베디드 타입을 사용하기 전 후의 매핑하는 테이블은 같다.
임베디드 타입 덕분에 객체와 테이블을 아주 세밀하게 매핑하는 것이 가능하다.

### 9.2.2 임베디드 타입과 연관관계

임베디드 타입은 값 타입을 포함하거나 엔티티를 참조할 수 있다. 

> 엔티티는 공유될 수 있으므로 참조한다고 표현하고, 값 타입은 특정 주인에 소속되고 논리적인 개념상으로 
> 공유되지 않으므로 포함한다고 표현한다.

> *임베디드 타입과 연관관계*

```java
@Entity
public class Member {
  
  @Embedded Address address;
  @Embedded PhoneNumber phoneNumber;
  ...
}
```

```java
@Embeddable
public class Address {

  String street;
  String city;
  String state;
  @Embedded Zipcode zipcode; // 임베디드 타입 포함
  
}
```

```java
@Embeddable
public class Zipcode {
  
  String zip;
  String plusFour;
  
}
```

```java
@Embeddable
public class PhoneNumber {
  
  String areaCode;
  String localNumber;
  @ManyToOne PhoneServiceProvider provider; // 엔티티 참조
  ...
}
```

```java
@Entity
public class PhoneServiceProvider {
  
  @Id String name;
  ...
}
```

- 값 타입인 Address가 값 타입인 Zipcode를 포함할 수 있다.
- 값 타입인 PhoneNumber가 엔티티 타입인 PhoneServiceProvider를 참조할 수 있다.



### 9.2.3 @AttributeOverride: 속성 재정의

임베디드 타입에 정의한 매핑정보를 재정의하려면 엔티티에 @AttributeOverride를 사용하면 된다.

예를 들어 회원에게 주소가 하나 더 필요하다고 해보자.

```java
@Entity
public class Member {
  
  @Id @GeneratedValue
  private Long id;
  private String name;
  
  
  @Embedded Address homeAddress;
  @Embedded Address companyAddress;
 
}
```

이렇게 했을 때의 문제점은 테이블에 매핑하는 컬럼명이 중복되는 것이다. 이 때 @AttributeOverrides를 사용하면 된다.

```java
@Entity
public class Member {
  
  @Id @GeneratedValue
  private Long id;
  private String name;
  
  
  @Embedded Address homeAddress;
  
  @Embedded 
  @AttributeOverrides({
    @AttributeOverride(name="city", column=@Column(name="company_city")),
    @AttributeOverride(name="street", column=@Column(name="company_steet")),
    @AttributeOverride(name="zipcode", column=@Column(name="company_zipcode")),
  })
  Address companyAddress;
 
}
```

생성된 테이블을 확인하자.

```sql
CREATE TABLE MEMBER {
  company_city varchar(255),
  company_street varchar(255),
  company_zipcode varchar(255),
  city varchar(255),
  street varchar(255),
  zipcode varchar(255),
  ...
}
```

@AttributeOverrides는 엔티티에 설정해야 한다. 임베디드 타입이 임베디드 타입을 가지고 있더라도 엔티티에 설정해야 한다.



## 9.3 값 타입과 불변 객체



### 9.3.1 값 타입 공유 참조

임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험하다.

```java
member1.setHomeAddress(new Address("OldCity"));
Address address = member1.getHomeAddress();

address.setCity("newCity"); // 회원1의 address 값을 공유해서 사용
member2.setHmeAddress(address);
```

**회원2의 주소만 `NewCity`로 변경되길 기대했지만 회원1의 주소도 `NewCity`로 변경되어 버린다.**
회원1과 2가 같은 address 인스턴스를 참조하기 때문인데, 영속성 컨텍스트는 회원1과 2 둘 다 city 속성이 변경된 것으로 판단해서 각각 UPDATE SQL을 실행한다.

이렇게 뭔가를 수정했는데 예상치 못한 곳에서 문제가 발생하는 것을 **부작용**<sup>side effect</sup>이라 한다.



### 9.3.2 값 타입 복사

값을 공유하여 사용하는 것 대신에 값<sup>인스턴스</sup>를 복사해서 사용해야 한다.

```java
member1.setHomeAddress(new Address("OldCity"));
Address address = member1.getHomeAddress();

Address newAddress = address.clone();

new Address.setCity("NewCity");
member2.setHomeAddress(newAddress);
```

이처럼 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다.

임베디드 타입처럼 직접 정의한 값 타입은 자바의 기본 타입<sup>primitive type</sup>이 아니라 객체 타입이다.
따라서 값을 대입하면 항상 복사본을 전달하는 기본 타입과는 다르게 항상 참조 값을 전달한다.

```java
Address a = new address("Old");
Address b = a; // 참조 값 전달 
b.setCity("New");
```

복사하지 않고 원본의 참조 값을 직접 넘기는 것을 막을 방법은 없다. 따라서 객체의 공유 참조는 피할 수 없다.

따라서 근본적인 해결책이 필요한데 객체의 값을 수정하지 못하게 막으면 된다.



### 9.3.3 불변 객체

값 타입은 부작용 없이 쓸 수 있어야 한다. 
**객체를 불변하게 만들면 부작용을 원천 차단할 수 있다. 따라서 값 타입은 가능하면 불변 객체**<sup>Immutable Object</sup>**로 설계해야 한다.**

불변 객체의 값은 조회할 수 있지만 수정할 수 없다. 인스턴스 참조 값의 공유를 피할 순 없지만 부작용은 발생하지 않는다.
구현하는 다양한 방법이 있지만 가장 간단하게 생성자로만 값을 설정하고 설정자를 만들지 않으면 된다.

> *주소 불변 객체*

```java
@Embeddable
public class Address {
  
  private String city;
  
  protected Address() {} // JPA에서 기본 생성자는 필수다.
  
  public Address(String city) {this.city = city}
 
  public String getCity() {return city;}
  
  // Setter는 만들지 않는다.
  
}
```

Integer, String 등은 자바가 제공하는 대표적인 불변 객체다.**불변이라는 작은 제약으로 부작용이라는 큰 재앙을 막을 수 있다.**



## 9.4 값 타입의 비교

자바에서 equals()를 재정의하면 hashCode()도 재정의하는 것이 안전하다. 그렇지 않으면 해시를 사용하는 컬렉션<sup>HashSet, HashMap</sup>이 정상 동작하지 않는다. 자바 IDE에는 대부분 equals, hashCode 메서드를 자동으로 생성해주느 기능이 있다.



## 9.5 값 타입 컬렉션

값 타입을 하나 이상 저장하려면 컬렉션에 보관하고 `@ElementCollection`, `@CollectionTable` 을 사용하면 된다.

> *값 타입 컬렉션*

```java
@Entity
public class Member {
  
  @Id @GeneratedValue
  private Long id;
  
  @Embedded
  private Address homeAddress;
  
  @ElementCollection
  @CollectionTable(name="favorite_food",
                  joinColumns=@JoinColumn(name="member_id"))
  @Column(name="food_name")
  private Set<String> favoriteFoods = new HashSet<>();
  
  @ElementCollection
  @CollectionTable(name="address",
                  joinColumns=@JoinColumn(name="member_id"))
  @Column(name="member_id")
  private List<Address> addressHistory = new ArrayList<>();
  
}

@Embeddable
public class Address {
  
  @Column
  private String city;
  private String street;
  private String zipcode;
  ...
}
```

`favoriteFoods`는 기본값 타입인 String을 컬렉션으로 가진다. 이것을 테이블로 매핑해야 하는데 테이블은 컬럼안에 컬렉션을 포함할 수 없다. 따라서 별도의 테이블을 추가하고 `@CollectionTable`을 사용해서 추가한 테이블을 매핑해야 한다. 
그리고 `favoriteFoods`처럼 값으로 사용되는 컬럼이 하나면 `@Column`을 사용해서 컬럼명을 지정할 수 있다.

테이블 매핑 정보는 @AttributeOverride를 사용해서 재정의할 수 있다.
@CollectionTable을 생략하면 기본값을 사용해서 매핑한다. `{엔티티 이름}_{컬렉션 속성 이름}`이 기본값이다.



### 9.5.1 값 타입 컬렉션 사용

> 값 타입 컬렉션 등록

```java
Member member = new Member();

// 임베디드 값 타입
member.setHomeAddress(new Address("통영", "몽돌해수욕장", "660-123"));

// 기본값 타입 컬렉션
member.getFavoriteFoods().add("짬뽕");
member.getFavoriteFoods().add("짜장");
member.getFavoriteFoods().add("탕수육");

// 임베디드 값 타입 컬렉션
member.getAddressHistory().add(new Address("서울", "강남", "123-123"));
member.getAddressHistory().add(new Address("서울", "강북", "000-000"));

em.persist(member);
```

JPA는 member 엔티티를 영속화할 때 값 타입들도 함께 저장한다. 실제 실행되는 INSERT SQL을 보자.

- member: INSERT SQL 1번
- member.homeAddress: 컬렉션이 아닌 임베디드 값 타입이므로 회원 테이블에 포함된다.
- member.favoriteFoods: INSERT SQL 3번
- member.addressHistory: INSERT SQL 2번

따라서 `em.persist(member)` 한 번 호출로 총 6번의 INSERT SQL을 실행한다.

> 값 타입은 영속성 전이 + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있다.

값 타입 컬렉션도 조회할 때 페치 전략을 선택할 수 있는데 LAZY가 기본 전략이다.

```java
@ElementCollection(fetch = FetchType.LAZY)
```



지연 로딩으로 가정하고 다음 코드를 실행하면 어떻게 되는지 보자.

> *조회*

```java
// SQL: SELECT ID, CITY, STREET, ZIPCODE FROM MEMBER WHERE ID = 1
Member member = em.find(Member.class, 1L); // 1. Member

// 2. member.homeAddress
Address homeAddress = member.getAddress();

// 3. member.favoriteFoods
Set<String> favoriteFoods = member.getFavoriteFoods(); // LAZY

// SQL: SELECT MEMBER ID, FOOD_NAME FROM FAVORITE_FOODS
// WHERE MEMBER_ID = 1
for (String favoriteFood : favoriteFoods) {
  System.out.println("favotiteFood = " + favoriteFood);
}

// 4. member.addressHistory
List<Address> addressHistory = member.getAddressHistory(); // LAZY

// SQL: SELECT MEMBER_ID, CITY, STREET, ZIPCODE FROM ADDRESS
// WHERE MEMBER_ID = 1
addressHistory.get(0);
```

1. member: 회원만 조회한다. 이때 임베디드 값 타입인 homeAddress도 함께 조회한다. 
2. member.homeAddress: 1번에서 회원을 조회할 때 같이 조회해 둔다.
3. member.favoriteFoods: LAZY로 설정해서 실제 컬렉션을 사용할 때 SELECT SQL을 1번 호출한다.
4. member.addressHistory: LAZY로 설정해서 실제 컬렉션을 사용할 때 SELECT SQL을 1번 호출한다.



> *수정*

```java
Member member = em.find(Member.class, 1L);

// 1. 임베디드 값 타입 수정
member.setHomeAddress(new Address("새로운도시", "신도시1", "123456");

// 2. 기본값 타입 컬렉션 수정
Set<String> favoriteFoods = member.getFavoriteFoods();
favoriteFoods.remove("탕수육");
favoriteFoods.add("치킨");

// 3. 임베디드 값  타입 컬렉션 수정
List<Address> addressHistory = member.getAddressHistory();
addressHistory.remove(new Address("서울", "기존 주소", "123-123"));
addressHistory.remove(new Address("신도시", "새로운 주소", "123-456"));
```

1. **임베디드 값 타입 수정**: homeAddress 임베디드 값 타입은 MEMBER 테이블과 매핑했으므로 MEMBER 테이블만 UPDATE 한다. Member 엔티티를 수정하는 것과 같다.
2. **기본값 타입 컬렉션 수정**: 탕수육을 치킨으로 변경하려면 탕수육을 제거하고 치킨을 추가해야 한다.
   자바의 String 타입은 불변 객체이다.
3. **임베디드 값 타입 컬렉션 수정**: 값 타입은 불변해야 하므로 컬렉션에서 기존 주소를 삭제하고 새로운 주소를 등록했다.
   값 타입은 equals, hashCode를 꼭 구현해야 한다고 했다.





### 9.5.2 값 타입 컬렉션의 제약사항

값 타입 컬렉션에 보관된 값 타입들은 별도의 테이블에 보관되므로 이 테이블에 보관된 값이 변경되면 데이터베이스에 있는 원본 데이터를 찾기 어렵다는 문제가 있다. 
이런 문제로 JPA 구현체들은 값 타입 컬렉션에 변경사항이 생기면 값 타입 컬렉션 테이블의 모든 데이터를 삭제하고
현재 값 타입 컬렉션 객체에 있는 모든 값을 데이터베이스에 다시 저장한다.

따라서 실무에서는 값 타입 컬렉션이 매핑된 테이블에 데이터가 많다면 일대다 관계를 고려해야 한다.
추가로 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본 키를 구성해야 한다.
따라서 기본 키 제약으로 인해 컬럼에 null을 입력할 수 없고, 같은 값을 중복해서 저장할 수도 없다.

위의 문제를 해결하려면 값 타입 컬렉션 대신에 새로운 엔티티를 만들어서 일대다 관계로 설정하고,
추가로 영속성 전이<sup>cascade</sup> + 고아 객체 제거<sup>ORPHAN REMOVE</sup> 기능을 적용하면 값 타입 컬렉션처럼 사용할 수 있다.



## 9.6 정리

#### 엔티티 타입의 특징

- 식별자<sup>@Id</sup>가 있다.
  - 엔티티 타입은 식별자가 있고 식별자로 구별할 수 있다.
- 생명 주기가 있다.
  - 생성, 영속화, 소멸의 생명주기가 있다.
  - em.persist(entity)로 영속화.
  - em.remove(entity)로 제거.
- 공유할 수 있다.
  - 참조 값을 공유할 수 있다. 이것을 공유 참조라 한다.
  - 회원 엔티티가 있다면 다른 엔티티에서 참조할 수 있다.



#### 값 타입의 특징

- 식별자가 없다.
- 생명 주기를 엔티티에 의존한다.
  - 스스로 생명주기를 가지지 않고 엔티티에 의존한다. 엔티티를 제거하면 같이 제거된다.
- 공유하지 않는 것이 안전하다.
  - 값을 복사해서 사용해야 한다.
- 오직 하나의 주인만이 관리해야 한다.
- 불변 객체로 만드는 것이 안전하다.



[실전 예제 - 값 타입 매핑](https://github.com/MoochiPark/jpa/tree/master/chapter09/src)

