package org.example;

import java.nio.channels.SocketChannel;

public class Player {
    private final String id;
    private int hp;
    private boolean specialAttack;
    private final SocketChannel channel;

    public Player(SocketChannel channel) {
        this.channel = channel;
        this.id = Utils.randomId();
        this.hp = 30;
        this.specialAttack = false;
    }

    public String getId() {
        return id;
    }

    public boolean doneSpecialAttack() {
        return specialAttack;
    }

    public void setSpecialAttack(boolean specialAttack) {
        this.specialAttack = specialAttack;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void sendMessage(String message) {
        Utils.sendResponse(message, channel);
    }
}
