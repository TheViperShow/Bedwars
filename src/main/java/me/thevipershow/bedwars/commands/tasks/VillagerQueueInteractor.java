package me.thevipershow.bedwars.commands.tasks;

import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.storage.sql.MySQLDatabase;
import me.thevipershow.bedwars.storage.sql.tables.QueueTableUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class VillagerQueueInteractor extends AbstractTargetInteractor<Player, Villager, PlayerVillagerLookupResult> {

    private final Gamemode gamemode;

    public VillagerQueueInteractor(Player interested, Gamemode gamemode) {
        super(interested, new PlayerVillagerLookupResult(interested));
        this.gamemode = gamemode;
    }

    @Override
    public void perform() {
        if (lookupResult.getLookupResult().isPresent()) {
            MySQLDatabase.getConnection().ifPresent(c -> QueueTableUtils.addVillager(c, lookupResult.getLookupResult().get(), gamemode));
            interested.sendMessage(Bedwars.PREFIX + String.format("§eYou successfully setup a %s bedwars queue villager", gamemode.name().toLowerCase()));
        } else {
            interested.sendMessage(Bedwars.PREFIX + "§eYou were not looking at villagers.");
        }
    }

    public Gamemode getGamemode() {
        return gamemode;
    }
}
