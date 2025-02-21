package org.example.domain;

import org.example.dto.OrderDto;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Order {

    @Id
    private String id;

    private String menuType;
    private String mainMenuName;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)  // OrderList와의 관계 설정
    @JoinColumn(name = "order_list_id")  // Foreign Key 설정
    private OrderList orderList;

    // 기본 생성자
    public Order() {}

    // OrderDto와 OrderList를 기반으로 생성하는 생성자
    public Order(OrderDto orderDto, OrderList orderList) {
        this.id = orderDto.getId();
        this.menuType = orderDto.getMenuType();
        this.mainMenuName = orderDto.getMainMenuName();
        this.quantity = orderDto.getQuantity();
        this.orderList = orderList;
    }

    // Getter와 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getMainMenuName() {
        return mainMenuName;
    }

    public void setMainMenuName(String mainMenuName) {
        this.mainMenuName = mainMenuName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderList getOrderList() {
        return orderList;
    }

    public void setOrderList(OrderList orderList) {
        this.orderList = orderList;
    }

    // from 메소드
    public static Order from(OrderDto orderDto, OrderList orderList) {
        return new Order(orderDto, orderList);
    }

    // equals와 hashCode 메소드 (id를 기준으로 비교)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString 메소드
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", menuType='" + menuType + '\'' +
                ", mainMenuName='" + mainMenuName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
