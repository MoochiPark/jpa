package io.wisoft.daewon.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
public class OrderItem {

  @Id @GeneratedValue
  @Column(name = "order_item_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "item_id")
  private Item item;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  private int orderPrice;
  private int count;

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(final Item item) {
    this.item = item;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(final Order order) {
    this.order = order;
  }

  public int getOrderPrice() {
    return orderPrice;
  }

  public void setOrderPrice(final int orderPrice) {
    this.orderPrice = orderPrice;
  }

  public int getCount() {
    return count;
  }

  public void setCount(final int count) {
    this.count = count;
  }

}
