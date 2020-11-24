package me.thevipershow.bedwars.game.runnables;

import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.config.objects.UpgradeItem;
import me.thevipershow.bedwars.config.objects.UpgradeLevel;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.game.enums.PlayerState;
import me.thevipershow.bedwars.game.managers.GameInventoriesManager;
import me.thevipershow.bedwars.game.shop.ShopCategory;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public final class RespawnRunnable extends BukkitRunnable {

    private int secondsLeft = 0x05;
    private final BedwarsPlayer p;
    private final ActiveGame activeGame;

    public RespawnRunnable(BedwarsPlayer p, ActiveGame activeGame) {
        this.p = p;
        this.activeGame = activeGame;
        p.setPlayerState(PlayerState.RESPAWNING);
        p.getPlayer().setGameMode(GameMode.SPECTATOR);
    }

    /**
     * This static method starts a RespawnRunnable animation for a BedwarsPlayer.
     *
     * @param activeGame    The ActiveGame where the BedwarsPlayer is playing on.
     * @param bedwarsPlayer The BedwarsPlayer who will get the animation.
     */
    public static void startForBedwarsPlayer(ActiveGame activeGame, BedwarsPlayer bedwarsPlayer) {
        BukkitRunnable bukkitRunnable = new RespawnRunnable(bedwarsPlayer, activeGame);
        bukkitRunnable.runTaskTimer(activeGame.getPlugin(), 1L, 20L);
        clearInventory(bedwarsPlayer);
        downgradeItems(bedwarsPlayer, activeGame);
    }

    private static final String[] matches = {"_SWORD", "_PICKAXE", "_AXE"};

    private static void downgradeItems(BedwarsPlayer bedwarsPlayer, ActiveGame activeGame) {
        GameInventoriesManager invManager = activeGame.getGameInventories();
        PlayerInventory playerInventory = bedwarsPlayer.getInventory();
        Map<ShopCategory, Inventory> shops = invManager.getPlayerShop().get(bedwarsPlayer.getUniqueId());
        if (shops == null) {
            return;
        }

        Map<UpgradeItem, Integer> upgradeLevels = invManager.getPlayerUpgradeLevels().get(bedwarsPlayer.getUniqueId());
        if (upgradeLevels == null) {
            return;
        }

        for (final Map.Entry<UpgradeItem, Integer> entry : upgradeLevels.entrySet()) {
            UpgradeItem upgrade = entry.getKey();
            List<UpgradeLevel> levels = upgrade.getLevels();
            int lvl = entry.getValue();
            if (lvl <= 0) {
                continue;
            }

            ShopCategory shopCategory = upgrade.getShopCategory();
            Inventory shopCategoryInv = shops.get(shopCategory);
            if (shopCategoryInv == null) {
                continue;
            }

            UpgradeLevel current = levels.get(lvl + 1);
            UpgradeLevel previous = levels.get(lvl);
            upgradeLevels.compute(upgrade, (k,v) -> v--);
            shopCategoryInv.setItem(upgrade.getSlot(), previous.generateFancyStack());
            playerInventory.remove(current.generateGameStack());
            GameUtils.giveStackToPlayer(previous.generateGameStack(), bedwarsPlayer.getPlayer(), playerInventory.getContents());
        }
    }

    private static boolean shouldBeRemovedFromInventory(ItemStack stack) {
        Material type = stack.getType();
        return !type.name().endsWith(matches[0]);
    }

    private static void clearInventory(BedwarsPlayer bedwarsPlayer) {
        PlayerInventory inventory = bedwarsPlayer.getInventory();
        final ItemStack[] contents = inventory.getContents();
        for (byte index = 0x00; index < contents.length; index++) {
            final ItemStack content = contents[index];
            if (content != null && shouldBeRemovedFromInventory(content)) {
                inventory.setItem(index, null);
            }
        }
    }

    /**
     * This method does the animation.
     */
    @Override
    public final void run() {
        if (p == null || !p.isOnline()) {
            cancel();
            p.setPlayerState(PlayerState.DEAD); // this shouldn't ever be necessary, I'll call it just to be sure.
        } else if (secondsLeft <= 0x00) {
            SpawnPosition spawnPos = activeGame.getCachedGameData().getCachedTeamSpawnPositions().get(p.getBedwarsTeam());
            if (spawnPos != null) {
                p.teleport(spawnPos.toLocation(activeGame.getCachedGameData().getGame())); // teleporting him to his spawn.
                p.getPlayer().setGameMode(GameMode.SURVIVAL);                              // Setting his gamemode to survival
                p.setPlayerState(PlayerState.PLAYING); // setting his state
            }
            cancel();
        } else {
            PlayerConnection conn = GameUtils.getPlayerConnection(p.getPlayer());
            PacketPlayOutTitle emptyTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatMessage(""), 2, 16, 2);
            IChatBaseComponent iChat = new ChatMessage(Bedwars.PREFIX + String.format("§eRespawning in §7%d §es", secondsLeft));
            PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iChat, 2, 16, 2);
            conn.sendPacket(emptyTitle);
            conn.sendPacket(titlePacket);
            secondsLeft--;
        }
    }
}
