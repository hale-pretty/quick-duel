package org.example;

import java.util.HashMap;
import java.util.Map;

public class GameRoom {
    public static final String SPECIAL_ATTACK = "SPECIAL_ATTACK";
    private final String id;
    private final Player player1;
    private final Player player2;
    private Map<Integer, String> gameState;
    private GameEngine gameEngine;

    public GameRoom(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.id = Utils.randomId();
        this.gameState = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void start() {
        String START_GAME_ID = ResponseType.START.name() + " %s %s";
        player1.sendMessage(String.format(START_GAME_ID, id, player1.getId()));
        player2.sendMessage(String.format(START_GAME_ID, id, player2.getId()));
        gameEngine = new GameEngine(player1, player2);
    }

    public void acceptPlayerMove(String move, String playerId) {
        String CHOOSE_MOVE_MESSAGE = ResponseType.INFO.name() + " > You choose %s";
        String CHOOSE_MOVE_AGAIN_MESSAGE = ResponseType.REDO.name() + " > %s is invalid. \nChoose your move again.";

        if (move.equals(SPECIAL_ATTACK) && playerId.equals(player1.getId()) && player1.doneSpecialAttack()){
            player1.sendMessage(String.format(CHOOSE_MOVE_AGAIN_MESSAGE, SPECIAL_ATTACK));
            return;
        }
        if (move.equals(SPECIAL_ATTACK) && playerId.equals(player2.getId()) && player2.doneSpecialAttack()) {
            player2.sendMessage(String.format(CHOOSE_MOVE_AGAIN_MESSAGE, SPECIAL_ATTACK));
            return;
        }
        if (playerId.equals(player1.getId())) {
            gameState.put(1, move);
            player1.sendMessage(String.format(CHOOSE_MOVE_MESSAGE, move));
        }
        if (playerId.equals(player2.getId())) {
            gameState.put(2, move);
            player2.sendMessage(String.format(CHOOSE_MOVE_MESSAGE, move));
        }
        if (gameState.size() == 2 && gameEngine.getRound() <= 3) {
            gameEngine.play(gameState.get(1), gameState.get(2));
            gameState = new HashMap<Integer, String>();
        }
    }

    // action message client: ACTION ROOM_ID PLAYER_ID

    private static String getIdFromMessage(String clientMessage, int idIdx, String errorMessage) {
        String[] parts = clientMessage.split(" ");
        if (parts.length >= idIdx + 1) {
            return parts[idIdx];
        }
        throw new IllegalArgumentException(errorMessage);
    }

    public static String getRoomId(String clientMessage) {
        return getIdFromMessage(clientMessage, 1, "Cannot find room id");
    }

    public static String getPlayerId(String clientMessage) {
        return getIdFromMessage(clientMessage, 2, "Cannot find player id");
    }

    public static String getMove(String clientMessage) {
        return getIdFromMessage(clientMessage, 0, "Cannot find move");
    }
}
