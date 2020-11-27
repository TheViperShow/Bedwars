package me.thevipershow.bedwars.game.managers;

import me.thevipershow.bedwars.game.ActiveGame;

public abstract class AbstractGameManager {

    protected final ActiveGame activeGame;

    public AbstractGameManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    public final ActiveGame getActiveGame() {
        return activeGame;
    }
}
