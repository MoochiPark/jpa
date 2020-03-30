# Chapter 08. 실전 예제 - 연관관계 관리

예제에 글로벌 페치 전략을 설정하고, 영속성 전이 옵션을 추가해보자.



### 글로벌 페치 전략 설정

글로벌 페치 전략을 즉시 로딩으로 설정하면 사용하지 않는 엔티티도 함께 조회되므로 모두 지연 로딩으로 설정하자.
@OneToMany, @ManyToMany는 기본이 지연 로딩이므로 그냥 두고 @OneToOne ,@ManyToOne의 fetch 속성을 지연 로딩으로 수정하자. 주문과 주문 상품에 있다.

```java
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

  @Id @GeneratedValue
  @Column(name = "order_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToMany(mappedBy = "order")
  private List<OrderItem> orderItems = new ArrayList<>();

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_id")
  private Delivery delivery;
  ...
```

member와 delivery를 지연 로딩으로 설정했다.

```java
@Entity
@Table(name = "order_item")
public class OrderItem {

  @Id
  @GeneratedValue
  @Column(name = "order_item_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id")
  private Item item;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;
  ...
```



### 영속성 전이 설정

엔티티를 영속 상태로 만들어서 데이터베이스에 저장할 때 연관된 엔티티도 모두 영속 상태여야 한다. 플러시 시점에 연관된 엔티티 중 영속 상태가 아닌 엔티티가 있으면 예외가 발생한다.

주문과 배송, 주문과 주문 상품의 연관관계에 영속성 전이를 사용하자.

```java
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

  @Id @GeneratedValue
  @Column(name = "order_id")
  private Long id;

  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItem> orderItems = new ArrayList<>();
  ...
```

1. Order → Delivery 관계인 delivery 필드에 영속성 전이를 설정했다.
2. Order → OrderItem 관계인 orderItems 필드에 영속성 전이를 설정했다.



영속성 전이를 사용하기 전후 코드를 비교해보자.

> *영속성 전이 사용 전*

```java
Delivery delivery = new Delivery();
em.persist(delivery);

OrderItem orderItem1 = new OrderItem();
OrderItem orderItem2 = new OrderItem();
em.persist(orderItem1);
em.persist(orderItem2);

Order order = new Order();
order.setDelivery(delivery);
order.addOrderItem(orderItem1);
order.addOrderItem(orderItem2);

em.persist(order);
```



>*영속성 전이 사용 후*

```java
Delivery delivery = new Delivery();
OrderItem orderItem1 = new OrderItem();
OrderItem orderItem2 = new OrderItem();

Order order = new Order();
order.setDelivery(delivery);
order.addOrderItem(orderItem1);
order.addOrderItem(orderItem2);

em.persist(order);
```

Order만 영속 상태로 만들면 영속성 전이로 설정한 delivery, orderItems도 영속 상태가 된다.



