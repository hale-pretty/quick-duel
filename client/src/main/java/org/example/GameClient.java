package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class GameClient {
    private String roomId;
    private String playerId;
    public static final String REDO = "REDO";
    public static final String END_ROUND = "END_ROUND";
    public static final String END_GAME = "END_GAME";

    public void startGame() throws IOException {
        try (SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 5000))) {
            System.out.println("Connected to the game server");

            String startMsg = readData(client);
            this.roomId = getRoomId(startMsg);
            this.playerId = getPlayerId(startMsg);
            System.out.println("START YOUR GAME NOW!!!");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                roundPlay(client, scanner);
                while (true) {
                    String resMsg = readData(client);
                    String responseType = getResponseType(resMsg);
                    System.out.println(resMsg);
                    if (responseType.equals(REDO) || responseType.equals(END_ROUND)) {
                        break;
                    } else if (responseType.equals(END_GAME)) {
                        return;
                    }
                }
            }
        }
    }

    private String readData(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            clientChannel.close();
            return null;
        }

        return new String(buffer.array(), 0, bytesRead).trim();
    }

    // action message client: START ROOM_ID PLAYER_ID

    private String getWordFromMessage(String clientMessage, int idx, String errorMessage) {
        String[] parts = clientMessage.split(" ");
        if (parts.length >= idx + 1) {
            return parts[idx];
        }
        throw new IllegalArgumentException(errorMessage);
    }

    private String getRoomId(String clientMessage) {
        return getWordFromMessage(clientMessage, 1, "Cannot find room id");
    }

    private String getPlayerId(String clientMessage) {
        return getWordFromMessage(clientMessage, 2, "Cannot find player id");
    }

    private String getResponseType(String clientMessage) {
        return getWordFromMessage(clientMessage, 0, "Cannot find response type");
    }

    private void roundPlay(SocketChannel client, Scanner scanner) throws IOException {
        System.out.print("Enter move (ATTACK, DEFEND, SPECIAL_ATTACK): ");
        String move = scanner.nextLine();
        String message = String.join(" ", move, roomId, playerId);

        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        client.write(buffer);

        buffer.clear();
    }

}

