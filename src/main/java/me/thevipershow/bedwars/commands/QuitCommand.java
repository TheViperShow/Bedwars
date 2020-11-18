package me.thevipershow.bedwars.commands;

import java.util.Collection;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveGameState;
import me.thevipershow.bedwars.game.managers.GameManager;
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

        final Collection<ActiveGame> activeGame = super.gameManager.getWorldsManager().getActiveGameList()
                .stream()
                .filter(game ->
                        game.getGameState() == ActiveGameState.QUEUE &&
                                game.getGameLobbyTicker().getAssociatedQueue().getInQueue()
                                        .stream()
                                        .anyMatch(p -> p.getUniqueId().equals(((Player) sender).getUniqueId())))
                .collect(Collectors.toList());

        if (!activeGame.isEmpty()) {
            sender.sendMessage(Bedwars.PREFIX + AllStrings.NOT_IN_QUEUE.get());
        } else {
            if (activeGame.size() != 1) {
                throw new RuntimeException(String.format("Player %s was found to be in more than 1 queue at the same time.", sender.getName()));
            } else {
                activeGame.forEach(game -> {
                    game.getGameLobbyTicker().getAssociatedQueue().removeFromQueue((Player) sender);
                    game.getMovementsManager().moveToSpawn((Player) sender);
                });
            }
            //activeGame.get().getTeamManager().removePlayer((Player) sender);
            //activeGame.get().getMovementsManager().moveToSpawn((Player) sender);
            sender.sendMessage(Bedwars.PREFIX + AllStrings.LEFT_QUEUE.get());
        }
    }
}
