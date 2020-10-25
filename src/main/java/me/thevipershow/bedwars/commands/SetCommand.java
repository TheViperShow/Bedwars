package me.thevipershow.bedwars.commands;

import java.util.Locale;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.commands.tasks.VillagerQueueInteractor;
import me.thevipershow.bedwars.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class SetCommand extends SubCommand {

    public SetCommand(GameManager gameManager, Plugin plugin, String[] args) {
        super(gameManager, plugin, args);
    }

    @Override
    public void run(CommandSender sender) {
        if (!sender.hasPermission(AllStrings.PERMISSION_ADMIN_USE.get())) {
            missingPerm(sender);
            return;
        }
        if (!(sender instanceof Player)) {
            illegalExecutor(sender);
            return;
        }
        if (args.length < 2) {
            wrongArgsNumber(sender);
            return;
        }
        final String secondArg = args[1].toUpperCase(Locale.ROOT);
        for (final Gamemode gamemode : Gamemode.values()) {
            if (gamemode.name().equals(secondArg)) {
                new VillagerQueueInteractor((Player) sender, gamemode).perform();
                return;
            }
        }
        sender.sendMessage(Bedwars.PREFIX + AllStrings.INVALID_GAMEMODE.get() + secondArg + "\"");
    }
}
