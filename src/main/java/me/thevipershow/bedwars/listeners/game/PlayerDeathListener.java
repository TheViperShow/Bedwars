package me.thevipershow.bedwars.listeners.game;

import com.google.common.collect.Lists;
import java.util.Map;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import me.thevipershow.bedwars.storage.sql.tables.GlobalStatsTableUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Deprecated
public final class PlayerDeathListener extends UnregisterableListener {

    private static final ItemStack LOBBY_COMPASS = new ItemStack(Material.COMPASS, 0x01);

    public PlayerDeathListener(ActiveGame activeGame) {
        super(activeGame);
    }

    /*

    public static void givePlayerLobbyCompass(final Player p) {
        p.getInventory().setItemInHand(LOBBY_COMPASS);
    }

    public static void doDeathTimer(final Player p, final ActiveGame activeGame) {
        activeGame.getPlayersRespawning().add(p);
        new RespawnRunnable(p, activeGame).runTaskTimer(activeGame.getPlugin(), 1L, 20L);
        activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), () -> activeGame.getPlayersRespawning().remove(p), 20L*5L);
    }

    public static void clearInvExceptArmorAndTools(final Player player, final ActiveGame game) {
        final PlayerInventory inv = player.getInventory();
        final ItemStack[] contents = inv.getContents();

        for (int i = 0; i < contents.length; i++) {
            final ItemStack stack = contents[i];
            if (stack == null) {
                continue;
            } else if (stack.getType().name().endsWith("_SWORD")) { // Checking if player has sword
                if (stack.getType() != Material.WOOD_SWORD) {
                    final Map<Enchantment, Integer> availableEnchs = stack.getEnchantments();
                    if (availableEnchs.isEmpty()) {
                        inv.setItem(i, new ItemStack(Material.WOOD_SWORD, 1));
                    } else {
                        final ItemStack es = new ItemStack(Material.WOOD_SWORD, 1);
                        es.addEnchantments(availableEnchs);
                        inv.setItem(i, es);
                    }
                }
                continue;
            } else if (game.getBedwarsGame().getShop().getUpgradeItems()
                    .stream()
                    .flatMap(shop -> shop.getLevels().stream())
                    .anyMatch(lvl -> lvl.getCachedGameStack().isSimilar(stack))) {
                continue;
            }
            inv.setItem(i, null);
        }

        game.downgradePlayerTools(player);

        final int enchantLvl = game.getUpgradesLevelsMap().get(UpgradeType.SHARPNESS).get(game.getPlayerTeam(player));
        if (enchantLvl != 0) {
            GameUtils.enchantSwords(Enchantment.DAMAGE_ALL, enchantLvl, player); // Adding enchant if he has the Upgrade.
        }
    }

    public static String generateDeathMessage(final EntityDamageEvent e, final BedwarsTeam killedPlayerTeam, final boolean finalKill, ActiveGame activeGame) {

        final StringBuilder msg = new StringBuilder("§" + killedPlayerTeam.getColorCode() + e.getEntity().getName());
        if (e instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
            final Entity damager = ee.getDamager();
            if (damager instanceof Player) {
                final Player p = (Player) damager;
                final BedwarsTeam damagerTeam = activeGame.getPlayerTeam(p);
                final DamageCause cz = ee.getCause();
                final String killMsg = cz == DamageCause.PROJECTILE ? "pierced " : "annihilated ";
                msg.append(String.format(" §7was %s by §", killMsg)).append(damagerTeam.getColorCode()).append(p.getName()).append('.');
            } else {
                final DamageCause damageCause = e.getCause();
                switch (damageCause) {
                    case FIRE:
                    case LAVA:
                        msg.append(" §7burned to death.");
                        break;
                    case DROWNING:
                        msg.append(" §7tried to swallow the ocean.");
                        break;
                    case SUFFOCATION:
                        msg.append(" §7suffocated to death.");
                        break;
                    case FALL:
                        msg.append(" §7believed the floor was soft.");
                        break;
                    case VOID:
                        msg.append(" §7fought against gravity.");
                        break;
                    case BLOCK_EXPLOSION:
                    case ENTITY_EXPLOSION:
                        msg.append(" §7exploded into pieces.");
                        break;
                    default:
                        msg.append(" §7has died.");
                        break;
                }
            }
        } else {
            final DamageCause damageCause = e.getCause();
            switch (damageCause) {
                case FIRE:
                case LAVA:
                    msg.append(" §7burned to death.");
                    break;
                case DROWNING:
                    msg.append(" §7tried to swallow the ocean.");
                    break;
                case SUFFOCATION:
                    msg.append(" §7suffocated to death.");
                    break;
                case FALL:
                    msg.append(" §7believed the floor was soft.");
                    break;
                case VOID:
                    msg.append(" §7fought against gravity.");
                    break;
                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    msg.append(" §7exploded into pieces.");
                    break;
                default:
                    msg.append(" §7has died.");
                    break;
            }
        }

        if (finalKill) {
            msg.append(" §e§lFINAL KILL.");
        }
        return msg.toString();
    }

    public static boolean deathLogic(final ActiveGame activeGame, final BedwarsTeam b, final Player p, final EntityDamageEvent event) {
        boolean isFinal = false;

        if (activeGame.getDestroyedTeams().contains(b) || activeGame.getAbstractDeathmatch().isRunning()) { // Checking if players' team's bed has been broken previously.
            // here player has lost the game.
            // or has died permanently since the deathmatch mode is ON.

            isFinal = true;

            activeGame.removePlayer(p);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 42069, 1, false), true);
            p.getInventory().clear();
            GameUtils.clearArmor(p);
            givePlayerLobbyCompass(p);
            if (!activeGame.isOutOfGame(p)) {
                p.sendMessage(Bedwars.PREFIX + "§cYou have been eliminated.");
                final String generatedDeathMsg = generateDeathMessage(event, b, true, activeGame);
                activeGame.getAssociatedWorld().getPlayers().forEach(player -> player.sendMessage(Bedwars.PREFIX + generatedDeathMsg));
            }
            activeGame.getPlayersOutOfGame().add(p);

            final Location pLoc = p.getLocation();
            if (pLoc.getY() <= -5.00) {
                pLoc.setY(90.00);
                p.teleport(pLoc);
            }

            if (!activeGame.isWinnerDeclared()) {
                final BedwarsTeam bedwarsTeam = activeGame.findWinningTeam();
                if (bedwarsTeam != null) {
                    activeGame.declareWinner(bedwarsTeam);
                    activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), activeGame::stop, 20 * 10L);
                }
            }

            if (event instanceof EntityDamageByEntityEvent) {
                final EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
                if (edbee.getDamager() instanceof Player) {
                    activeGame.getKillTracker().increaseFinalKillsCounter((Player) edbee.getDamager());
                }
            }
        } else { // team is still in game
            p.setGameMode(GameMode.SPECTATOR);
            clearInvExceptArmorAndTools(p, activeGame);
            doDeathTimer(p, activeGame);
            final Location pLoc = p.getLocation();
            if (pLoc.getY() <= -5.00) {
                pLoc.setY(90.00);
                p.teleport(pLoc);
            }

            final String generatedDeathMsg = generateDeathMessage(event, b, false, activeGame);
            activeGame.getAssociatedWorld().getPlayers().forEach(player -> player.sendMessage(Bedwars.PREFIX + generatedDeathMsg));

            if (event instanceof EntityDamageByEntityEvent) {
                final EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
                if (edbee.getDamager() instanceof Player) {
                    activeGame.getKillTracker().increaseKillsCounter((Player) edbee.getDamager());
                }
            }
        }

        p.setHealth(p.getMaxHealth());
        return isFinal;
    }

    @EventHandler()
    public final void onPlayerDeath(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player p = (Player) event.getEntity();
        final World w = p.getWorld();

        if (!w.equals(activeGame.getAssociatedWorld())) {
            return;
        }

        final double playerHealth = p.getHealth();
        final double damage = event.getDamage();

        if (playerHealth - damage <= 0.00) {
            final BedwarsTeam b = activeGame.getPlayerTeam(p);

            p.closeInventory();
            final boolean isFinal = deathLogic(activeGame, b, p, event);
            event.setCancelled(true);

            if (event instanceof EntityDamageByEntityEvent) {
                final EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
                final Entity damager = edbee.getDamager();
                if (damager instanceof Player) {
                    GlobalStatsTableUtils.increaseKills(activeGame.getBedwarsGame().getGamemode(), activeGame.getPlugin(), damager.getUniqueId(), isFinal);
                    GameUtils.sendKillActionBar(activeGame, (Player) damager,  p);
                    ((Player) damager).playSound(damager.getLocation(), Sound.SPLASH, 8.50f, 0.65f);
                }
            }

        } else if (event.getCause() == DamageCause.FALL) {
            p.setNoDamageTicks(0x10);    // Making him invincible for 0.5s.
        }
    }*/
}
