package org.eremin.server.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class HttpRequest {
    HttpMethod method;
    String path;
    String version;
    String host;
    String body;
    Map<String, String> headers = new HashMap<>();
}
