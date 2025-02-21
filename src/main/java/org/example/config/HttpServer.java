package org.example.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.controller.OrderListController;
import org.example.RequestHandler;
import org.example.dto.MappingDto;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private final int port;
    private final ExecutorService threadPool;
    private final HttpRouter router;
    private final OrderListController orderListController;

    public HttpServer(int port, int threadPoolSize, Connection connection) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
        this.router = new HttpRouter();
        this.orderListController = new OrderListController(connection);
        configureRoutes();
    }

    private MappingDto configureRoutes() {
        router.addPostRoute("/api/orderList", body -> {
            try {
                return orderListController.getOrderList(body);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        router.addPostRoute("/api/orderList/save", body -> {
            try {
                return orderListController.saveOrderList(body);
            } catch (JsonProcessingException | SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return new MappingDto();
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
