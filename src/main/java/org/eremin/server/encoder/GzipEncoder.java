package org.eremin.server.encoder;

import org.eremin.server.model.HttpResponse;

public class GzipEncoder implements Encoder {

    @Override
    public EncodingType getType() {
        return EncodingType.GZIP;
    }

    @Override
    public HttpResponse encode(HttpResponse response) {
        response.addHeader("Content-Encoding", EncodingType.GZIP.getType());
        return response;
    }
}
