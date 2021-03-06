package me.thevipershow.bedwars.commands;

import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.commands.tasks.VillageQueueRemover;
import me.thevipershow.bedwars.game.managers.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class RemoveCommand extends SubCommand {
    public RemoveCommand(GameManager gameManager, Plugin plugin, String[] args) {
        super(gameManager, plugin, args);
    }

    @Override
    public void run(CommandSender sender) {
        if (!sender.hasPermission(AllStrings.PERMISSION_ADMIN_REMOVE.get())) {
            missingPerm(sender);
            return;
        }
        if (!(sender instanceof Player)) {
            illegalExecutor(sender);
            return;
        }
        if (args.length != 1) {
            wrongArgsNumber(sender);
            return;
        }
        new VillageQueueRemover((Player) sender).perform();
    }
}
