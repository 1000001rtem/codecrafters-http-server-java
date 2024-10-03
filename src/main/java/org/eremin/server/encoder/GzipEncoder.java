package org.eremin.server.encoder;

import org.eremin.server.model.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class GzipEncoder implements Encoder {

    @Override
    public EncodingType getType() {
        return EncodingType.GZIP;
    }

    @Override
    public HttpResponse encode(HttpResponse response) {
        try (var out = new ByteArrayOutputStream()) {
            try (var gzip = new GZIPOutputStream(out)) {
                gzip.write(convertToBytes(response.getBody()));
                gzip.finish();

                var encodedBody = out.toByteArray();

                return response.setBody(encodedBody)
                        .addHeader("Content-Encoding", EncodingType.GZIP.getType())
                        .addHeader("Content-Length", String.valueOf(encodedBody.length));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] convertToBytes(Object object) {
        try {
            var s = (String) object;
            return s.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Unsupported body type");
            throw new RuntimeException(e);
        }
    }
}
