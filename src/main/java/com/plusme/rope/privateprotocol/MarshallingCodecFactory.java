package com.plusme.rope.privateprotocol;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.*;

/**
 * @author plusme
 * @create 2019-12-01 21:37
 */
public class MarshallingCodecFactory {
    public static void main(String[] args) {
        System.err.println(buildMarshallingDecoder());
    }
    public static MarshallingDecoder buildMarshallingDecoder() {
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024);
        return decoder;
    }


    public static MarshallingEncoder buildMarshallingEncoder() {
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        MarshallingEncoder encoder = new MarshallingEncoder(provider);
        return encoder;
    }
}
