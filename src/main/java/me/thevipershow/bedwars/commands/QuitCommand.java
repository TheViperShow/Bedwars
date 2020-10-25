package me.thevipershow.bedwars.commands;

import java.util.Optional;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class QuitCommand extends SubCommand {
    public QuitCommand(GameManager gameManager, Plugin plugin, String[] args) {
        super(gameManager, plugin, args);
    }

    @Override
    public final void run(final CommandSender sender) {
        if (!sender.hasPermission(AllStrings.PERMISSION_USER_QUIT.get())) {
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
            sender.sendMessage(Bedwars.PREFIX + AllStrings.NOT_IN_QUEUE.get());
        } else {
            activeGame.get().removePlayer((Player) sender);
            activeGame.get().moveToLobby((Player) sender);
            sender.sendMessage(Bedwars.PREFIX + AllStrings.LEFT_QUEUE.get());
        }
    }
}
