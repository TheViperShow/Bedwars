package me.thevipershow.bedwars.listeners.game;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.Pair;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public final class PlayerDeathListener2 extends UnregisterableListener {

    private final ActiveGame activeGame;
    private final Random random;

    public PlayerDeathListener2(final ActiveGame activeGame) {
        this.activeGame = activeGame;
        this.random = new Random();
        final ItemMeta compassMeta = LOBBY_COMPASS.getItemMeta();
        compassMeta.setDisplayName(Bedwars.PREFIX + "§c§lReturn to lobby");
        compassMeta.setLore(Lists.newArrayList(
                "§7- You can use this compass to return to the server's lobby",
                "§7  You can simply click on it and you will be automatically teleported.",
                "§7  Remember that you can't join this game once you've left."
        ));
        LOBBY_COMPASS.setItemMeta(compassMeta);
    }

    public boolean isFinalDeath(final Player player) {
        return (activeGame.getDestroyedTeams().contains(activeGame.getPlayerTeam(player)) || activeGame.getAbstractDeathmatch().isRunning());
    }

    public final String generateNormalDeathMessage(final Player dead, final Player killer, final DamageCause damageCause) {
        if (!isFinalDeath(dead)) {
            if (damageCause == DamageCause.ENTITY_ATTACK) {
                return String.format("§%c%s §7was slashed by §%c%s.", activeGame.getPlayerTeam(dead).getColorCode(), dead.getName(), activeGame.getPlayerTeam(killer).getColorCode(), killer.getName());
            } else if (damageCause == DamageCause.BLOCK_EXPLOSION || damageCause == DamageCause.ENTITY_EXPLOSION) {
                return String.format("§%c%s's §7TNT killed §%c%s.", activeGame.getPlayerTeam(killer).getColorCode(), killer.getName(), activeGame.getPlayerTeam(dead).getColorCode(), dead.getName());
            } else if (damageCause == DamageCause.PROJECTILE) {
                return String.format("§%c%s's §7arrow perforated §%c%s.", activeGame.getPlayerTeam(killer).getColorCode(), killer.getName(), activeGame.getPlayerTeam(dead).getColorCode(), dead.getName());
            } else {
                return String.format("§%c%s §7was killed by §%c%s.", activeGame.getPlayerTeam(dead).getColorCode(), dead.getName(), activeGame.getPlayerTeam(killer).getColorCode(), killer.getName());
            }
        } else {
            if (damageCause == DamageCause.ENTITY_ATTACK) {
                return String.format("§%c%s §7was slashed by §%c%s. §c§lFINAL KILL!", activeGame.getPlayerTeam(dead).getColorCode(), dead.getName(), activeGame.getPlayerTeam(killer).getColorCode(), killer.getName());
            } else if (damageCause == DamageCause.BLOCK_EXPLOSION || damageCause == DamageCause.ENTITY_EXPLOSION) {
                return String.format("§%c%s's §7TNT killed §%c%s. §c§lFINAL KILL!", activeGame.getPlayerTeam(killer).getColorCode(), killer.getName(), activeGame.getPlayerTeam(dead).getColorCode(), dead.getName());
            } else if (damageCause == DamageCause.PROJECTILE) {
                return String.format("§%c%s's §7arrow perforated §%c%s. §c§lFINAL KILL!", activeGame.getPlayerTeam(killer).getColorCode(), killer.getName(), activeGame.getPlayerTeam(dead).getColorCode(), dead.getName());
            } else {
                return String.format("§%c%s §7was killed by §%c%s. §c§lFINAL KILL!", activeGame.getPlayerTeam(dead).getColorCode(), dead.getName(), activeGame.getPlayerTeam(killer).getColorCode(), killer.getName());
            }
        }
    }

    public static void doDeathTimer(final Player p, final ActiveGame activeGame) {
        activeGame.getPlayersRespawning().add(p);
        new PlayerDeathListener.RespawnRunnable(p, activeGame).runTaskTimer(activeGame.getPlugin(), 1L, 20L);
        activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), () -> activeGame.getPlayersRespawning().remove(p), 20L * 5L);
    }

    public void doNonFinalDeath(final Player dead, final Player killer, final DamageCause damageCause) {

        dead.setGameMode(GameMode.SPECTATOR);
        GameUtils.clearInvExceptArmorAndTools(dead, activeGame);

        final String deathMsg = generateNormalDeathMessage(dead, killer, damageCause);
        activeGame.getAssociatedWorld().getPlayers().forEach(player -> player.sendMessage(Bedwars.PREFIX + deathMsg));

        doDeathTimer(dead, activeGame);

        activeGame.getKillTracker().increaseKillsCounter(killer);
    }

    public void doNonFinalDeath(final Player dead, final DamageCause damageCause) {

        dead.setGameMode(GameMode.SPECTATOR);
        GameUtils.clearInvExceptArmorAndTools(dead, activeGame);

        final String deathMsg = funnyDeathMsg(damageCause, dead);
        activeGame.getAssociatedWorld().getPlayers().forEach(player -> player.sendMessage(Bedwars.PREFIX + deathMsg));

        doDeathTimer(dead, activeGame);
    }

    private static final ItemStack LOBBY_COMPASS = new ItemStack(Material.COMPASS, 0x01);

    public static void givePlayerLobbyCompass(final Player p) {
        p.getInventory().setItemInHand(LOBBY_COMPASS);
    }

    public void doFinalDeath(final Player dead, final Player killer, final DamageCause damageCause) {
        activeGame.removePlayer(dead);
        dead.setAllowFlight(true);
        dead.setFlying(true);
        dead.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(42000, 1), true);
        dead.getInventory().clear();
        GameUtils.clearArmor(dead);
        givePlayerLobbyCompass(dead);
        if (!activeGame.isOutOfGame(dead)) {
            dead.sendMessage(Bedwars.PREFIX + "§cYou have been eliminated.");
            final String deathMsg = generateNormalDeathMessage(dead, killer, damageCause);
            activeGame.getAssociatedWorld().getPlayers().forEach(p -> p.sendMessage(Bedwars.PREFIX + deathMsg));
        }
        activeGame.getPlayersOutOfGame().add(dead);

        if (!activeGame.isWinnerDeclared()) {
            final BedwarsTeam bedwarsTeam = activeGame.findWinningTeam();
            if (bedwarsTeam != null) {
                activeGame.declareWinner(bedwarsTeam);
                activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), activeGame::stop, 20 * 10L);
            }
        }

        activeGame.getKillTracker().increaseFinalKillsCounter(killer);
    }

    public void doFinalDeath(final Player dead, final DamageCause damageCause) {
        activeGame.removePlayer(dead);
        dead.setAllowFlight(true);
        dead.setFlying(true);
        dead.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(42000, 1), true);
        dead.getInventory().clear();
        GameUtils.clearArmor(dead);
        givePlayerLobbyCompass(dead);
        if (!activeGame.isOutOfGame(dead)) {
            dead.sendMessage(Bedwars.PREFIX + "§cYou have been eliminated.");
            final String deathMsg = funnyDeathMsg(damageCause, dead);
            activeGame.getAssociatedWorld().getPlayers().forEach(p -> p.sendMessage(Bedwars.PREFIX + deathMsg));
        }
        activeGame.getPlayersOutOfGame().add(dead);

        if (!activeGame.isWinnerDeclared()) {
            final BedwarsTeam bedwarsTeam = activeGame.findWinningTeam();
            if (bedwarsTeam != null) {
                activeGame.declareWinner(bedwarsTeam);
                activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), activeGame::stop, 20 * 10L);
            }
        }
    }

    private final HashMap<UUID, Pair<UUID, Long>> lastAttackMap = new HashMap<>();

    public String funnyDeathMsg(final DamageCause damageCause, final Player dead) {
        final StringBuilder builder = new StringBuilder("§" + activeGame.getPlayerTeam(dead).getColorCode() + dead.getName() + " §7");
        switch (damageCause) {
            case FALL:
                builder.append("believed the ground was soft.");
                break;
            case FIRE: {
                final int r = random.nextInt(3);
                if (r == 0) {
                    builder.append("entered Dante's inferno.");
                } else if (r == 1) {
                    builder.append("got perfectly grilled.");
                } else {
                    builder.append("proved to be flammable.");
                }
            }
            break;
            case VOID:
                builder.append("disappeared into the void.");
                break;
            case DROWNING:
                builder.append("forgot he wasn't a fish.");
                break;
            case ENTITY_EXPLOSION:
                builder.append("exploded into pieces");
                break;
            case SUFFOCATION:
                builder.append("forgot how to breathe.");
                break;
            case PROJECTILE:
                builder.append("got hit by a projectile.");
                break;
            case CUSTOM:
                builder.append("CUSTOM DAMAGE???");
                break;
            default:
                builder.append("died in a mysterious way.");
                break;
        }

        if (isFinalDeath(dead)) {
            builder.append("§c§l FINAL KILL!");
        }

        return builder.toString();
    }

    public final static class Monkey {
        public final void flip() {
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamage(final EntityDamageEvent event) {

        if (!activeGame.isHasStarted()) {
            return;
        }

        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        final Player damaged = (Player) event.getEntity();
        final World damagedWorld = damaged.getWorld();

        if (!damagedWorld.equals(activeGame.getAssociatedWorld())) {
            return;
        }

        if (damaged.getHealth() - event.getDamage() > 00.00) {
            return;
        }

        event.setCancelled(true);
        final Location loc = damaged.getLocation();
        if (loc.getY() <= 0.00) {
            loc.setY(80.00);
            damaged.teleport(loc);
        }
        damaged.setHealth(damaged.getMaxHealth());
        final boolean isFinalKill = isFinalDeath(damaged);

        if (event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            final Entity damager = e.getDamager();
            if (damager.getType() == EntityType.PRIMED_TNT) {
                if (activeGame.getPlacedTntMap().containsKey(damager.getUniqueId())) {
                    final OfflinePlayer offlinePlayer = activeGame.getPlugin().getServer().getOfflinePlayer(activeGame.getPlacedTntMap().get(damager.getUniqueId()));
                    if (offlinePlayer.isOnline() && !offlinePlayer.getUniqueId().equals(damaged.getUniqueId())) {
                        if (!isFinalDeath(damaged)) {
                            doNonFinalDeath(damaged, offlinePlayer.getPlayer(), e.getCause());
                        } else {
                            doFinalDeath(damaged, offlinePlayer.getPlayer(), e.getCause());
                        }
                        if (!isFinalKill) {
                            activeGame.getKillTracker().increaseKillsCounter(offlinePlayer.getPlayer());
                        } else {
                            activeGame.getKillTracker().increaseFinalKillsCounter(offlinePlayer.getPlayer());
                        }
                    } else {
                        if (!isFinalDeath(damaged)) {
                            doNonFinalDeath(damaged, e.getCause());
                        } else {
                            doFinalDeath(damaged, e.getCause());
                        }
                    }
                }
            } else if (damager.getType() == EntityType.ARROW) {
                final Arrow arrow = (Arrow) damager;
                final ProjectileSource pjs = arrow.getShooter();
                if (pjs instanceof Player) {
                    final Player killer = (Player) pjs;
                    if (killer != null && killer.isOnline()) {
                        if (!isFinalDeath(damaged)) {
                            doNonFinalDeath(damaged, killer, e.getCause());
                        } else {
                            doFinalDeath(damaged, killer, e.getCause());
                        }
                        if (!isFinalKill) {
                            activeGame.getKillTracker().increaseKillsCounter(killer);
                        } else {
                            activeGame.getKillTracker().increaseFinalKillsCounter(killer);
                        }
                    } else {
                        if (!isFinalDeath(damaged)) {
                            doNonFinalDeath(damaged, e.getCause());
                        } else {
                            doFinalDeath(damaged, e.getCause());
                        }
                    }
                }
            } else if (damager.getType() == EntityType.PLAYER) {
                final Player pDamager = (Player) e.getDamager();
                lastAttackMap.put(damaged.getUniqueId(), new Pair<>(damager.getUniqueId(), System.currentTimeMillis()));
                if (!isFinalDeath(damaged)) {
                    doNonFinalDeath(damaged, pDamager, e.getCause());
                } else {
                    doFinalDeath(damaged, pDamager, e.getCause());
                }
            } else {
                if (!isFinalDeath(damaged)) {
                    doNonFinalDeath(damaged, e.getCause());
                } else {
                    doFinalDeath(damaged, e.getCause());
                }
            }
        } else {
            if (event.getCause() == DamageCause.VOID) {
                if (this.lastAttackMap.containsKey(damaged.getUniqueId())) {
                    final Pair<UUID, Long> pair = this.lastAttackMap.get(damaged.getUniqueId());
                    if (System.currentTimeMillis() - pair.getB() <= 10_000) {
                        final OfflinePlayer offlinePlayer = activeGame.getPlugin().getServer().getOfflinePlayer(pair.getA());
                        if (offlinePlayer.isOnline()) {
                            if (!isFinalDeath(damaged)) {
                                doNonFinalDeath(damaged, offlinePlayer.getPlayer(), event.getCause());
                            } else {
                                doFinalDeath(damaged, offlinePlayer.getPlayer(), event.getCause());
                            }
                            if (!isFinalKill) {
                                activeGame.getKillTracker().increaseKillsCounter(offlinePlayer.getPlayer());
                            } else {
                                activeGame.getKillTracker().increaseFinalKillsCounter(offlinePlayer.getPlayer());
                            }
                        } else {
                            if (!isFinalDeath(damaged)) {
                                doNonFinalDeath(damaged, event.getCause());
                            } else {
                                doFinalDeath(damaged, event.getCause());
                            }
                        }
                    }
                    this.lastAttackMap.remove(damaged.getUniqueId());
                } else {
                    if (!isFinalDeath(damaged)) {
                        doNonFinalDeath(damaged, event.getCause());
                    } else {
                        doFinalDeath(damaged, event.getCause());
                    }
                }

            } else {
                if (!isFinalDeath(damaged)) {
                    doNonFinalDeath(damaged, event.getCause());
                } else {
                    doFinalDeath(damaged, event.getCause());
                }
            }
        }
    }
}
