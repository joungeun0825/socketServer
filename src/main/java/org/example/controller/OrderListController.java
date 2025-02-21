package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.LoginRequest;
import org.example.dto.MappingDto;
import org.example.service.LoginService;
import org.example.service.OrderListService;

import java.sql.Connection;
import java.sql.SQLException;

public class OrderListController {

    private final LoginService loginService;
    private final OrderListService orderListService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public OrderListController(Connection connection) {
        this.loginService = new LoginService();
        this.orderListService = new OrderListService(loginService, connection);
    }

    public MappingDto getOrderList(String body) throws JsonProcessingException {
        LoginRequest loginRequest = objectMapper.readValue(body, LoginRequest.class);
        int statusCode = loginService.login(loginRequest);
        if (statusCode == 200) {
            return orderListService.getList();
        }
        return new MappingDto();
    }

    public MappingDto saveOrderList(String body) throws JsonProcessingException, SQLException {
        LoginRequest loginRequest = objectMapper.readValue(body, LoginRequest.class);
        int statusCode = loginService.login(loginRequest);
        if (statusCode == 200) {
            return orderListService.save();
        }
        return new MappingDto();
    }
}
