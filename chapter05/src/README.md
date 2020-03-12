# Chapter 05. 실전 예제 - 연관관계 매핑 시작

앞 장의 실전 예제는 외래 키를 엔티티에 그대로 가져오는 문제가 있었다.
엔티티에서 외래 키로 사용한 필드는 제거하고 참조를 사용하도록 변경해보자.

테이블 구조는 이전과 같다.

> *실전 예제 2 ERD*

![](https://user-images.githubusercontent.com/43429667/75963346-a7ee2280-5f08-11ea-826c-1309ff2fba9c.png)



다음 객체 관계는 외래 키를 직접 사용하는 것에서 참조를 사용하도록 변경했다.

> *실전 예제 2 UML 상세*

![image](https://user-images.githubusercontent.com/43429667/76503694-a7660680-6489-11ea-92ee-76866c38adcb.png)



### 일대다, 다대일 연관관계 매핑

> *회원 엔티티*

<script src="https://gist.github.com/9983251cf8bcbf8ed36822901aefd8df.js">



> *주문 엔티티*

<script src="https://gist.github.com/ac175e91308d55a3986fc5c28fc4150d.js">

회원과 주문은 일대다 관계고 그 반대인 주문과 회원은 다대일 관계다.

Order → Member로 참조하는 Order.member 필드와 Member → Order로 참조하는 Member.orders 필드 중에 
외래 키가 있는 Order.member가 연관관계의 주인이다. 따라서 주인이 아닌 Member.orders에는 @OneToMany 속성에
mappedBy를 선언해서 연관관계의 주인으로 member를 지정했다. 참고로 여기서 지정한 member는 Order.member다.



- **연관관계 편의 메서드**

  양방향 연관관계인 두 엔티티 간에 관계를 맺을 때는 원래 다음처럼 설정해야 한다.

  `Member member = new Member();`

  `Order order = new Order();`

  `member.getOrders().add(this);`

  `order.setMember(member);`

  여기서는 setMember() 라는 연관관계 편의 메서드를 추가했으므로 사용하면 된다.

  `order.setMember(member);`



> *주문상품 엔티티*

<script src="https://gist.github.com/7e16d5ab1b498721fc86a9e95706e444.js">

주문과 주문상품은 일대다 관계고 그 반대는 다대일 관계다. OrderItem → Order로 참조하는 OrderItem.order 필드와
Order → OrderItem으로 참조하는 Order.orderItems 필드 둘 중에 외래 키가 있는 Order.orderItems 필드에는
mappedBy 속성을 사용해서 주인이 아님을 표시했다.

> *상품 엔티티*

<script src="https://gist.github.com/a542607b69e8e4ec1c0350b58dbb1456.js">

비즈니스 요구사항을 분석해본 결과 주문상품에서 상품을 참조할 일은 많지만, 상품에서 주문상품을 참조할 일은 거의 없었다.
따라서 주문상품과 상품은 다대일 단방향 관계로 설정했다. 즉 OrderItem → Item 방향으로 참조하는 OrderItem.item 필드만 사용해서 다대일 단방향 관계로 설정했다.



### 객체 그래프 탐색

이제 객체에서 참조를 사용할 수 있으므로, 객체 그래프를 탐색할 수 있고, JPQL에서도 사용할 수 있다.
주문한 회원을 객체 그래프로 탐색해보자.

```java
Order order = em.find(Order.class, orderId);
Member member = order.getMember(); // 주문한 회원, 참조 사용

// 주문한 상품 하나를 객체 그래프로 탐색해보자.
Order order = em.find(Order.class, orderId);
orderItem = order.getOrderItems().get(0);
Item item = orderItem.getItem();
```

