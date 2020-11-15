package me.thevipershow.bedwars.game.objects;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.upgrades.ActiveTrap;

public final class TrapsManager {

    private final ActiveGame activeGame;

    public TrapsManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private final Map<BedwarsTeam, LinkedList<ActiveTrap>> activeTraps = new EnumMap<>(BedwarsTeam.class);

    private final Map<BedwarsTeam, Long> trapsActivationTime = new EnumMap<>(BedwarsTeam.class);

    /*---------------------------------------------------------------------------------------------------------*/

    public final Map<BedwarsTeam, LinkedList<ActiveTrap>> getActiveTraps() {
        return activeTraps;
    }

    public final Map<BedwarsTeam, Long> getTrapsActivationTime() {
        return trapsActivationTime;
    }

    public final void fillTraps() {
        for (BedwarsTeam bedwarsTeam : activeGame.getTeamManager().getDataMap().keySet()) {
            activeTraps.put(bedwarsTeam, new LinkedList<>());
        }
    }

    public final void fillTrapsDelay() {
        final long currentTime = System.currentTimeMillis();
        for (BedwarsTeam bedwarsTeam : activeGame.getTeamManager().getDataMap().keySet()) {
            trapsActivationTime.put(bedwarsTeam, currentTime);
        }
    }
}
