# Chapter 07. 실전 예제 - 상속 관계 매핑

다음 요구사항이 추가되었다고 하자.

- 상품의 종류는 음반, 도서, 영화가 있고 이후 더 확장될 수 있다.
- 모든 데이터는 등록일과 수정일이 있어야 한다.



> *실전 예제 4 UML*

![image](https://user-images.githubusercontent.com/43429667/77629297-a3a7a900-6f8c-11ea-9807-cb67bebfa33e.png)



> *실전 예제 4 UML 상세*

![image](https://user-images.githubusercontent.com/43429667/77630049-ce463180-6f8d-11ea-8905-3d82be607d14.png)



> *실전 예제 4 ERD*

![image](https://user-images.githubusercontent.com/43429667/77630546-97245000-6f8e-11ea-92e3-db23e70fa4ca.png)



상속 관계를 테이블 하나에 통합하는 단일 테이블 전략을 선택했다. 따라서 ITEM 테이블 하나만 사용하고 DTYPE이라는 컬럼으로 자식 상품을 구분한다.



### 상속 관계 매핑

model.entity.item이라는 패키지에 상품과 숭품의 자식 클래스들을 모아두었다. 그리고 상품 클래스는 직접 생성해서 사용하지
않으므로 abstract를 추가해서 추상 클래스로 만들었다.

> *부모 엔티티 Item*<sup>상품</sup>

<script src="https://gist.github.com/9abd39603412a1e5bd2f583a4e80fb75.js"></script>

- 상속 관계를 매핑하기 위해 부모 클래스인 Item에 @Inheritance 애노테이션을 사용하고 strategy 속성에 InheritanceType.SINGLE_TABLE을 선택해서 단일 테이블 전략을 선택했다.
- 단일 테이블 전략은 구분 컬럼을 필수로 사용해야 한다. @DiscriminatorColumn을 사용하고 name 속성에 DTYPE이라는 구분 컬럼으로 사용할 이름을 주었다. (기본 값이 DTYPE이므로 생략이 가능하다.)



> *자식 엔티티 Album*

<script src="https://gist.github.com/e48d3db7120688b49256ca12a7c9fdf8.js"></script>

> *자식 엔티티 Book*

<script src="https://gist.github.com/423457a9f0ca2396c07c36f85ca1fa73.js"></script>

> *자식 엔티티 Movie*

<script src="https://gist.github.com/601e88c2b52421b0261e340b373c552c.js"></script>

자식 테이블은 @DiscriminatorValue를 사용하고 그값으로 구분 컬럼에 입력될 값을 정하면 된다.



### @MappedSuperclass 매핑

두 번째 요구사항을 만족하려면 모든 테이블에 등록일과 수정일 컬럼을 우선 추가해야 한다. 그리고 모든 엔티티에 등록일과 수정일을 추가하면 된다. 이ㄷ때 직접 추가하는 것 보다는 @MappedSuperclass를 사용해서 부모 클래스로부터 상속 받는 편이 효과적이다. 

> *기본 부모 엔티티*

<script src="https://gist.github.com/ede9f273efaf61a7c9d858576ba2a309.js"></script>

> *매핑 정보를 상속*

```java
public class Member extends BaseEntity {...}
public class Order extends BaseEntity {...}
```

