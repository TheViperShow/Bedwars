package me.thevipershow.bedwars.game.managers;

import java.util.Map;
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
        teamManager.performAll(bedwarsPlayer -> applyColorsAndProtection(bedwarsPlayer, ArmorSet.generateFromType("leather")));
    }

    private void applyColorsAndProtection(BedwarsPlayer bedwarsPlayer, Map<ArmorSet, ItemStack> map) {
        map.forEach((k,v) -> {
            if (k == ArmorSet.HELMET) {
                v.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
            }
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) v.getItemMeta();
            leatherArmorMeta.setColor(bedwarsPlayer.getBedwarsTeam().getRGBColor());
            v.setItemMeta(leatherArmorMeta);
            k.setArmorPiece(bedwarsPlayer.getInventory(), v);
        });
    }
}
