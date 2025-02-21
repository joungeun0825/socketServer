package org.example;

import org.example.config.DatabaseConfig;
import org.example.config.HttpServer;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection connection = DatabaseConfig.connect();
        HttpServer httpServer = new HttpServer(8080, 3, connection);
        httpServer.start();

    }
}