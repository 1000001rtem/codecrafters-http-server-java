package org.eremin.server.encoder;

import lombok.Getter;

@Getter
public enum EncodingType {
    GZIP("gzip"),
    ;

    private final String type;

    EncodingType(String type) {
        this.type = type;

    }
}
