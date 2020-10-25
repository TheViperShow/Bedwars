package me.thevipershow.bedwars.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class BedwarsMainCommand implements CommandExecutor, TabExecutor {
    private final Plugin plugin;
    private final GameManager gameManager;

    private final List<String> immutableBaseArgs = Collections.unmodifiableList(Arrays.asList(
            AllStrings.SET.get(),
            AllStrings.REMOVE.get(),
            AllStrings.JOIN.get(),
            AllStrings.QUIT.get(),
            AllStrings.EXP.get()
    ));

    public BedwarsMainCommand(Plugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public final void sendHelp(final CommandSender sender) {

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
            return this.immutableBaseArgs;
        }

        return null;
    }
}
