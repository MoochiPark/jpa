package io.wisoft.daewon.model.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "orders")
public class Order {

  @Id @GeneratedValue
  @Column(name = "order_id")
  private Long id;

  @Column(name = "member_id")
  private Long memberId;

  @Temporal(TemporalType.TIMESTAMP)
  private Date orderDate;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

}
