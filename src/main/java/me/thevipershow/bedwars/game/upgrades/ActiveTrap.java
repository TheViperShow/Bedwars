package me.thevipershow.bedwars.game.upgrades;

import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import org.bukkit.Sound;
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

    @SuppressWarnings("deprecation")
    public void alertTrapOwners() {
        for (final Player player : activeGame.getTeamPlayers(owner)) {
            if (player.isOnline() && !activeGame.isOutOfGame(player)) {
                player.sendTitle("", AllStrings.YOUR.get() + GameUtils.beautifyCaps(trapType.name()) + AllStrings.TRAP_ACTIVATED.get());
                player.playSound(player.getLocation(), Sound.CLICK, 9.0f, 0.85f);
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
