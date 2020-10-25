package me.thevipershow.bedwars.commands;

import java.util.Optional;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameManager;
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
                player.sendMessage(Bedwars.PREFIX + String.format(AllStrings.NO_GAME_FOUND_FOR_GAMEMODE.get(), gamemode.name()));
                super.gameManager.loadRandom(gamemode);
            }

        } else {
            player.sendMessage(Bedwars.PREFIX + AllStrings.GAME_ALREADY_JOINED.get());
        }
    }

    @Override
    public void run(CommandSender sender) {
        if (!sender.hasPermission(AllStrings.PERMISSION_USER_JOIN.get())) {
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

        sender.sendMessage(Bedwars.PREFIX + AllStrings.INVALID_GAMEMODE.get() + args[1] + "\"");
    }
}
