package org.eremin.server;

import org.eremin.server.model.HttpRequest;
import org.eremin.server.model.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4221)) {

            serverSocket.setReuseAddress(true);

            try (var clientSocket = serverSocket.accept()) {
                var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                var writer = clientSocket.getOutputStream();

                var request = buildRequest(reader);
                var response = getResponse(request);

                writer.write(response.toByteArray());
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static HttpResponse getResponse(HttpRequest request) {
        if (request.getPath().equals("/")) {
            return HttpResponse.OK_RESPONSE;
        } else if (request.getPath().startsWith("/echo")) {
            return HttpResponse.withTextBody(request.getPath().split("/")[2]);
        } else {
            return HttpResponse.NOT_FOUND_RESPONSE;
        }
    }

    private static HttpRequest buildRequest(BufferedReader reader) {
        try {
            var request = new HttpRequest();
            var line = reader.readLine();
            var requestLine = line.split(" ");
            request.setMethod(requestLine[0])
                    .setPath(requestLine[1])
                    .setVersion(requestLine[2]);
            line = reader.readLine();
            request.setHost(line.split(": ")[1]);
            while (line != null && !line.isEmpty()) {
                var header = line.split(": ");
                request.getHeaders().put(header[0], header[1]);
                line = reader.readLine();
            }
            return request;
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

