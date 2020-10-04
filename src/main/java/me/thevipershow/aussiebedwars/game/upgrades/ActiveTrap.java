package me.thevipershow.aussiebedwars.game.upgrades;

import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtils;
import org.bukkit.entity.Player;

public abstract class ActiveTrap {

    protected final BedwarsTeam owner;
    protected final TrapType trapType;
    protected final ActiveGame activeGame;
    protected long activated = -1L;

    public ActiveTrap(final BedwarsTeam owner, final TrapType trapType, ActiveGame activeGame) {
        this.owner = owner;
        this.trapType = trapType;
        this.activeGame = activeGame;
    }

    public abstract void trigger(final Player player);

    public void alertTrapOwners() {
        for (final Player player : activeGame.getTeamPlayers(owner)) {
            if (player.isOnline() && !activeGame.isOutOfGame(player)) {
                player.sendTitle("", "§eYour §a§l" + GameUtils.beautifyCaps(trapType.name()) + " §r§etrap has been activated!");
            }
        }
    }

    public long getActivated() {
        return activated;
    }

    public BedwarsTeam getOwner() {
        return owner;
    }

    public TrapType getTrapType() {
        return trapType;
    }

    public ActiveGame getActiveGame() {
        return activeGame;
    }

    public void setActivated(long activated) {
        this.activated = activated;
    }
}
