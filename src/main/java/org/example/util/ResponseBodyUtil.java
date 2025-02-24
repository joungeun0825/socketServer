package org.example.util;

public class ResponseBodyUtil {
    public static String extractResponseBody(String response) {
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
