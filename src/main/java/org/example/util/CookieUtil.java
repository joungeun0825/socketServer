package org.example.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CookieUtil {
    public static String extractCookie(String responseHeaders) {

        List<String> cookies = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(responseHeaders))) {
            String line;
            // 한 줄씩 읽기
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("Set-Cookie:")) {
                    // "Set-Cookie: " 이후 값을 저장
                    cookies.add(line.substring("Set-Cookie:".length()).trim());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("응답 헤더를 읽는 중 오류가 발생했습니다.", e);
        }
        if (cookies.isEmpty()) {
            throw new IllegalArgumentException("쿠키를 가져올 수 없습니다.");
        }

        // 여러 쿠키가 있을 수 있으므로 세미콜론(;)으로 구분하여 반환
        return String.join("; ", cookies);
    }
}
