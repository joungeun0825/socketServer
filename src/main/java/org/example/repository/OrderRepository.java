package org.example.repository;
import org.example.domain.Order;

import java.sql.*;

public class OrderRepository {

    private Connection connection;

    // 데이터베이스 연결
    public OrderRepository(Connection connection) {
        this.connection = connection;
    }

    // Order 객체를 데이터베이스에 저장하는 메소드
    public void save(Order order) throws SQLException {
        // 먼저 id가 이미 존재하는지 확인
        String checkSql = "SELECT COUNT(*) FROM `order` WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, order.getId());  // order의 id를 사용해서 체크
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // 이미 같은 ID가 존재하면 패스
                    System.out.println("Order with id " + order.getId() + " already exists, skipping insert.");
                    return;
                }
            }
        }

        // ID가 존재하지 않으면 INSERT
        String sql = "INSERT INTO `order` (id, menu_type, main_menu_name, quantity, order_list_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, order.getId());  // id 설정
            stmt.setString(2, order.getMenuType());
            stmt.setString(3, order.getMainMenuName());
            stmt.setInt(4, order.getQuantity());
            stmt.setLong(5, order.getOrderList().getId());  // orderList의 ID 설정

            stmt.executeUpdate();
        }
    }


}
