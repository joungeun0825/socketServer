package org.example.domain;

import org.example.dto.MappingDto;
import org.example.dto.OrderDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class OrderList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalPrice;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderList", orphanRemoval = true)
    private List<Order> order;

    // 기본 생성자
    public OrderList() {
    }

    // MappingDto를 받아서 OrderList 객체를 초기화하는 생성자
    public OrderList(MappingDto mappingDto) {
        this.totalPrice = mappingDto.getTotalPrice();
    }

    // getter, setter 메서드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Order> getOrder() {
        return order;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }

    // from 메서드
    public static OrderList from(MappingDto mappingDto) {
        OrderList orderList = new OrderList();
        orderList.setId(1L);
        orderList.setTotalPrice(mappingDto.getTotalPrice());

        // MappingDto의 responseList를 Order 객체로 변환하여 order 필드에 설정
        List<Order> orders = new ArrayList<>();
        for (OrderDto orderDto : mappingDto.getResponseList()) {
            Order order = Order.from(orderDto, orderList);  // OrderList를 넣어줌
            orders.add(order);
        }
        orderList.setOrder(orders); // orders 리스트를 orderList에 설정

        return orderList;
    }

    // toString 메서드
    @Override
    public String toString() {
        return "OrderList{" +
                "id=" + id +
                ", totalPrice=" + totalPrice +
                ", orderCount=" + (order != null ? order.size() : 0) +  // Order 리스트 크기만 출력
                '}';
    }
}
