package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.dto.HttpRequest;
import org.example.util.CookieUtil;
import org.example.dto.LoginRequest;
import org.example.dto.CsrfDto;
import org.example.util.ResponseBodyUtil;

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
        String beforeLoginCsrfToken = getCsrfToken(); // CSRF 토큰 가져오기

        Map<String, String> params = new HashMap<>();
        params.put("username", loginRequest.getUsername());
        params.put("password", loginRequest.getPassword());
        params.put("redirect", "false");
        params.put("csrfToken", csrfToken);
        params.put("callbackUrl", "https://bbq.co.kr/member/login");
        params.put("json", "true");

        // 로그인 폼 데이터 작성
        String formData = HttpRequest.buildFormData(params);

        System.out.println("before " + beforeLoginCsrfToken);

        Map<String, String> headers = getDefaultHeaders();
        headers.put("Cookie", beforeLoginCsrfToken);
        headers.put("Content-Length", String.valueOf(formData.getBytes(StandardCharsets.UTF_8).length));


        // 로그인 요청 보내기
        String response = HttpRequest.sendHttpRequest("/api/auth/callback/member", "POST", formData, headers);

        // 응답에서 쿠키 추출
        sessionCookie = CookieUtil.extractCookie(response);

        // 응답 코드 반환
        return response.contains("HTTP/1.1 200 OK") ? 200 : 401;
    }

    private String getCsrfToken() {
        Map<String, String> headers = getDefaultHeaders();
        // CSRF 토큰 요청
        String response = HttpRequest.sendHttpRequest(CSRF_URI, "GET", null, headers);
        String responseBody = ResponseBodyUtil.extractResponseBody(response);

        // 응답에서 JSON 파싱
        try {
            CsrfDto csrfDto = objectMapper.readValue(responseBody, CsrfDto.class);
            csrfToken = csrfDto.getCsrfToken();
            return CookieUtil.extractCookie(response);
        } catch (IOException e) {
            throw new RuntimeException("CSRF 토큰 파싱 실패", e);
        }
    }

    private Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Origin", "https://bbq.co.kr");
        headers.put("Referer", "https://bbq.co.kr/member/login");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }

    public String getSessionCookie() {
        return this.sessionCookie;
    }
}
