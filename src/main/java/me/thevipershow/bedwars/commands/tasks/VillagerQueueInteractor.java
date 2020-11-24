package me.thevipershow.bedwars.commands.tasks;

import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.storage.sql.MySQLDatabase;
import me.thevipershow.bedwars.storage.sql.tables.QueueTableUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class VillagerQueueInteractor extends AbstractTargetInteractor<Player, Villager, PlayerVillagerLookupResult> {

    private final Gamemode gamemode;

    public VillagerQueueInteractor(final Player interested, final Gamemode gamemode) {
        super(interested, new PlayerVillagerLookupResult(interested));
        this.gamemode = gamemode;
    }

    @Override
    public void perform() {
        if (lookupResult.getLookupResult().isPresent()) {
            QueueTableUtils.addVillager(lookupResult.getLookupResult().get(), gamemode, Bedwars.plugin);
            interested.sendMessage(AllStrings.PREFIX.get() + String.format(AllStrings.SETUP_QUEUE_VILLAGER.get(), gamemode.name().toLowerCase()));
        } else {
            interested.sendMessage(AllStrings.PREFIX.get() + AllStrings.NOT_LOOKING_AT_VILLAGER.get());
        }
    }

    public Gamemode getGamemode() {
        return gamemode;
    }
}
