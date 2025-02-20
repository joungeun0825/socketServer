package org.example;

public class Main {
    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer(8080, 3);
        httpServer.start();
    }
}