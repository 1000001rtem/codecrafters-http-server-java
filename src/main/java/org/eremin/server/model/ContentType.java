package org.eremin.server.model;

import lombok.Getter;

@Getter
public enum ContentType {

    TEXT_PLAIN("text/plain"),
    FILE("application/octet-stream");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

}
