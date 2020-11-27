package me.thevipershow.bedwars.game.managers;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;

public final class MessageManager extends AbstractGameManager {

    public MessageManager(ActiveGame activeGame) {
        super(activeGame);
    }

    public enum MessageType {
        INFO, WARNING, GAME;
    }

    public static void message(MessageType type, BedwarsPlayer bedwarsPlayer, String text) {

    }
}
