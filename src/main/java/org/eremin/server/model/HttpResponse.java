package org.eremin.server.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponse {
    String version = "HTTP/1.1";
    HttpStatus status;
    ContentType contentType;
    Integer contentLength;
    String body;

    public static final HttpResponse OK_RESPONSE = new HttpResponse().setStatus(HttpStatus.OK);
    public static final HttpResponse CREATED_RESONSE = new HttpResponse().setStatus(HttpStatus.CREATED);
    public static final HttpResponse NOT_FOUND_RESPONSE = new HttpResponse().setStatus(HttpStatus.NOT_FOUND);
    public static final HttpResponse METHOD_NOT_ALLOWED_RESPONSE = new HttpResponse().setStatus(HttpStatus.METHOD_NOT_ALLOWED);

    public static HttpResponse withTextBody(String body) {
        return new HttpResponse()
                .setStatus(HttpStatus.OK)
                .setContentType(ContentType.TEXT_PLAIN)
                .setContentLength(body.length())
                .setBody(body);
    }

    public static HttpResponse fileResponse(String body) {
        return new HttpResponse()
                .setStatus(HttpStatus.OK)
                .setContentType(ContentType.FILE)
                .setContentLength(body.length())
                .setBody(body);
    }

    public byte[] toByteArray() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        return version + " " + status + "\r\n" +
                (contentType != null ? "Content-Type: " + contentType.getType() + "\r\n" : "") +
                (contentLength != null ? "Content-Length: " + contentLength + "\r\n" : "") +
                "\r\n" +
                (body != null ? body : "");
    }
}
