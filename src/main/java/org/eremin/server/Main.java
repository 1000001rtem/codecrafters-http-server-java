package org.eremin.server;

import org.eremin.server.model.HttpResponse;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4221)) {
            serverSocket.setReuseAddress(true);
            Dispatcher dispatcher = new Dispatcher();
            initDispatcher(dispatcher);
            try (ExecutorService connectionPoll = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
                while (!serverSocket.isClosed()) {
                    var socket = serverSocket.accept();
                    connectionPoll.submit(() -> new ClientConnection(socket, dispatcher).run());
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void initDispatcher(Dispatcher dispatcher) {
        dispatcher.addEndpoint("/", request -> HttpResponse.OK_RESPONSE);
        dispatcher.addEndpoint("/echo", request -> HttpResponse.withTextBody(request.getPath().split("/")[2]));
        dispatcher.addEndpoint("/user-agent", request -> HttpResponse.withTextBody(request.getHeaders().get("User-Agent")));
    }
}

