package me.thevipershow.aussiebedwars.commands.tasks;

import java.util.Optional;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.storage.sql.MySQLDatabase;
import me.thevipershow.aussiebedwars.storage.sql.queue.QueueTableUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class VillageQueueRemover extends AbstractTargetInteractor<Player, Villager, PlayerVillagerLookupResult> {

    public VillageQueueRemover(Player interested) {
        super(interested, new PlayerVillagerLookupResult(interested));
    }

    @Override
    public void perform() {
        final Optional<Villager> villager = lookupResult.getLookupResult();
        if (villager.isPresent()) {
            MySQLDatabase.getConnection().ifPresent(connection -> {
                QueueTableUtils.removeVillager(connection, villager.get())
                        .thenAccept(bool -> {
                           if (bool != null && bool) {
                               interested.sendMessage(AussieBedwars.PREFIX + "§eYou successfully removed that villager from database.");
                           } else {
                               interested.sendMessage(AussieBedwars.PREFIX + "§eThat villager was not associated with a database.");
                           }
                        });
            });
        } else {
            interested.sendMessage(AussieBedwars.PREFIX + "§eYou were not looking at a villager.");
        }
    }
}