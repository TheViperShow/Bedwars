package me.thevipershow.bedwars.listeners.unregisterable;

import com.google.common.collect.ImmutableList;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.api.BedwarsPlayerDeathEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.managers.TeamManager;
import me.thevipershow.bedwars.game.runnables.RespawnRunnable;
import me.thevipershow.bedwars.listeners.DeathMessages;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import static me.thevipershow.bedwars.game.GameUtils.color;

public final class BedwarsPlayerDeathUnregisterableListener extends UnregisterableListener {
    public BedwarsPlayerDeathUnregisterableListener(@NotNull ActiveGame activeGame) {
        super(activeGame);
    }

    /**
     * Prevents BedwarsPlayers from "glitching" into the void
     * by moving them on their Y axis +150 blocks.
     *
     * @param bedwarsPlayer The BedwarsPlayer to be teleported.
     */
    private void preventVoidBug(@NotNull BedwarsPlayer bedwarsPlayer) {
        if (bedwarsPlayer.getLocation().getY() <= 0) {
            bedwarsPlayer.slideTeleport('y', 150);
        }
    }

    /**
     * Find the possible owner of that TNT.
     *
     * @param tnt The TNT.
     * @return The BedwarsPlayer if he's still in game and he placed that TNT,
     * otherwise will return null.
     */
    @Nullable
    private BedwarsPlayer findTNTOwner(@NotNull TNTPrimed tnt) {
        Entity tntSource = tnt.getSource();
        if (tntSource == null || tntSource.getType() != EntityType.PLAYER) {
            return null;
        }

        Player player = (Player) tntSource;
        if (!player.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return null;
        }

        return activeGame.getPlayerMapper().get(player);
    }

    /**
     * Find the possible owner of this Arrow.
     *
     * @param arrow The Arrow.
     * @return The BedwarsPlayer if he's still in game and he shot
     * that Arrow, otherwise will return null.
     */
    @Nullable
    private BedwarsPlayer findArrowOwner(@NotNull Arrow arrow) {
        ProjectileSource source = arrow.getShooter();
        if (source == null) {
            return null;
        }
        if (!(source instanceof Player)) {
            return null;
        }
        Player shooter = (Player) source;
        return activeGame.getPlayerMapper().get(shooter);
    }

    /**
     * Tries to find the Owner of the Entity that killed a BedwarsPlayer.
     *
     * @param entity The Entity.
     * @return Returns null if no owner could be matched, otherwise a BedwarsPlayer.
     */
    @Nullable
    private BedwarsPlayer findEntityOwner(@NotNull Entity entity) {
        switch (entity.getType()) {
            case PRIMED_TNT:
                return findTNTOwner((TNTPrimed) entity);
            case ARROW:
                return findArrowOwner((Arrow) entity);
            default:
                return null;
        }
    }

    /**
     * Generates a funny message reporting how a BedwarsPlayer has died based on the given arguments.
     *
     * @param killerEntityOwner The BedwarsPlayer that has killed the player.
     *                          Use null if has not been killed by another player.
     * @param killed            The BedwarsPlayer that has been killed.
     * @param killerEntityType  The EntityType of the Entity that has killed the player.
     * @return The death message.
     */
    @SuppressWarnings("deprecation")
    @NotNull
    private String generateEntityDeathMessage(@Nullable BedwarsPlayer killerEntityOwner, @NotNull BedwarsPlayer killed, EntityType killerEntityType) {
        final StringBuilder builder = new StringBuilder();
        BedwarsTeam killedTeam = killed.getBedwarsTeam();
        builder.append('&').append(killedTeam.getColorCode()).append(killed.getName());
        if (killerEntityOwner == null) {
            switch (killerEntityType) {
                case PRIMED_TNT:
                    builder.append(" &7has exploded into pieces.");
                    break;
                case ARROW:
                    builder.append("'s &7heart has been pierced by an arrow.");
                    break;
                default:
                    builder.append(" &7has been slain by a ").append(killerEntityType.getName());
                    break;
            }
        } else {
            BedwarsTeam killerTeam = killerEntityOwner.getBedwarsTeam();
            String coloredKillerName = killerTeam.getColorCode() + killerEntityOwner.getName();
            switch (killerEntityType) {
                case PRIMED_TNT:
                    builder.append(" &7has exploded from &").append(coloredKillerName).append("'s &7TNT");
                    break;
                case ARROW:
                    builder.append("'s &7heart has been pierced by &").append(coloredKillerName).append("'s &7arrow");
                    break;
                default:
                    builder.append(" &7has been killed by &").append(coloredKillerName);
                    break;
            }
        }
        return color(builder.toString());
    }

    /**
     * This methods generates a funny message for players that died
     * but were not killed by an entity.
     *
     * @param cause The DamageCause.
     * @return The message.
     */
    @NotNull
    private String genericDeathMessage(@NotNull BedwarsPlayer died, @NotNull DamageCause cause) {
        BedwarsTeam diedTeam = died.getBedwarsTeam();
        return color("&" + diedTeam.getColorCode() + died.getName() + " &7" + DeathMessages.getRandomFrom(cause));
    }

    /**
     * Slain kill msg
     *
     * @param killer .
     * @return The message.
     */
    @NotNull
    private String slainDeath(@NotNull BedwarsPlayer killer, @NotNull BedwarsPlayer killed) {
        BedwarsTeam kTeam = killer.getBedwarsTeam();
        BedwarsTeam dTeam = killed.getBedwarsTeam();
        return color("&" + dTeam.getColorCode() + killed.getName() + " " + DeathMessages.SLAIN.getRandom() + " by &" + kTeam.getColorCode() + killer.getName());
    }

    /**
     * Listens for the 'Death' of a BedwarsPlayer during a Bedwars game.
     * This method has the objective to do several things, such as messaging
     * other players of the death, playing sound effects, and updating the game values.
     *
     * @param event This is the event containing all the required information.
     */
    @EventHandler(ignoreCancelled = true)
    public final void onBedwarsPlayerDeath(BedwarsPlayerDeathEvent event) {
        if (!this.activeGame.equals(event.getActiveGame())) { // this shouldn't ever happen, however
            return;                                      // I ensure games instances are not different.
        }

        TeamManager<?> teamManager = activeGame.getTeamManager();
        DamageCause cause = event.getCause();
        BedwarsPlayer killed = event.getDied();
        BedwarsPlayer killer = event.getKiller();
        Entity killerEntity = event.getKillerEntity();
        boolean isFinalKill = event.isFinalKill();

       // teamManager.checkForTeamLose(killed.getBedwarsTeam()); // checking if that team has lost after
        // that this player has been killed.

       // teamManager.checkForTeamWin(); // checking if any team has won after that player has been killed. (it can happen!)

        RespawnRunnable.startForBedwarsPlayer(this.activeGame, killed); // starting his animation

        GameUtils.sendKillSound(killed); // sending a kill sound to the killed.

        preventVoidBug(killed); // prevents getting stuck in the void.

        StringBuilder deathMessage = new StringBuilder(); // the death message that will be sent to all.

        if (killerEntity != null && killer != null) { // Killer is a BedwarsPlayer

            GameUtils.sendKillActionBar(this.activeGame, killer, killed); // sending a kill action bar to the killer
            GameUtils.sendKillSound(killer);                              // sending a kill sound to the killer

            deathMessage.append(slainDeath(killer, killed));
        } else if (killerEntity != null && killer == null) { // Killer may be a TNT or other entities.
            BedwarsPlayer foundKiller = findEntityOwner(killerEntity);
            deathMessage.append(generateEntityDeathMessage(foundKiller, killed, killerEntity.getType()));

            if (foundKiller != null) {
                GameUtils.sendKillActionBar(this.activeGame, killer, killed); // sending a kill action bar to the killer
                GameUtils.sendKillSound(killer);                              // sending a kill sound to the killer
            }
        } else { // killer is not an entity
            deathMessage.append(genericDeathMessage(killed, cause));
        }

        if (isFinalKill) {
            deathMessage.append(color("&7. &c&lFINAL KILL!")); // adding final kill to the output because the player was final killed!
            setGameSpectator(killed);     // he dead bro.
        }

        final String sendToAll = deathMessage.toString();
        teamManager.sendMessageToAll(sendToAll); // sending the message to everyone in the game!
    }

    /**
     * Make a player enter the spectator mode.
     * When in this gamemode the player will be in survival,
     * invisible, and unable to interact with anything present
     * in the game.
     *
     * @param target The BedwarsPlayer.
     */
    private void setGameSpectator(BedwarsPlayer target) {
        ItemStack compassToGive = generateLobbyCompass();
        Player targetPlayer = target.getPlayer();
        targetPlayer.setGameMode(GameMode.SURVIVAL);
        activeGame.getInternalGameManager().getInvisibilityManager().hidePlayer(target);
        targetPlayer.setAllowFlight(true);

    }

    private ItemStack generateLobbyCompass() {
        if (LOBBY_COMPASS == null) {
            final ItemStack compass = new ItemStack(Material.COMPASS);
            final ItemMeta compassMeta = compass.getItemMeta();
            compassMeta.setDisplayName(GameUtils.color("&8[&c&lRETURN TO LOBBY&r&8]"));
            compass.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            compassMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            compassMeta.setLore(ImmutableList.of(
                    ChatColor.GRAY + "Click on this item to",
                    ChatColor.GRAY + "return to the server's lobby."
            ));
            compass.setItemMeta(compassMeta);
            LOBBY_COMPASS = compass;
        }
        return LOBBY_COMPASS.clone();
    }

    private static ItemStack LOBBY_COMPASS = null;
}
