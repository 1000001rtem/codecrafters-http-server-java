package org.eremin.server.model;

import lombok.Getter;

@Getter
public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    CREATED(201, "Created"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed")
    ;

    private final int code;
    private final String reason;

    HttpStatus(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return code + " " + reason;
    }
}
