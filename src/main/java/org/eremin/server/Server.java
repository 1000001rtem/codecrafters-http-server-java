package org.eremin.server;

import org.eremin.server.encoder.CompressionService;
import org.eremin.server.encoder.Encoder;
import org.eremin.server.encoder.EncodingType;
import org.eremin.server.encoder.GzipEncoder;
import org.eremin.server.exception.FileNotFoundException;
import org.eremin.server.model.HttpMethod;
import org.eremin.server.model.HttpResponse;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server {

    private final Integer port;

    public Server(Integer port) {
        this.port = port;
    }

    public void start(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4221)) {
            serverSocket.setReuseAddress(true);

            System.out.println("Server started on port " + port);
            var arguments = parseArgs(args);

            Dispatcher dispatcher = new Dispatcher();
            FileHelper fileReader = new FileHelper(arguments.get("directory"));
            initDispatcher(dispatcher, fileReader);

            var encoders = createEncoders();
            CompressionService compressionService = new CompressionService(encoders);

            try (ExecutorService connectionPoll = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
                while (!serverSocket.isClosed()) {
                    var socket = serverSocket.accept();
                    connectionPoll.submit(() -> new ClientConnection(socket, dispatcher, compressionService).run());
                }
            }
            System.out.println("Bye");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Map<EncodingType, Encoder> createEncoders() {
        return Stream.of(new GzipEncoder()).collect(Collectors.toMap(Encoder::getType, Function.identity()));
    }

    private void initDispatcher(Dispatcher dispatcher, FileHelper fileHelper) {
        dispatcher.addEndpoint("/", HttpMethod.GET, request -> HttpResponse.OK_RESPONSE);
        dispatcher.addEndpoint("/echo", HttpMethod.GET, request -> HttpResponse.withTextBody(request.getPath().split("/")[2]));
        dispatcher.addEndpoint("/user-agent", HttpMethod.GET, request -> HttpResponse.withTextBody(request.getHeaders().get("User-Agent")));
        dispatcher.addEndpoint("/files", HttpMethod.GET, request -> {
            try {
                return HttpResponse.fileResponse(fileHelper.readFile(request.getPath().split("/")[2]));
            } catch (FileNotFoundException e) {
                return HttpResponse.NOT_FOUND_RESPONSE;
            }
        });
        dispatcher.addEndpoint("/files", HttpMethod.POST, request -> {
            fileHelper.writeToFile(request.getPath().split("/")[2], request.getBody());
            return HttpResponse.CREATED_RESONSE;
        });
    }

    private Map<String, String> parseArgs(String[] args) {
        var map = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                map.put(args[i].substring(2), args[i + 1]);
            }
        }
        return map;
    }

}
