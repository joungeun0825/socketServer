package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HttpRouter {
    private final Map<String, Function<String, HttpResponse>> getRoutes = new HashMap<>();
    private final Map<String, Function<String, HttpResponse>> postRoutes = new HashMap<>();

    public void addGetRoute(String path, Function<String, HttpResponse> handler) {
        getRoutes.put(path, handler);
    }

    public void addPostRoute(String path, Function<String, HttpResponse> handler) {
        postRoutes.put(path, handler);
    }

    public HttpResponse route(String method, String path, String body) {
        if ("GET".equalsIgnoreCase(method) && getRoutes.containsKey(path)) {
            return getRoutes.get(path).apply(body);
        } else if ("POST".equalsIgnoreCase(method) && postRoutes.containsKey(path)) {
            return postRoutes.get(path).apply(body);
        } else {
            return new HttpResponse(404, "Not Found", "<h1>404 Not Found</h1>");
        }
    }
}
