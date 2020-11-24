package me.thevipershow.bedwars.game.upgrades.traps;

import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import org.bukkit.Sound;

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

    public abstract void trigger(final BedwarsPlayer player);

    @SuppressWarnings("deprecation")
    public void alertTrapOwners() {
        for (Map.Entry<BedwarsTeam, ? extends TeamData<?>> entry : activeGame.getTeamManager().getDataMap().entrySet()) {
            entry.getValue().perform(bedwarsPlayer -> {
                bedwarsPlayer.playSound(Sound.CLICK, 9.0f, 0.8f);
                bedwarsPlayer.sendTitle("", AllStrings.YOUR.get() + GameUtils.beautifyCaps(trapType.name()) + AllStrings.TRAP_ACTIVATED.get());
            });
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
