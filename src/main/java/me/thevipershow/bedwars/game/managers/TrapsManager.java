package me.thevipershow.bedwars.game.managers;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.upgrades.traps.ActiveTrap;

public final class TrapsManager extends AbstractGameManager {

    public TrapsManager(ActiveGame activeGame) {
        super(activeGame);
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
