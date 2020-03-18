# Chapter 06. 실전 예제 - 다양한 연관관계 매핑

다음 이전 실전 예제에서 다음의 요구사항이 추가되었다.

- 상품을 주문할 때 배송 정보를 입력할 수 있다. 주문과 배송은 일대일 관계다.
- 상품을 카테고리로 구분할 수 있다.



> 실전 예제 3 UML

![image](https://user-images.githubusercontent.com/43429667/76961955-a89fa380-6961-11ea-9f6c-8c838555e372.png)

배송 엔티티와 카테고리 엔티티가 추가되었다.



> *실전 예제 3 ERD*

![image-20200318215641136](/Users/daewon/Library/Application Support/typora-user-images/image-20200318215641136.png)

테이블이 추가된 ERD를 분석해보자.

- **주문과 배송**: 주문<sup>ORDERS</sup>과 배송<sup>DELIVERY</sup>은 일대일 관계다. 객체 관계를 고려할 때 주문에서 배송으로 자주 접근할 예정이므로 외래 키를 주문 테이블에 두었다. 참고로 일대일 관계이므로 ORDERS 테이블에 있는 DELIVERY_ID 외래 키에는
  유니크 제약조건을 주는 것이 좋다.
- **상품과 카테고리**: 한 상품은 여러 카테고리<sup>CATREGORY</sup>에 속할 수 있고, 한 카테고리도 여러 상품을 가질 수 있으므로 둘은
  다대다 관계다.

추가된 요구사항을 객체에 반영해서 상세한 엔티티를 완성했다.

> *실전 예제 3 UML 상세*

![image](https://user-images.githubusercontent.com/43429667/76964290-d555ba00-6965-11ea-9e72-a30a13ae7c8a.png)



#### 일대일 매핑

> *주문 엔티티*

<script src="https://gist.github.com/e904aec4ad4dbf6dae9de9ca700dc444.js"></script>

> *배송 엔티티*

<script src="https://gist.github.com/97a99d277e337c2ff57a9ba126214bfa.js"></script>

> 배송 상태

<script src="https://gist.github.com/bf15bdb2f0cc332104766f24218025ff.js"></script>

Order와 Delivery는 일대일 관계고 그 반대도 일대일 관계다. 
여기서는 Order가 매핑된 ORDERS를 주 테이블로 보고 주 테이블에 외래 키를 두었다.
따라서 외래 키가 있는 Order.delivery가 연관관계의 주인이다. 주인이 아닌 Delivery.order 필드에는 mappedBy를 줬다.



#### 다대다 매핑

> *카테고리 엔티티*

<script src="https://gist.github.com/6605651dd9738d749fad1c47a4f1c9f8.js"></script>

> *상품 엔티티*

<script src="https://gist.github.com/fa55e32aa9647256f3628c4bdc776544.js"></script>

Category와 Item은 다대다 관계, 그 반대도 다대다 관계다. Category.items 필드를 보면 @ManyToMany와 @JoinTable을 사용해서 CATEGORY_ITEM 연결 테이블을 바로 매핑했다. 그리고 여기서는 Category를 연관관계의 주인으로 정했다.

다대다 관계는 연결 테이블을 JPA가 알아서  처리해주므로 편리하지만 연결 테이블에 필드가 추가되면 더는 사용할 수 없으므로
실무에서 활용하기에는 무리가 있다. 따라서 CategoryItem이라는 연결 엔티티를 만들어서 일대다, 다대일 관계로 매핑하는 것을 권장한다. 