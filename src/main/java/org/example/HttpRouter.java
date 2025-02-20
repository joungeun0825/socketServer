package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HttpRouter {
    private final Map<String, Function<String, String>> getRoutes = new HashMap<>();
    private final Map<String, Function<String, String>> postRoutes = new HashMap<>();

    public void addGetRoute(String path, Function<String, String> handler) {
        getRoutes.put(path, handler);
    }

    public void addPostRoute(String path, Function<String, String> handler) {
        postRoutes.put(path, handler);
    }

    public String route(String method, String path, String body) {
        if ("GET".equalsIgnoreCase(method) && getRoutes.containsKey(path)) {
            return getRoutes.get(path).apply(body);
        } else if ("POST".equalsIgnoreCase(method) && postRoutes.containsKey(path)) {
            return postRoutes.get(path).apply(body);
        } else {
            return "Not Found";
        }
    }
}
