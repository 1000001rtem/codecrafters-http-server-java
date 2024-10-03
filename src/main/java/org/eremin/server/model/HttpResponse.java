package org.eremin.server.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponse {
    private String version = "HTTP/1.1";
    private HttpStatus status;
    private Map<String, String> headers = new HashMap<>();
    private Object body;

    public static final HttpResponse OK_RESPONSE = new HttpResponse().setStatus(HttpStatus.OK);
    public static final HttpResponse CREATED_RESPONSE = new HttpResponse().setStatus(HttpStatus.CREATED);
    public static final HttpResponse NOT_FOUND_RESPONSE = new HttpResponse().setStatus(HttpStatus.NOT_FOUND);
    public static final HttpResponse METHOD_NOT_ALLOWED_RESPONSE = new HttpResponse().setStatus(HttpStatus.METHOD_NOT_ALLOWED);

    public static HttpResponse withTextBody(String body) {
        return new HttpResponse()
                .setStatus(HttpStatus.OK)
                .addHeader("Content-Type", ContentType.TEXT_PLAIN.getType())
                .addHeader("Content-Length", String.valueOf(body.length()))
                .setBody(body);
    }

    public static HttpResponse fileResponse(String body) {
        return new HttpResponse()
                .setStatus(HttpStatus.OK)
                .addHeader("Content-Type", ContentType.FILE.getType())
                .addHeader("Content-Length", String.valueOf(body.length()))
                .setBody(body);
    }

    public HttpResponse addHeader(String key, String value) {
        if (key != null && value != null) {
            this.headers.put(key, value);
        }
        return this;
    }

    public byte[] toByteArray() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        return version + " " + status + "\r\n" +
                headers.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining("\r\n")) +
                "\r\n\r\n";
    }
}
