package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.dto.HttpRequest;
import org.example.domain.Order;
import org.example.domain.OrderList;
import org.example.dto.MappingDto;
import org.example.dto.OrderDto;
import org.example.repository.OrderListRepository;
import org.example.repository.OrderRepository;
import org.example.util.ResponseBodyUtil;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class OrderListService {

    private static final String HOST = "bbq.co.kr"; // API 서버 주소
    private static final int PORT = 443;
    private static final String API_URI = "/api/delivery/cart/list";

    private final LoginService loginService;
    private final OrderListRepository orderListRepository;
    private final OrderRepository orderRepository;

    public OrderListService(LoginService loginService, Connection connection) {
        this.loginService = loginService;
        this.orderListRepository = new OrderListRepository(connection);
        this.orderRepository = new OrderRepository(connection);
    }

    public MappingDto save() throws SQLException, JsonProcessingException {
        MappingDto mappingDto = getList();

        OrderList orderList = orderListRepository.save(OrderList.from(mappingDto));
        System.out.println(orderList.getId());
        for (OrderDto orderDto : mappingDto.getResponseList()) {
            orderRepository.save(Order.from(orderDto, orderList));
        }

        return mappingDto;
    }

    public MappingDto getList() throws JsonProcessingException {
        // JSON 형식의 payload
        String payload = """
        {
            "branchId": "",
            "mealType": "DELIVERY",
            "latitude": 37.35886358148,
            "longitude": 126.7726331339,
            "legalDongId": "4139012700",
            "administrativeDongId": "4139058100",
            "ecouponList": []
        }""";

        // 로그인 후 쿠키 가져오기
        String loginCookie = loginService.getSessionCookie();


        Map<String, String> headers = getHeaders(loginCookie, payload);
        String response = HttpRequest.sendHttpRequest(API_URI, "POST", payload, headers);
        System.out.println(response);
        return new ObjectMapper().readValue(ResponseBodyUtil.extractResponseBody(response.toString()), MappingDto.class);
    }

    private Map<String, String> getHeaders(String loginCookie, String payload) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("Cookie", loginCookie);
        headers.put("Content-Length", String.valueOf(payload.getBytes(StandardCharsets.UTF_8).length));
        return headers;
    }

}
