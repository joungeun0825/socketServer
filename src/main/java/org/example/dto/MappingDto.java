package org.example.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MappingDto {

    private Integer totalPrice;
    private List<OrderDto> responseList;

    // 기본 생성자
    public MappingDto() {
    }

    // Getter와 Setter
    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderDto> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<OrderDto> responseList) {
        this.responseList = responseList;
    }

    // toString 메서드
    @Override
    public String toString() {
        return "MappingDto{" +
                "totalPrice=" + totalPrice +
                ", responseList=" + responseList +
                '}';
    }
}
