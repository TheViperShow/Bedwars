package me.thevipershow.bedwars.commands;

import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public abstract class SubCommand {

    protected static void illegalExecutor(final CommandSender sender) {
        sender.sendMessage(Bedwars.PREFIX + "You cannot execute this command from here.");
    }

    protected static void missingPerm(final CommandSender sender) {
        sender.sendMessage(Bedwars.PREFIX + "You do not have enough permissions.");
    }

    protected final void wrongArgsNumber(final CommandSender sender) {
        sender.sendMessage(Bedwars.PREFIX + "Wrong number of args: " + this.args.length);
    }

    protected final GameManager gameManager;
    protected final Plugin plugin;
    protected final String args[];

    public SubCommand(final GameManager gameManager, final Plugin plugin, String args[]) {
        this.gameManager = gameManager;
        this.plugin = plugin;
        this.args = args;
    }

    public abstract void run(final CommandSender sender);
}
