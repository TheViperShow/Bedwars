package me.thevipershow.aussiebedwars.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.game.ExperienceManager;
import me.thevipershow.aussiebedwars.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class AussieBedwarsMainCommand implements CommandExecutor, TabExecutor {
    private final Plugin plugin;
    private final GameManager gameManager;

    private final List<String> immutableBaseArgs = Collections.unmodifiableList(Arrays.asList(
            "set",
            "remove",
            "join",
            "quit",
            "exp"
    ));

    public AussieBedwarsMainCommand(Plugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public final void sendHelp(final CommandSender sender) {

    }

    public final void unknownArg(final CommandSender sender, final String arg) {
        sender.sendMessage(AussieBedwars.PREFIX + "Unknown command argument \"" + arg + "\"");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        final Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
        } else {
            final String firstArg = args[0].toLowerCase(Locale.ROOT);
            switch (firstArg) {
                case "set":
                    new SetCommand(this.gameManager, this.plugin, args).run(sender);
                    break;
                case "remove":
                    new RemoveCommand(this.gameManager, this.plugin, args).run(sender);
                    break;
                case "join":
                    new JoinCommand(this.gameManager, this.plugin, args).run(sender);
                    break;
                case "quit":
                    new QuitCommand(this.gameManager, this.plugin, args).run(sender);
                    break;
                case "exp":
                    new ExpCommand(this.gameManager, this.plugin, args).run(sender);
                    break;
                default:
                    unknownArg(sender, firstArg);
                    break;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return this.immutableBaseArgs;
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "set":
                    break;
                case "remove":
                    break;
                case "join":
                    break;
                case "quit":
                    break;
                default:
                    break;
            }
        }

        return null;
    }
}
