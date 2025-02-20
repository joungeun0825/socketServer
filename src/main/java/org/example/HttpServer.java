package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private final int port;
    private final ExecutorService threadPool;
    private final HttpRouter router;

    public HttpServer(int port, int threadPoolSize) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
        this.router = new HttpRouter();
        configureRoutes();
    }

    private void configureRoutes() {
        router.addGetRoute("/api/orderList", body -> new HttpResponse(200, "OK", "<h1>Current Time: " + System.currentTimeMillis() + "</h1>"));
        router.addPostRoute("/api/orderList/save", body -> new HttpResponse(200, "OK", "<h1>Echo: " + body + "</h1>"));
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("HTTP server port:" + port + " is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("new connection: " + clientSocket.getInetAddress());

                threadPool.execute(new RequestHandler(clientSocket, router));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
