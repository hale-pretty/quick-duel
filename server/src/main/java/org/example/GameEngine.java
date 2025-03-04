package org.example;

public class GameEngine {

    private final Player player1;
    private final Player player2;
    private int round;

    public GameEngine(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.round = 1;
    }

    public void play(String player1Move, String player2Move) {
        String RESULT_RESOLUTION_MESSAGE = ResponseType.END_ROUND.name() + " > Result: You %s and opponent %s.\n> Your HP is now %d.\n> Opponent HP is now %d.";
        String GAME_WIN_MESSAGE = ResponseType.END_GAME.name() + " > Duel ends: Your HP: %d | Opponent HP: %d\n" +
                "> You WIN!";
        String GAME_LOSE_MESSAGE = ResponseType.END_GAME.name() + " > Duel ends: Your HP: %d | Opponent HP: %d\n" +
                "> You LOSE!";
        String GAME_DRAW_MESSAGE = ResponseType.END_GAME.name() + " > Duel ends: Your HP: %d | Opponent HP: %d\n" +
                "> You DRAW!";

        if (player1Move.equals(GameRoom.SPECIAL_ATTACK) || player2Move.equals(GameRoom.SPECIAL_ATTACK)) {
            roundPlayWithSpecialAttack(player1Move, player2Move);
        }
        else {
            roundPlayNorm(player1Move, player2Move);
        }

        // End if out of hp
        if (player1.getHp() <= 0 && player2.getHp() <= 0) {
            System.out.println("Draw");
            player1.sendMessage(String.format(GAME_DRAW_MESSAGE, player1.getHp(), player2.getHp()));
            player2.sendMessage(String.format(GAME_DRAW_MESSAGE, player2.getHp(), player1.getHp()));
            return;
        }
        if (player2.getHp() <= 0) {
            System.out.println("Winner: Player1");
            player1.sendMessage(String.format(GAME_WIN_MESSAGE, player1.getHp(), player2.getHp()));
            player2.sendMessage(String.format(GAME_LOSE_MESSAGE, player2.getHp(), player1.getHp()));
            return;
        }
        if (player1.getHp() <= 0) {
            System.out.println("Winner: Player2");
            player2.sendMessage(String.format(GAME_WIN_MESSAGE, player2.getHp(), player1.getHp()));
            player1.sendMessage(String.format(GAME_LOSE_MESSAGE, player1.getHp(), player2.getHp()));
            return;
        }

        // End if out of rounds
        if (round == 3) {
            if (player1.getHp() > player2.getHp()) {
                System.out.println("Winner: Player1" + player1.getId());
                player1.sendMessage(String.format(GAME_WIN_MESSAGE, player1.getHp(), player2.getHp()));
                player2.sendMessage(String.format(GAME_LOSE_MESSAGE, player2.getHp(), player1.getHp()));
            } else if (player1.getHp() < player2.getHp()) {
                System.out.println("Winner: Player2" + player2.getId());
                player2.sendMessage(String.format(GAME_WIN_MESSAGE, player2.getHp(), player1.getHp()));
                player1.sendMessage(String.format(GAME_LOSE_MESSAGE, player1.getHp(), player2.getHp()));
            } else {
                System.out.println("Draw");
                player1.sendMessage(String.format(GAME_DRAW_MESSAGE, player1.getHp(), player2.getHp()));
                player2.sendMessage(String.format(GAME_DRAW_MESSAGE, player2.getHp(), player1.getHp()));
            }
            return;
        }
        round += 1;
        player1.sendMessage(String.format(RESULT_RESOLUTION_MESSAGE, player1Move, player2Move, player1.getHp(), player2.getHp()));
        player2.sendMessage(String.format(RESULT_RESOLUTION_MESSAGE, player2Move, player1Move, player2.getHp(), player1.getHp()));
    }

    private void roundPlayNorm(String player1Move, String player2Move) {
        if (player1Move.equals("ATTACK") && player2Move.equals("ATTACK")) {
            bothAttack();
        } else if (player1Move.equals("ATTACK") && player2Move.equals("DEFEND")) {
            oneAttackOneDefend(player1.getId());
        } else if (player2Move.equals("ATTACK") && player1Move.equals("DEFEND")) {
            oneAttackOneDefend(player2.getId());
        }
    }

    private void roundPlayWithSpecialAttack(String player1Move, String player2Move) {
        if (player1Move.equals(GameRoom.SPECIAL_ATTACK)) {
            player2.setHp(player2.getHp() - 15);
            player1.setSpecialAttack(true);
        }
        if (player2Move.equals(GameRoom.SPECIAL_ATTACK)) {
            player1.setHp(player1.getHp() - 15);
            player2.setSpecialAttack(true);
        }
        if (player1Move.equals("ATTACK")) {
            player2.setHp(player2.getHp() - 10);
        }
        if (player2Move.equals("ATTACK")) {
            player1.setHp(player1.getHp() - 10);
        }
    }

    private void bothAttack() {
        player1.setHp(player1.getHp() - 10);
        player2.setHp(player2.getHp() - 10);
    }

    private void oneAttackOneDefend(String attackerId) {
        if (player1.getId().equals(attackerId)) {
            player2.setHp(player2.getHp() - 5);
        } else if (player2.getId().equals(attackerId)) {
            player1.setHp(player1.getHp() - 5);
        }
    }

    public int getRound() {
        return round;
    }
}
