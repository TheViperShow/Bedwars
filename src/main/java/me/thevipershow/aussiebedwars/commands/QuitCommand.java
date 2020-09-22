package me.thevipershow.aussiebedwars.commands;

import java.util.Optional;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class QuitCommand extends SubCommand {
    public QuitCommand(GameManager gameManager, Plugin plugin, String[] args) {
        super(gameManager, plugin, args);
    }

    @Override
    public void run(CommandSender sender) {
        if (!sender.hasPermission("abedwars.users.quit")) {
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

        final Optional<ActiveGame> activeGame = super.gameManager.getWorldsManager().getActiveGameList()
                .stream()
                .filter(game -> !game.isHasStarted() && game.getAssociatedQueue().getInQueue().stream().anyMatch(p -> p.equals(sender)))
                .findFirst();

        if (!activeGame.isPresent()) {
            sender.sendMessage(AussieBedwars.PREFIX + "You are not in any queue.");
        } else {
            activeGame.get().removePlayer((Player) sender);
            activeGame.get().moveToLobby((Player) sender);
            sender.sendMessage(AussieBedwars.PREFIX + "You left the queue.");
        }
    }
}
