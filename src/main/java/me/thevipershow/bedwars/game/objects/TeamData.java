package me.thevipershow.bedwars.game.objects;

import java.util.Set;
import java.util.function.Consumer;
import me.thevipershow.bedwars.bedwars.Gamemode;
import org.bukkit.entity.Player;

public abstract class TeamData<T> {

    public TeamData(Gamemode gamemode, PlayerMapper playerMapper) {
        this.gamemode = gamemode;
        this.playerMapper = playerMapper;
    }

    private T data;
    private String statusCharacter;
    private TeamStatus status = TeamStatus.BED_EXISTS;
    private final Gamemode gamemode;
    private final PlayerMapper playerMapper;

    public abstract void add(Player player);

    public abstract void perform(Consumer<? super BedwarsPlayer> consumer);

    public abstract Set<BedwarsPlayer> getAll();

    public abstract void updateStatusCharacter();

    public final TeamStatus getStatus() {
        return status;
    }

    public final void setData(T data) {
        this.data = data;
    }

    public final void setStatus(TeamStatus status) {
        this.status = status;
    }

    public final T getData() {
        return data;
    }

    public final String getStatusCharacter() {
        return statusCharacter;
    }

    public final void setStatusCharacter(String statusCharacter) {
        this.statusCharacter = statusCharacter;
    }

    public final Gamemode getGamemode() {
        return gamemode;
    }

    public final PlayerMapper getPlayerMapper() {
        return playerMapper;
    }
}
