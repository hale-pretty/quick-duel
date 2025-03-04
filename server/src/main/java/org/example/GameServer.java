package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class GameServer {

    private final Queue<SocketChannel> waitingPlayers;
    private final Map<String, GameRoom> gameRoomMap;

    public GameServer() {
        this.waitingPlayers = new ArrayDeque<>();
        this.gameRoomMap = new HashMap<>();
    }

    public void startServer(int port) throws IOException {
        try (Selector selector = Selector.open(); ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Game Server started on port " + port);

            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isAcceptable()) {
                        acceptClient(serverChannel, selector);
                    } else if (key.isReadable()) {
                        try {
                            String msg = readData(key);
                            if (msg != null) {
                                // msg: move + roomId + playerId
                                handleClientMove(msg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    private void handleClientMove(String msg) {
        String roomId = GameRoom.getRoomId(msg);
        String playerId = GameRoom.getPlayerId(msg);
        String move = GameRoom.getMove(msg);
        if (gameRoomMap.containsKey(roomId)) {
            gameRoomMap.get(roomId).acceptPlayerMove(move, playerId);
        }
    }

    private void acceptClient(ServerSocketChannel serverChannel, Selector selector) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        waitingPlayers.add(clientChannel);
        System.out.println("New player connected: " + clientChannel.getRemoteAddress());
        createRoom();
    }

    private void createRoom() {
        while (this.waitingPlayers.size() > 1) {
            Player player1 = new Player(waitingPlayers.poll());
            Player player2 = new Player(waitingPlayers.poll());
            GameRoom newRoom = new GameRoom(player1, player2);
            gameRoomMap.put(newRoom.getId(), newRoom);
            System.out.println("New room created: " + newRoom.getId());
            newRoom.start();
        }
    }

    private String readData(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            clientChannel.close();
            return null;
        }
        String message = new String(buffer.array(), 0, bytesRead).trim();
        System.out.println("Received: " + message);
        return message;
    }
}
