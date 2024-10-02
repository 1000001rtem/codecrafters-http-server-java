import lombok.Data;
import lombok.experimental.Accessors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4221)) {

            serverSocket.setReuseAddress(true);

            try (var clientSocket = serverSocket.accept()) {
                var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                var writer = clientSocket.getOutputStream();

                var request = buildRequest(reader);
                var response = getResponse(request);

                writer.write(response.getBytes());
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static String getResponse(HttpRequest request) {
        if(request.getPath().equals("/")) {
           return "HTTP/1.1 200 OK\r\n\r\n";
        } else {
            return "HTTP/1.1 404 Not Found\r\n\r\n";
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

@Data
@Accessors(chain = true)
class HttpRequest {
    String method;
    String path;
    String version;
    String host;
    Map<String, String> headers = new HashMap<>();
}
