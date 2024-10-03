package org.eremin.server;

import org.eremin.server.model.HttpMethod;
import org.eremin.server.model.HttpRequest;
import org.eremin.server.model.HttpResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Dispatcher {

    private final Map<String, Map<HttpMethod, Function<HttpRequest, HttpResponse>>> endpoints;

    public Dispatcher() {
        endpoints = new HashMap<>();
    }

    public void addEndpoint(String path, HttpMethod method, Function<HttpRequest, HttpResponse> handler) {
        endpoints.computeIfAbsent(path, k -> new HashMap<>()).put(method, handler);
    }

    public HttpResponse dispatch(HttpRequest request) {
        var route = endpoints.get(getRoute(request.getPath()));
        if (route == null) return HttpResponse.NOT_FOUND_RESPONSE;
        System.out.println(route);
        return route.getOrDefault(request.getMethod(), r -> HttpResponse.METHOD_NOT_ALLOWED_RESPONSE).apply(request);
    }

    private String getRoute(String path) {
        if("/".equals(path)) {
            return path;
        }
        return "/" + path.split("/")[1];
    }
}
