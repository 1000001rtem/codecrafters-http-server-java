package org.eremin.server;

import org.eremin.server.exception.FileNotFoundException;
import org.eremin.server.model.HttpResponse;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4221)) {
            serverSocket.setReuseAddress(true);

            Dispatcher dispatcher = new Dispatcher();
            var arguments = parseArgs(args);
            FileReader fileReader = new FileReader(arguments.get("directory"));
            initDispatcher(dispatcher, fileReader);

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

    private static void initDispatcher(Dispatcher dispatcher, FileReader fileReader) {
        dispatcher.addEndpoint("/", request -> HttpResponse.OK_RESPONSE);
        dispatcher.addEndpoint("/echo", request -> HttpResponse.withTextBody(request.getPath().split("/")[2]));
        dispatcher.addEndpoint("/user-agent", request -> HttpResponse.withTextBody(request.getHeaders().get("User-Agent")));
        dispatcher.addEndpoint("/files", request -> {
            try {
                return HttpResponse.fileResponse(fileReader.read(request.getPath().split("/")[2]));
            } catch (FileNotFoundException e) {
                return HttpResponse.NOT_FOUND_RESPONSE;
            }
        });
    }

    private static Map<String, String> parseArgs(String[] args) {
        var map = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            if (args[i].startsWith("--")) {
                map.put(args[i].substring(2), args[i + 1]);
            }
        }
        return map;
    }
}

