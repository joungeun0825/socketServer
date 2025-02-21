package org.example.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {

    private String id;
    private String menuType;
    private String mainMenuName;
    private int quantity;

    // 기본 생성자
    public OrderDto() {
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

    // toString 메서드
    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", menuType='" + menuType + '\'' +
                ", mainMenuName='" + mainMenuName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
