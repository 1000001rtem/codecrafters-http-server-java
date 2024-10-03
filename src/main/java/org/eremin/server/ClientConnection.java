package org.eremin.server;

import lombok.RequiredArgsConstructor;
import org.eremin.server.model.HttpMethod;
import org.eremin.server.model.HttpRequest;
import org.eremin.server.model.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@RequiredArgsConstructor
public class ClientConnection implements Runnable {

    private final Socket clientSocket;
    private final Dispatcher dispatcher;

    @Override
    public void run() {
        try (clientSocket) {
            var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            var writer = clientSocket.getOutputStream();

            var request = receiveRequest(reader);
            var response = dispatcher.dispatch(request);

            writer.write(response.toByteArray());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static HttpRequest receiveRequest(BufferedReader reader) {
        try {
            var request = new HttpRequest();
            var line = reader.readLine();
            var requestLine = line.split(" ");
            request.setMethod(HttpMethod.valueOf(requestLine[0]))
                    .setPath(requestLine[1])
                    .setVersion(requestLine[2]);
            line = reader.readLine();
            request.setHost(line.split(": ")[1]);
            while (line != null && !line.isEmpty()) {
                var header = line.split(": ");
                request.getHeaders().put(header[0], header[1]);
                line = reader.readLine();
            }
            if (request.getHeaders().containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(request.getHeaders().get("Content-Length"));
                char[] body = new char[contentLength];
                reader.read(body, 0, contentLength);
                request.setBody(new String(body));
            }            return request;
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
