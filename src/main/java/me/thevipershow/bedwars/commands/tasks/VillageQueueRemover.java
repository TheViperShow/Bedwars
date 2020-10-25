package me.thevipershow.bedwars.commands.tasks;

import java.util.Optional;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.storage.sql.MySQLDatabase;
import me.thevipershow.bedwars.storage.sql.tables.QueueTableUtils;
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
                QueueTableUtils.removeVillager(villager.get(), Bedwars.plugin)
                        .thenAccept(bool -> {
                           if (bool != null && bool) {
                               interested.sendMessage(Bedwars.PREFIX + AllStrings.REMOVED_VILLAGER_FROM_DATABASE.get());
                           } else {
                               interested.sendMessage(Bedwars.PREFIX + AllStrings.VILLAGER_NOT_PRESENT_IN_DATABASE.get());
                           }
                        });

        } else {
            interested.sendMessage(Bedwars.PREFIX + AllStrings.NOT_LOOKING_AT_VILLAGER);
        }
    }
}