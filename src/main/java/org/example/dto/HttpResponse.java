package org.example.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private final int statusCode;
    private final String statusMessage;
    private final String body;

    public HttpResponse(int statusCode, String statusMessage, MappingDto body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.body = convertBodyToJson(body);
    }

    private String convertBodyToJson(Object body) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public byte[] getResponseBytes() {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "Connection: close\r\n" +  // Keep-alive 대신 Connection: close 사용
                "\r\n";

        byte[] headerBytes = headers.getBytes(StandardCharsets.UTF_8);
        byte[] responseBytes = new byte[headerBytes.length + bodyBytes.length];

        System.arraycopy(headerBytes, 0, responseBytes, 0, headerBytes.length);
        System.arraycopy(bodyBytes, 0, responseBytes, headerBytes.length, bodyBytes.length);

        return responseBytes;
    }
}
