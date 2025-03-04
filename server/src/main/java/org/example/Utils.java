package org.example;

import org.apache.commons.text.RandomStringGenerator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Utils {
    public static String randomId() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .get();
        return generator.generate(20);
    }

    public static void sendResponse(String message, SocketChannel clientChannel) {
        try {
            clientChannel.write(ByteBuffer.wrap(message.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
