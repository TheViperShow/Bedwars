package me.thevipershow.bedwars.game.managers;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ArmorSet;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class ArmorManager {

    private final ActiveGame activeGame;

    public ArmorManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    /**
     * Give everyone in the game a leather armour set.
     * The leather set will have the color of their current team.
     * It will also have a aqua affinity enchantment on the helmet.
     */
    public final void giveDefaultColoredSet() {
        TeamManager<?> teamManager = activeGame.getTeamManager();
        teamManager.performAll(bedwarsPlayer -> {
            Player player = bedwarsPlayer.getPlayer();
            ArmorSet.setArmorFromType(player, "leather", false);
            applyColorsAndProtection(bedwarsPlayer, player.getInventory().getArmorContents());
        });
    }

    private void applyColorsAndProtection(BedwarsPlayer bedwarsPlayer, ItemStack[] armorPieces) {
        BedwarsTeam team = bedwarsPlayer.getBedwarsTeam();
        for (ItemStack stack : armorPieces) {
            ItemMeta meta = stack.getItemMeta();
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
            leatherArmorMeta.setColor(team.getRGBColor());
            if (stack.getType().name().endsWith("_HELMET")) {
                stack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }
            stack.setItemMeta(leatherArmorMeta);
        }
    }
}
