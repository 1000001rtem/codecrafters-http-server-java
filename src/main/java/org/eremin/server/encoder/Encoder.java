package org.eremin.server.encoder;

import org.eremin.server.model.HttpResponse;

public interface Encoder {

    EncodingType getType();

    HttpResponse encode(HttpResponse response);
}
