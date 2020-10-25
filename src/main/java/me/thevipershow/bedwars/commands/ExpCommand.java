package me.thevipershow.bedwars.commands;

import java.util.concurrent.CompletableFuture;
import me.thevipershow.bedwars.AllStrings;
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
                    p.sendMessage(Bedwars.PREFIX + AllStrings.NO_EXPERIENCE_POINTS.get());
                } else {
                    p.sendMessage(Bedwars.PREFIX + AllStrings.NO_EXPERIENCE_POINTS_HE.get());
                }

            } else {
                final int playerLevel = ExperienceManager.findLevelFromExp(e);
                final int playerLevelMinExp = ExperienceManager.requiredExpMap.get(playerLevel);
                final int playerNextLevelMinExp = ExperienceManager.requiredExpMap.get(playerLevel + 1);

                final int expForNextLevel = playerNextLevelMinExp - playerLevelMinExp;
                final int currentLevelExp = e - playerLevelMinExp;

                if (self) {
                    p.sendMessage(Bedwars.PREFIX + AllStrings.YOUR_CURRENT_EXP.get() + e);
                    p.sendMessage(AllStrings.YOUR_CURRENT_LEVEL.get() + playerLevel);
                    p.sendMessage(String.format(AllStrings.YOU_OWN.get(), currentLevelExp, expForNextLevel, playerLevel + 1));
                } else {
                    p.sendMessage(Bedwars.PREFIX + String.format(AllStrings.HIS_CURRENT_EXP.get(), targetName) + e);
                    p.sendMessage(String.format(AllStrings.HIS_CURRENT_LEVEL.get(), targetName) + playerLevel);
                    p.sendMessage(String.format(AllStrings.HE_OWNS.get(), targetName, currentLevelExp, expForNextLevel, playerLevel+1));
                }
            }
        });
    }

    @Override
    public final void run(final CommandSender sender) {
        if (args.length == 1) {
            if (!sender.hasPermission(AllStrings.PERMISSION_USER_EXP.get())) {
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
                if (args[1].equalsIgnoreCase(AllStrings.VIEW.get())) {
                    if (!sender.hasPermission(AllStrings.PERMISSION_ADMIN_EXP_VIEW.get())) {
                        missingPerm(sender);
                    } else {

                        lookupOther(sender, args[2], plugin);
                    }
                }
            } else if (args.length == 4) {
                if (!sender.hasPermission(AllStrings.PERMISSION_ADMIN_EXP_GIVE.get())) {
                    missingPerm(sender);
                } else {
                    if (args[1].equalsIgnoreCase(AllStrings.ADD.get())) {
                        try {
                            final int i = Integer.parseInt(args[3]);
                            RankTableUtils.rewardPlayerExp(args[2], i, plugin);
                            sender.sendMessage(Bedwars.PREFIX + AllStrings.EXP_ADDED_MSG.get() + i);
                        } catch (final NumberFormatException e) {
                            sender.sendMessage(Bedwars.PREFIX + AllStrings.LAST_ARG_NOT_NUMBER.get());
                        }
                    }
                }
            } else {
                wrongArgsNumber(sender);
            }
        }
    }

}
