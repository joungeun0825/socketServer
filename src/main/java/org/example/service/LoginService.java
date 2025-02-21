package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.util.CookieUtil;
import org.example.dto.LoginRequest;
import org.example.dto.CsrfDto;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginService {

    private static final String HOST = "bbq.co.kr";
    private static final int PORT = 443;
    private static final String CSRF_URI = "/api/auth/csrf";

    private String sessionCookie;
    private String csrfToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public int login(LoginRequest loginRequest) {
        getCsrfToken(); // CSRF 토큰 가져오기

        // 로그인 폼 데이터 작성
        String formData = buildFormData(loginRequest);

        // 로그인 요청 보내기
        String response = sendHttpRequest("/api/auth/callback/member", "POST", formData, true);

        // 응답에서 쿠키 추출
        sessionCookie = CookieUtil.extractCookie(response);

        // 응답 코드 반환
        return response.contains("HTTP/1.1 200 OK") ? 200 : 401;
    }

    private void getCsrfToken() {
        // CSRF 토큰 요청
        String response = sendHttpRequest(CSRF_URI, "GET", null, false);
        String responseBody = extractResponseBody(response);

        // 응답에서 JSON 파싱
        try {
            CsrfDto csrfDto = objectMapper.readValue(responseBody, CsrfDto.class);
            csrfToken = csrfDto.getCsrfToken();
            sessionCookie = CookieUtil.extractCookie(response);
        } catch (IOException e) {
            throw new RuntimeException("CSRF 토큰 파싱 실패", e);
        }
    }

    private String sendHttpRequest(String path, String method, String body, boolean includeCookies) {
        System.out.println("🚀 HTTP 요청 시작...");
        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(HOST, PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            // 🔹 요청 헤더 작성
            out.println(method + " " + path + " HTTP/1.1");
            out.println("Host: " + HOST);
            out.println("Accept: */*");
            out.println("Origin: https://bbq.co.kr");
            out.println("Referer: https://bbq.co.kr/member/login");

            if (includeCookies && sessionCookie != null) {
                out.println("Cookie: " + sessionCookie);
            }

            if (body != null) {
                out.println("Content-Type: application/x-www-form-urlencoded");
                out.println("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length);
            }

            out.println();

            // 🔹 본문 데이터 전송 (POST 요청의 경우)
            if (body != null) {
                out.write(body);
                out.flush();
            }

            // 🔹 서버에 더 이상 보낼 데이터가 없음을 명시
            socket.shutdownOutput();

            // 🔹 서버 응답 수신
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            System.out.println("서버 응답:\n" + response);
            return response.toString();

        } catch (IOException e) {
            throw new RuntimeException("HTTP 요청 실패: " + e.getMessage(), e);
        }
    }


    private String buildFormData(LoginRequest loginRequest) {
        Map<String, String> params = new HashMap<>();
        params.put("username", loginRequest.getUsername());
        params.put("password", loginRequest.getPassword());
        params.put("redirect", "false");
        params.put("csrfToken", csrfToken);
        params.put("callbackUrl", "https://bbq.co.kr/member/login");
        params.put("json", "true");

        // URL 인코딩된 폼 데이터 생성
        StringBuilder formData = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (formData.length() > 0) formData.append("&");
            formData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return formData.toString();
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

    public String getSessionCookie() {
        return this.sessionCookie;
    }
}
