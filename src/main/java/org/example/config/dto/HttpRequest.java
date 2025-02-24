package org.example.config.dto;

import java.io.PrintWriter;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class HttpRequest {

    private static final String HOST = "bbq.co.kr";
    private static final int PORT = 443;

    // 기본 요청 헤더 작성


    private static void addHeaders(PrintWriter out, String path, String method, Map<String, String> headers) {
        out.println(method + " " + path + " HTTP/1.1");
        out.println("Host: " + HOST);

        // 동적으로 헤더 추가
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                out.println(entry.getKey() + ": " + entry.getValue());
            }
        }

        out.println(); // 헤더 끝
    }

    // HTTP 요청 보내기
    public static String sendHttpRequest(String path, String method, String body, Map<String, String> headers) {
        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(HOST, PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            // 요청 헤더 작성
            addHeaders(out, path, method, headers);

            // 본문 데이터 전송 (POST 요청의 경우)
            if (body != null) {
                out.write(body);
                out.flush();
            }

            // 서버에 더 이상 보낼 데이터가 없음을 명시
            socket.shutdownOutput();

            // 서버 응답 수신
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            return response.toString();

        } catch (IOException e) {
            throw new RuntimeException("HTTP 요청 실패: " + e.getMessage(), e);
        }
    }

    // URL 인코딩된 폼 데이터 생성
    public static String buildFormData(Map<String, String> params) {
        StringBuilder formData = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (formData.length() > 0) formData.append("&");
            formData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return formData.toString();
    }
}
