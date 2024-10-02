package org.eremin.server;

import org.eremin.server.model.HttpRequest;
import org.eremin.server.model.HttpResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Dispatcher {

    private final Map<String, Function<HttpRequest, HttpResponse>> endpoints;

    public Dispatcher() {
        endpoints = new HashMap<>();
    }

    public void addEndpoint(String path, Function<HttpRequest, HttpResponse> handler) {
        endpoints.put(path, handler);
    }

    public HttpResponse dispatch(HttpRequest request) {
        return endpoints.getOrDefault(getRoute(request.getPath()), r -> HttpResponse.NOT_FOUND_RESPONSE).apply(request);
    }

    private String getRoute(String path) {
        if("/".equals(path)) {
            return path;
        }
        return "/" + path.split("/")[1];
    }
}
