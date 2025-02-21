package org.example.config;

import org.example.dto.MappingDto;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HttpRouter {
    private final Map<String, Function<String, MappingDto>> getRoutes = new HashMap<>();
    private final Map<String, Function<String, MappingDto>> postRoutes = new HashMap<>();

    public void addGetRoute(String path, Function<String, MappingDto> handler) {
        getRoutes.put(path, handler);
    }

    public void addPostRoute(String path, Function<String, MappingDto> handler) {
        postRoutes.put(path, handler);
    }

    public MappingDto route(String method, String path, String body) {
        if ("GET".equalsIgnoreCase(method) && getRoutes.containsKey(path)) {
            return getRoutes.get(path).apply(body);
        } else if ("POST".equalsIgnoreCase(method) && postRoutes.containsKey(path)) {
            return postRoutes.get(path).apply(body);
        } else {
            return new MappingDto();
        }
    }
}
