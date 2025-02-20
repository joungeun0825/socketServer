package org.example;
import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class RequestHandler implements Runnable {
    private final Socket clientSocket;
    private final HttpRouter router;

    public RequestHandler(Socket clientSocket, HttpRouter router) {
        this.clientSocket = clientSocket;
        this.router = router;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // 요청 라인 읽기
            String requestLine = in.readLine();
            System.out.println("request: " + requestLine);
            if (requestLine == null || requestLine.isEmpty()) return;

            // HTTP 메서드와 URL 파싱
            StringTokenizer tokenizer = new StringTokenizer(requestLine);
            String method = tokenizer.nextToken(); // GET, POST
            String path = tokenizer.nextToken();   // 요청된 URL

            // POST 요청이면 바디 읽기
            StringBuilder body = new StringBuilder();
            if ("POST".equalsIgnoreCase(method)) {
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {}
                while (in.ready()) {
                    body.append((char) in.read());
                }
            }

            // 라우터를 통해 응답 생성
            HttpResponse response = router.route(method, path, body.toString());

            // 응답 전송
            out.print(response.toString());
            out.flush();

            // 클라이언트 소켓 닫기
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
