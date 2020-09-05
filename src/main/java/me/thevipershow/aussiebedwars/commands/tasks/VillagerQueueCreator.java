package me.thevipershow.aussiebedwars.commands.tasks;

import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.storage.sql.SQLiteDatabase;
import me.thevipershow.aussiebedwars.storage.sql.queue.QueueTableUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class VillagerQueueCreator extends AbstractTargetCreator<Player, Villager, PlayerVillagerLookupResult> {

    private final Gamemode gamemode;

    public VillagerQueueCreator(Player interested, Gamemode gamemode) {
        super(interested, new PlayerVillagerLookupResult(interested));
        this.gamemode = gamemode;
    }

    @Override
    public void create() {
        if (lookupResult.getLookupResult().isPresent()) {
            SQLiteDatabase.getConnection().ifPresent(c -> QueueTableUtils.addVillager(c, lookupResult.getLookupResult().get(), gamemode));
            interested.sendMessage(AussieBedwars.PREFIX + String.format("§eYou successfully setup a %s bedwars queue villager", gamemode.name().toLowerCase()));
        } else {
            interested.sendMessage(AussieBedwars.PREFIX + "§eYou were not looking at villagers.");
        }
    }

    public Gamemode getGamemode() {
        return gamemode;
    }
}
