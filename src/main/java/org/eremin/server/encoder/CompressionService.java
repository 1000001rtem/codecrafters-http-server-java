package org.eremin.server.encoder;

import lombok.RequiredArgsConstructor;
import org.eremin.server.model.HttpResponse;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor
public class CompressionService {

    private final Map<EncodingType, Encoder> encoders;

    public HttpResponse encode(HttpResponse response, String[] compressionTypes) {
        if (response.getBody() != null) {
            for (String ct : compressionTypes) {
                EncodingType encodingType = Arrays.stream(EncodingType.values()).filter(it -> it.getType().equals(ct)).findAny().orElse(null);
                var encoder = encoders.get(encodingType);
                if (encoder != null) {
                    response.addHeader("Content-Encoding", encoder.getType().getType());
                    return encoder.encode(response);
                }
            }
        }
        return response;
    }
}
