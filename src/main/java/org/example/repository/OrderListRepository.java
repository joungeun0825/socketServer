package org.example.repository;
import org.example.domain.OrderList;

import javax.transaction.Transactional;
import java.sql.*;

public class OrderListRepository {

    private Connection connection;

    // 데이터베이스 연결
    public OrderListRepository(Connection connection) {
        this.connection = connection;
    }

    @Transactional
    // OrderList 객체를 데이터베이스에 저장하는 메소드
    public OrderList save(OrderList orderList) throws SQLException {
        String sql = "INSERT INTO order_list (total_price) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderList.getTotalPrice());
            stmt.executeUpdate();
        }
        return orderList;
    }
}
