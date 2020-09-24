package me.thevipershow.aussiebedwars.commands;

import java.util.Optional;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class JoinCommand extends SubCommand {
    public JoinCommand(GameManager gameManager, Plugin plugin, String[] args) {
        super(gameManager, plugin, args);
    }

    private void joinGamemode(final Player player, final Gamemode gamemode) {
        if (super.gameManager.getWorldsManager().getActiveGameList()
                .stream()
                .flatMap(game -> game.getAssociatedQueue().getInQueue().stream())
                .noneMatch(p -> p.equals(player)))
        {
            final Optional<ActiveGame> opt = gameManager.findOptimalGame(gamemode);
            if (opt.isPresent()) {
                final ActiveGame found = opt.get();
                super.gameManager.addToQueue(player, found);
            } else {
                player.sendMessage(AussieBedwars.PREFIX + String.format("No game could be found for %s gamemode.", gamemode.name()));
                super.gameManager.loadRandom(gamemode);
            }

        } else {
            player.sendMessage(AussieBedwars.PREFIX + "You have already joined a game!");
        }
    }

    @Override
    public void run(CommandSender sender) {
        if (!sender.hasPermission("abedwars.users.join")) {
            missingPerm(sender);
            return;
        }
        if (!(sender instanceof Player)) {
            illegalExecutor(sender);
            return;
        }
        if (args.length != 2) {
            wrongArgsNumber(sender);
            return;
        }

        for (final Gamemode gamemode : Gamemode.values()) {
            if (gamemode.name().equalsIgnoreCase(args[1])) {
                joinGamemode((Player) sender, gamemode);
                return;
            }
        }

        sender.sendMessage(AussieBedwars.PREFIX + "Unknown gamemode \"" + args[1] + "\"");
    }
}
