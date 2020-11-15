package me.thevipershow.bedwars.game.objects;

import java.util.Optional;
import me.thevipershow.bedwars.config.objects.upgradeshop.HealPoolUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.Upgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ExperienceManager;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.QuestManager;
import me.thevipershow.bedwars.game.upgrades.ActiveHealPool;
import me.thevipershow.bedwars.placeholders.BedwarsExpansion;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class UpgradesManager {

    private final ActiveGame activeGame;

    public UpgradesManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private void dragonBuffUpgrade(BedwarsPlayer bedwarsPlayer, TeamData<?> data) {

    }

    private void maniacMinerUpgrade(BedwarsPlayer bedwarsPlayer, TeamData<?> data) {
        final int lvl = data.getUpgradeLevel(UpgradeType.MANIAC_MINER);
        data.perform(bp -> bp.getPlayer().addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(0xA455, lvl)));
    }

    private void ironForgeUpgrade(BedwarsPlayer bedwarsPlayer, TeamData<?> data) {
        int lvl = data.getUpgradeLevel(UpgradeType.IRON_FORGE);
        if (lvl == 3) {
            activeGame.getActiveSpawnersManager().getTeamSpawners(bedwarsPlayer.getBedwarsTeam())
                    .stream().findAny().ifPresent(any -> {
                activeGame.getActiveSpawnersManager().getEmeraldBoostsTasks().add(
                        activeGame.getPlugin().getServer().getScheduler().runTaskTimer(activeGame.getPlugin(),
                                () -> activeGame.getCachedGameData().getGame().dropItem(any.getSpawner().getSpawnPosition().toLocation(activeGame.getCachedGameData().getGame()), new ItemStack(Material.EMERALD, 1)).setVelocity(new Vector(0, 0, 0)), 20L, 20L * 180L)
                );
            });
        } else if (lvl == 1 || lvl == 2 || lvl == 4) {
            activeGame.getActiveSpawnersManager().getTeamSpawners(bedwarsPlayer.getBedwarsTeam()).forEach(s -> s.setDropSpeedRegulator((lvl) * 50));
        }
    }

    private void sharpnessUpgrade(BedwarsPlayer bedwarsPlayer, TeamData<?> data) {
        int lvl = data.getUpgradeLevel(UpgradeType.SHARPNESS);
        data.perform(bp -> GameUtils.enchantSwords(Enchantment.DAMAGE_ALL, lvl, bp.getPlayer()));
    }

    private void reinforcedArmor(BedwarsPlayer bedwarsPlayer, TeamData<?> data) {
        int lvl = data.getUpgradeLevel(UpgradeType.REINFORCED_ARMOR);
        data.perform(bp -> GameUtils.enchantArmor(Enchantment.PROTECTION_ENVIRONMENTAL, lvl, bp.getPlayer()));
    }

    private void healPool(BedwarsPlayer bedwarsPlayer, Upgrade upgrade, TeamData<?> data) {
        ActiveHealPool pool = new ActiveHealPool(activeGame, bedwarsPlayer.getBedwarsTeam(), (HealPoolUpgrade) upgrade);
        data.setActiveHealPool(pool);
        pool.start();
    }

    public final void upgrade(BedwarsPlayer bedwarsPlayer, UpgradeType type, Upgrade upgrade) {
        final TeamData<?> data = activeGame.getTeamManager().dataOfBedwarsPlayer(bedwarsPlayer);
        if (data == null) {
            return;
        }
        switch (type) {
            case MANIAC_MINER:
                maniacMinerUpgrade(bedwarsPlayer, data);
                break;
            case DRAGON_BUFF:
                dragonBuffUpgrade(bedwarsPlayer, data);
                break;
            case IRON_FORGE:
                ironForgeUpgrade(bedwarsPlayer, data);
                break;
            case SHARPNESS:
                sharpnessUpgrade(bedwarsPlayer, data);
                break;
            case REINFORCED_ARMOR:
                reinforcedArmor(bedwarsPlayer, data);
                break;
            case HEAL_POOL:
                healPool(bedwarsPlayer, upgrade, data);
                break;
            default:
                break;
        }
    }
}
