package me.thevipershow.bedwars.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.game.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import static me.thevipershow.bedwars.game.GameUtils.color;

public final class BedwarsMainCommand implements CommandExecutor, TabExecutor {
    private final Plugin plugin;
    private final GameManager gameManager;

    private static final List<String> immutableBaseArgs = Collections.unmodifiableList(Arrays.asList(
            AllStrings.SET.get(),
            AllStrings.REMOVE.get(),
            AllStrings.JOIN.get(),
            AllStrings.QUIT.get(),
            AllStrings.EXP.get()
    ));

    private static final List<String> immutableGamemodesArgs = Collections.unmodifiableList(Arrays.asList(Gamemode.values()).stream().map(s -> s.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList()));

    public BedwarsMainCommand(Plugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public final void sendHelp(final CommandSender sender) {
        sender.sendMessage(color("&e&lBedwars' Help Page&7:")); // │ ├
        sender.sendMessage(color("&7  │"));
        sender.sendMessage(color("&7  ├─ &8[&ebedwars join &7<&6gamemode&7>&8]"));
        sender.sendMessage(color("&7  │  &f&oUsed to join a specific gamemode."));
        sender.sendMessage(color("&7  │"));
        sender.sendMessage(color("&7  ├─ &8[&ebedwars set &7<&6gamemode&7>&8]"));
        sender.sendMessage(color("&7  │  &f&oUsed by admins to setup a queue villager."));
        sender.sendMessage(color("&7  │"));
        sender.sendMessage(color("&7  ├─ &8[&ebedwars quit&8]"));
        sender.sendMessage(color("&7  │  &f&oUsed to quit your current game."));
        sender.sendMessage(color("&7  │"));
        sender.sendMessage(color("&7  ├─ &8[&ebedwars exp &8]"));
        sender.sendMessage(color("&7  │  &f&oUsed to view your current experience."));
        sender.sendMessage(color("&7  │"));
        sender.sendMessage(color("&7  ├─ &8[&ebedwars exp view &7<&6player&7>&8]"));
        sender.sendMessage(color("&7  │  &f&oUsed to view someone's experience."));
        sender.sendMessage(color("&7  │"));
        sender.sendMessage(color("&7  ├─ &8[&ebedwars exp add &7<&6player&7> &7<&6exp&7>&8]"));
        sender.sendMessage(color("&7  │  &f&oUsed to add experience to a player."));
    }

    public final void unknownArg(final CommandSender sender, final String arg) {
        sender.sendMessage(Bedwars.PREFIX + AllStrings.UNKNOWN_ARG.get() + arg + "\"");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        final Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
        } else {
            final String firstArg = args[0].toLowerCase(Locale.ROOT);

            if (AllStrings.SET.get().equals(firstArg)) {
                new SetCommand(this.gameManager, this.plugin, args).run(sender);
            } else if (AllStrings.REMOVE.get().equals(firstArg)) {
                new RemoveCommand(this.gameManager, this.plugin, args).run(sender);
            } else if (AllStrings.JOIN.get().equals(firstArg)) {
                new JoinCommand(this.gameManager, this.plugin, args).run(sender);
            } else if (AllStrings.QUIT.get().equals(firstArg)) {
                new QuitCommand(this.gameManager, this.plugin, args).run(sender);
            } else if (AllStrings.EXP.get().equals(firstArg)) {
                new ExpCommand(this.gameManager, this.plugin, args).run(sender);
            } else {
                unknownArg(sender, firstArg);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return immutableBaseArgs;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("set"))  {
                return immutableGamemodesArgs;
            }
        }
        return null;
    }
}
