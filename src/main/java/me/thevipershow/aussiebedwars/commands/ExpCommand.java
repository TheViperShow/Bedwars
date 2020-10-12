package me.thevipershow.aussiebedwars.commands;

import java.util.concurrent.CompletableFuture;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.game.ExperienceManager;
import me.thevipershow.aussiebedwars.game.GameManager;
import me.thevipershow.aussiebedwars.storage.sql.queue.RankTableUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class ExpCommand extends SubCommand {

    public ExpCommand(final GameManager gameManager, final Plugin plugin, final String[] args) {
        super(gameManager, plugin, args);
    }

    public static void lookupSelf(final Player p, final Plugin plugin) {
        lookupLevel(RankTableUtils.getPlayerExp(p.getUniqueId(), plugin), p, p.getName(), true);
    }

    public static void lookupOther(final CommandSender sender, final String name, final Plugin plugin) {
        lookupLevel(RankTableUtils.getPlayerExp(name, plugin), sender, name, sender.getName().equals(name));

    }

    public static void lookupLevel(final CompletableFuture<Integer> expFuture, final CommandSender p, final String targetName, final boolean self) {
        expFuture.thenAccept(e -> {
            if (e <= 0) {

                if (self) {
                    p.sendMessage(AussieBedwars.PREFIX + "§7You still don't have any experience points.");
                } else {
                    p.sendMessage(AussieBedwars.PREFIX + "§7He still doesn't have any experience points.");
                }

            } else {

                final int lvl = ExperienceManager.findLevelFromExp(e);

                final int nextLevelExp = ExperienceManager.requiredExpMap.get(lvl + 1);

                if (self) {
                    p.sendMessage(AussieBedwars.PREFIX + "\n    §7Your current experience: §6§l" + e + "§8/§6§l" + nextLevelExp);
                    p.sendMessage("    §7Your current bedwars level: §6§l" + lvl);
                    p.sendMessage("    §7You need §6§l" + (nextLevelExp - e) + " EXP §7to reach §3§lLevel " + (lvl + 1));
                } else {
                    p.sendMessage(AussieBedwars.PREFIX + "\n    §7" + targetName + "'s current experience: §6§l" + e + "§8/§6§l" + nextLevelExp);
                    p.sendMessage("    §7" + targetName + "'s current bedwars level: §6§l" + lvl);
                    p.sendMessage("    §7He needs §6§l" + (nextLevelExp - e) + " EXP §7to reach §3§lLevel " + (lvl + 1));
                }
            }
        });
    }

    @Override
    public final void run(final CommandSender sender) {
        if (args.length == 1) {
            if (!sender.hasPermission("abedwars.users.exp")) {
                missingPerm(sender);
            } else {
                if (!(sender instanceof Player)) {
                    illegalExecutor(sender);
                } else {
                    final Player p = (Player) sender;
                    lookupSelf(p, gameManager.getPlugin());
                }
            }
        } else {
            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("view")) {
                    if (!sender.hasPermission("abedwars.admin.exp")) {
                        missingPerm(sender);
                    } else {

                        lookupOther(sender, args[2], plugin);
                    }
                }
            } else {
                wrongArgsNumber(sender);
            }
        }
    }

}
