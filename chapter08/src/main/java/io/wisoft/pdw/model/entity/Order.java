package io.wisoft.pdw.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_id")
  private Delivery delivery;

  private Date orderDate;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  public void setMember(final Member member) {
    if (this.member != null) {
      this.member.getOrders().remove(this);
    }
    this.member = member;
    member.getOrders().add(this);
  }

  public void addOrderItem(final OrderItem orderItem) {
    orderItems.add(orderItem);
    orderItem.setOrder(this);
  }

  public void setDelivery(final Delivery delivery) {
    this.delivery = delivery;
    delivery.setOrder(this);
  }

}
