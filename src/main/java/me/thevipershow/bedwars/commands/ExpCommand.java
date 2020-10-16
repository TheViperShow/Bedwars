package me.thevipershow.bedwars.commands;

import java.util.concurrent.CompletableFuture;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.game.ExperienceManager;
import me.thevipershow.bedwars.game.GameManager;
import me.thevipershow.bedwars.storage.sql.tables.RankTableUtils;
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
                    p.sendMessage(Bedwars.PREFIX + "§7You still don't have any experience points.");
                } else {
                    p.sendMessage(Bedwars.PREFIX + "§7He still doesn't have any experience points.");
                }

            } else {
                final int playerLevel = ExperienceManager.findLevelFromExp(e);
                final int playerLevelMinExp = ExperienceManager.requiredExpMap.get(playerLevel);
                final int playerNextLevelMinExp = ExperienceManager.requiredExpMap.get(playerLevel + 1);

                final int expForNextLevel = playerNextLevelMinExp - playerLevelMinExp;
                final int currentLevelExp = e - playerLevelMinExp;

                if (self) {
                    p.sendMessage(Bedwars.PREFIX + "\n    §7Your current experience: §6§l" + e);
                    p.sendMessage("    §7Your current bedwars level: §6§l" + playerLevel);
                    p.sendMessage("    §7You own §6§l" + currentLevelExp + "§8\\§6§l" + expForNextLevel + " EXP §r§7required to reach §3§lLevel " + (playerLevel + 1));
                } else {
                    p.sendMessage(Bedwars.PREFIX + String.format("\n    §7%s's current experience: §6§l", targetName) + e);
                    p.sendMessage(String.format("    §7%s's current bedwars level: §6§l", targetName) + playerLevel);
                    p.sendMessage(String.format("    §7%s owns §6§l", targetName) + currentLevelExp + "§8\\§6§l" + expForNextLevel + " EXP §r§7required to reach §3§lLevel " + (playerLevel + 1));
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
                    if (!sender.hasPermission("abedwars.admin.exp.view")) {
                        missingPerm(sender);
                    } else {

                        lookupOther(sender, args[2], plugin);
                    }
                }
            } else if (args.length == 4) {
                if (!sender.hasPermission("abedwars.admin.exp.give")) {
                    missingPerm(sender);
                } else {
                    if (args[1].equalsIgnoreCase("add")) {
                        try {
                            final int i = Integer.parseInt(args[3]);
                            RankTableUtils.rewardPlayerExp(args[2], i, plugin);
                            sender.sendMessage(Bedwars.PREFIX + "§7If that player was present in the database, his exp increased by §6§l" + i);
                        } catch (final NumberFormatException e) {
                            sender.sendMessage(Bedwars.PREFIX + "§7The last argument is not a number.");
                        }
                    }
                }
            } else {
                wrongArgsNumber(sender);
            }
        }
    }

}
