package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.Order;
import org.example.domain.OrderList;
import org.example.dto.MappingDto;
import org.example.dto.OrderDto;
import org.example.repository.OrderListRepository;
import org.example.repository.OrderRepository;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

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

    public MappingDto save() throws SQLException {
        MappingDto mappingDto = getList();

        OrderList orderList = orderListRepository.save(OrderList.from(mappingDto));
        System.out.println(orderList.getId());
        for (OrderDto orderDto : mappingDto.getResponseList()) {
            orderRepository.save(Order.from(orderDto, orderList));
        }

        return mappingDto;
    }

    public MappingDto getList() {
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

        return sendHttpRequest(API_URI, "POST", payload, loginCookie);
    }

    private MappingDto sendHttpRequest(String path, String method, String body, String loginCookie) {
        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(HOST, PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            // 요청 헤더 작성
            out.println(method + " " + path + " HTTP/1.1");
            out.println("Host: " + HOST);
            out.println("Accept: application/json");
            out.println("Content-Type: application/json");
            out.println("Cookie: " + loginCookie);  // 로그인 쿠키 추가

            if (body != null) {
                out.println("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length);
            }

            out.println();  // 헤더 끝

            // 본문 데이터 전송
            if (body != null) {
                out.write(body);
                out.flush();
            }

            socket.shutdownOutput();

            // 서버 응답 수신
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            System.out.println(response.toString());

            // 응답 본문을 추출하여 MappingDto로 변환
            return new ObjectMapper().readValue(extractResponseBody(response.toString()), MappingDto.class);

        } catch (IOException e) {
            throw new RuntimeException("HTTP 요청 실패", e);
        }
    }

    private String extractResponseBody(String response) {
        // 응답에서 JSON 형식의 본문이 시작하는 부분 찾기
        int bodyStart = response.indexOf("{");

        // 본문이 없는 경우 처리
        if (bodyStart == -1) {
            return "";  // 본문이 없으면 빈 문자열 반환
        }

        // 본문이 끝나는 부분 찾기
        int bodyEnd = response.lastIndexOf("}");

        // 본문 끝이 없는 경우 처리
        if (bodyEnd == -1) {
            return "";  // 본문 끝이 없으면 빈 문자열 반환
        }

        // 본문 추출
        return response.substring(bodyStart, bodyEnd + 1);  // JSON은 { ~ } 범위로 반환
    }
}
