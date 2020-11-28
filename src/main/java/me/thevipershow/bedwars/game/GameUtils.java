package me.thevipershow.bedwars.game;

import java.util.HashMap;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.bedwars.objects.spawners.SpawnerType;
import me.thevipershow.bedwars.bedwars.spawner.SpawnerLevel;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.config.objects.Merchant;
import me.thevipershow.bedwars.config.objects.Shop;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.Spawner;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.bedwars.config.objects.UpgradeItem;
import me.thevipershow.bedwars.config.objects.UpgradeLevel;
import me.thevipershow.bedwars.config.objects.upgradeshop.DragonBuffUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.HealPoolUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.IronForgeUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.ManiacMinerUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.ReinforcedArmorUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.SharpnessUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.TrapUpgrades;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.AlarmTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.BlindnessAndPoisonTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.CounterOffensiveTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.MinerFatigueTrap;
import me.thevipershow.bedwars.game.data.Pair;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.deathmatch.AbstractDeathmatch;
import me.thevipershow.bedwars.game.deathmatch.impl.SoloDeathmatch;
import me.thevipershow.bedwars.game.deathmatch.impl.TeamDeathmatch;
import me.thevipershow.bedwars.game.spawners.ActiveSpawner;
import me.thevipershow.bedwars.game.upgrades.merchants.AbstractActiveMerchant;
import me.thevipershow.bedwars.game.upgrades.merchants.impl.ShopActiveMerchant;
import me.thevipershow.bedwars.game.upgrades.merchants.impl.UpgradeActiveMerchant;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public final class GameUtils {

    private final static String NO_AI_TAG = "NoAi";

    private GameUtils() {
        throw new UnsupportedOperationException();
    }

    public static void clearInventory(BedwarsPlayer bedwarsPlayer) {
        PlayerInventory playerInventory = bedwarsPlayer.getInventory();
        playerInventory.setHelmet(null);
        playerInventory.setChestplate(null);
        playerInventory.setLeggings(null);
        playerInventory.setBoots(null);
        playerInventory.clear();
    }

    public static void cleanAllEntities(ActiveGame activeGame) {
        World world = activeGame.getCachedGameData().getGame();
        if (world == null) {
            return;
        }
        for (Entity entity : world.getEntities()) {
            if (entity.getType() != EntityType.PLAYER) {
                entity.remove();
            }
        }
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String beautifyCaps(final String s) {
        final String[] split = s.split("_");
        final StringBuilder stringBuilder = new StringBuilder();
        for (String s1 : split) {
            stringBuilder.append(capitalize(s1.toLowerCase())).append(' ');
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static ItemStack applyEventualColors(ItemStack stack, BedwarsPlayer bedwarsPlayer) {
        Material stackType = stack.getType();
        BedwarsTeam team = bedwarsPlayer.getBedwarsTeam();
        switch (stackType) {
            case STAINED_GLASS:
            case STAINED_GLASS_PANE:
                stack.setDurability(team.getGlassColor());
                break;
            case WOOL:
                stack.setDurability(team.getWoolColor());
                break;
            case STAINED_CLAY:
                stack.setDurability(team.getClayColor());
                break;
            default:
                break;
        }
        return stack;
    }

    public static Pair<HashMap<Integer, Integer>, Boolean> canAfford(final PlayerInventory inventory, final Material currency, final int price) {
        final ItemStack[] contents = inventory.getContents();

        final HashMap<Integer, Integer> playerTakeFromMap = new HashMap<>();
        int took = 0;

        for (int i = 0; i < contents.length; i++) {
            final ItemStack stack = contents[i];
            if (stack == null) continue;
            if (stack.getType() != currency) continue;
            final boolean shouldWeExit = (price - took) <= 0;
            if (shouldWeExit) break;
            final int leftToTake = (price - took);
            final int takeFromThisSlot = Math.min(leftToTake, stack.getAmount());
            playerTakeFromMap.put(i, takeFromThisSlot);
            took += takeFromThisSlot;
        }

        if (playerTakeFromMap.isEmpty() || took < price) return new Pair<>(playerTakeFromMap, false);

        return new Pair<>(playerTakeFromMap, true);
    }

    public static void makePlayerPay(final PlayerInventory inventory, final Material currency, final int price, final HashMap<Integer, Integer> takeFrom) {

        for (Map.Entry<Integer, Integer> entry : takeFrom.entrySet()) {
            final ItemStack at = inventory.getItem(entry.getKey());
            final int newAmount = at.getAmount() - entry.getValue();
            if (newAmount <= 0) {
                inventory.setItem(entry.getKey(), null);
            } else {
                at.setAmount(newAmount);
                inventory.setItem(entry.getKey(), at);
            }
        }
    }

    /**
     * Unsafe check
     *
     * @param p Player p
     * @return the connection
     */
    public static PlayerConnection getPlayerConnection(final Player p) {
        return ((CraftPlayer) p).getHandle().playerConnection;
    }

    public static void setAI(final LivingEntity entity, boolean status) {
        ((CraftEntity) entity).getHandle().ai = status;
    }

    public static BedwarsTeam findMerchantTeam(final Merchant merchant, final ActiveGame game) {
        TeamSpawnPosition nearest = null;
        double sqdDistance = Double.MAX_VALUE;
        for (final TeamSpawnPosition teamSpawnPosition : game.getBedwarsGame().getMapSpawns()) {
            final double tempSqdDistance = merchant.getMerchantPosition().squaredDistance(teamSpawnPosition);
            if (tempSqdDistance < sqdDistance) {
                nearest = teamSpawnPosition;
                sqdDistance = tempSqdDistance;
            }
        }
        return nearest.getBedwarsTeam();
    }

    public static boolean isArmor(final ItemStack stack) {
        final Material material = stack.getType();
        final boolean isHelmet = material.name().endsWith("_HELMET");
        final boolean isChestplate = material.name().endsWith("_CHESTPLATE");
        final boolean isLeggings = material.name().endsWith("_LEGGINGS");
        final boolean isBoots = material.name().endsWith("_BOOTS");
        return isHelmet || isChestplate || isLeggings || isBoots;
    }


    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Generate a kill action bar.
     * @param killed The BedwarsPlayer that has been killed.
     * @return The Packet for the ActionBar.
     */
    public static PacketPlayOutChat killActionBar(BedwarsPlayer killed) {
        final BedwarsTeam killedTeam = killed.getBedwarsTeam();
        final IChatBaseComponent iChatBaseComponent = new ChatMessage(color("&e⚔ You killed player &" + killedTeam.getColorCode() + killed.getName() + " &r&e⚔"));
        return new PacketPlayOutChat(iChatBaseComponent, (byte) 0x02);
    }

    /**
     * Send the predefined action bar for Bedwars kills.
     * @param killer The BedwarsPlayer who performed this kill.
     * @param killed The BedwarsPlayer who has been killed.
     */
    public static void sendKillActionBar(BedwarsPlayer killer, BedwarsPlayer killed) {
        final PlayerConnection conn = getPlayerConnection(killer.getPlayer());
        conn.sendPacket(killActionBar(killed));
    }

    /**
     * This is the predefined sound that gets played after a kill.
     * @param bedwarsPlayer The player who will hear the sound.
     */
    public static void sendKillSound(BedwarsPlayer bedwarsPlayer) {
        bedwarsPlayer.playSound(Sound.NOTE_PLING, 9.0f, 0.805f);
    }

    public static void giveStackToPlayer(final ItemStack itemStack, final Player player, final ItemStack[] contents) {
        final PlayerInventory inv = player.getInventory();

        if (isInventoryEmpty(contents)) {
            inv.setItem(0, itemStack);
            return;
        }

        if (isInventoryFull(contents)) {
            player.getWorld().dropItem(player.getLocation(), itemStack);
            return;
        }

        final Map<Integer, Integer> matchingTypeSlots = new HashMap<>();

        for (int i = 0; i < contents.length; i++) {
            final ItemStack stack = contents[i];
            if (stack == null) {
                continue;
            }
            final int stackAmount = stack.getAmount();
            if (stack.getType() == itemStack.getType() && stack.getDurability() == itemStack.getDurability() && stack.getAmount() != 64) {
                matchingTypeSlots.put(i, stackAmount);
            }
        }

        if (matchingTypeSlots.isEmpty()) {
            inv.setItem(findFirstEmptySlot(contents), itemStack);
            return;
        }

        int given = 0;
        final int toGive = itemStack.getAmount();

        for (Map.Entry<Integer, Integer> entry : matchingTypeSlots.entrySet()) {
            if (toGive - given <= 0) break;
            final int key = entry.getKey();
            final int slotItemsCount = entry.getValue();
            final int canAddToThisSlot = 64 - slotItemsCount;
            final int leftToGive = (toGive - given);
            final int willGive = leftToGive > canAddToThisSlot ? (slotItemsCount + canAddToThisSlot) : leftToGive;
            final ItemStack s = contents[key];
            if (slotItemsCount + willGive > 64) {
                // Bro wtf you're doing get out immediately
                continue;
            }
            s.setAmount(slotItemsCount + willGive);
            inv.setItem(key, s);
            given += willGive;
        }

    }

    /**
     * Searches for the first empty slot if someone's inventory;
     *
     * @return any integer if found, -1 if no slots are found.
     */
    public static int findFirstEmptySlot(final ItemStack[] contents) {
        int empty = -1;
        for (int i = 0; i < contents.length; i++) {
            final ItemStack content = contents[i];
            if (content == null) {
                empty = i;
                break;
            }
        }
        return empty;
    }

    public static void decreaseItemInHand(final Player player) {
        final ItemStack i = player.getItemInHand();
        if (i == null) return;
        if (i.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            i.setAmount(i.getAmount() - 1);
            player.setItemInHand(i);
        }
    }

    /**
     * Evaluates if someone's inventory is empty
     *
     * @return true if completely empty, false otherwise.
     */
    public static boolean isInventoryEmpty(final ItemStack[] contents) {
        boolean empty = true;
        for (final ItemStack content : contents) {
            if (content != null) {
                empty = false;
                break;
            }
        }
        return empty;
    }

    /**
     * Evaluates if someone's inventor is full
     *
     * @return true if completely full, false otherwise.
     */
    public static boolean isInventoryFull(final ItemStack[] contents) {
        boolean full = true;
        for (final ItemStack content : contents) {
            if (content == null) {
                full = false;
                break;
            }
        }
        return full;
    }

    public static AbstractActiveMerchant fromMerchant(final Merchant merchant, final ActiveGame activeGame) {
        switch (merchant.getMerchantType()) {
            case SHOP:
                return new ShopActiveMerchant(activeGame, merchant, findMerchantTeam(merchant, activeGame));
            case UPGRADE:
                return new UpgradeActiveMerchant(activeGame, merchant, findMerchantTeam(merchant, activeGame));
        }
        return null;
    }

    public static String generateDeathmatch(final AbstractDeathmatch abstractDeathmatch) {
        final long t = abstractDeathmatch.timeUntilDeathmatch();
        if (t < 0) {
            return color("&6&lDEATHMATCH &r&7(&fStarted&7)");
        }
        return color("&6&lDEATHMATCH &r&7in &e" + t + "&7s");
    }

    public static String generateDragons(final AbstractDeathmatch abstractDeathmatch) {
        final long t = abstractDeathmatch.timeUntilDeathmatch();
        if (t < 0) {
            return color("&5&lDRAGONS &r&7(&fReleased&7)");
        }
        return color("&5&lDRAGONS &r&7in &e" + t + "&7s");
    }

    public static String generateScoreboardMissingTimeSpawners(final ActiveSpawner activeSpawner) {
        final SpawnerType type = activeSpawner.getType();
        final StringBuilder str = new StringBuilder();
        if (type == SpawnerType.DIAMOND) {
            str.append(ChatColor.BLUE);
        } else if (type == SpawnerType.EMERALD) {
            str.append(ChatColor.GREEN);
        } else if (type == SpawnerType.GOLD) {
            str.append(ChatColor.GOLD);
        } else {
            str.append(ChatColor.WHITE);
        }

        str.append(type.name());

        final long timeLeft = activeSpawner.getTimeUntilNextLevel();

        if (timeLeft == -1L) {
            str.append(color(" &7Spawner (&eMax Lvl.&7)"));
        } else {
            str.append(color(" &7Lvl. &e")).append(toRoman(1 + activeSpawner.getCurrentLevel().getLevel())).append(ChatColor.GRAY + " in " + ChatColor.YELLOW).append(timeLeft).append(ChatColor.GRAY + "s");
        }
        return str.toString();
    }

    public static String toRoman(int number) {
        return String.valueOf(new char[number]).replace('\0', 'I')
                .replace("IIIII", "V")
                .replace("IIII", "IV")
                .replace("VV", "X")
                .replace("VIV", "IX")
                .replace("XXXXX", "L")
                .replace("XXXX", "XL")
                .replace("LL", "C")
                .replace("LXL", "XC")
                .replace("CCCCC", "D")
                .replace("CCCC", "CD")
                .replace("DD", "M")
                .replace("DCD", "CM");
    }


    public static void buyFailSound(final Player player) {
        player.playSound(player.getLocation(), Sound.DOOR_CLOSE, 5.0f, 0.768f);
    }

    public static void paySound(final Player player) {
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10.0f, 1.0f);
    }

    public static AbstractDeathmatch deathmatchFromGamemode(final Gamemode gamemode, final ActiveGame activeGame) {
        switch (gamemode) {
            case SOLO:
                return new SoloDeathmatch(activeGame);
            case DUO:
            case QUAD:
                return new TeamDeathmatch(activeGame);
            default:
                throw new IllegalArgumentException();
        }
    }

    public static ItemStack applyEnchant(final ItemStack stack, final Enchantment enchant, final int level) {
        if (stack != null) {
            stack.addUnsafeEnchantment(enchant, level);
        }
        return stack;
    }

    public static void enchantSwords(final Enchantment enchant, final int level, final Player player) {

        final PlayerInventory inv = player.getInventory();

        final ItemStack[] contents = inv.getContents();
        for (final ItemStack content : contents) {
            if (content != null) {
                if (content.getType().name().endsWith("_SWORD")) {
                    content.addEnchantment(enchant, level);
                }
            }
        }
    }

    public static void enchantArmor(final Enchantment enchant, final int level, final Player player) {

        final PlayerInventory inv = player.getInventory();

        final ItemStack leggings = inv.getLeggings();
        final ItemStack boots = inv.getBoots();

        applyEnchant(leggings, enchant, level);
        applyEnchant(boots, enchant, level);
    }

    public static ActiveGame from(final World world, final BedwarsGame game, final World lobbyWorld, final Plugin plugin) {
        return new ActiveGame(world, game, lobbyWorld, plugin);
    }

    public static void payMaterial(Material material, int amount, Inventory inventory) {
        Map<Integer, ? extends ItemStack> map = inventory.all(material);
        if (!map.isEmpty()) {
            int collected = 0b00;
            for (Map.Entry<Integer, ? extends ItemStack> entry : map.entrySet()) {
                int slot = entry.getKey();
                ItemStack stack = entry.getValue();
                int stackAmount = stack.getAmount();
                int toTake = Math.min(stackAmount, amount - collected);
                if (toTake == stackAmount) {
                    inventory.setItem(slot, null);
                } else {
                    stack.setAmount(stackAmount - toTake);
                }
                collected += toTake;
                if (collected >= amount) {
                    return;
                }
            }
        }
    }

    public static void registerSerializers() {
        ConfigurationSerialization.registerClass(DragonBuffUpgrade.class);
        ConfigurationSerialization.registerClass(HealPoolUpgrade.class);
        ConfigurationSerialization.registerClass(IronForgeUpgrade.class);
        ConfigurationSerialization.registerClass(ManiacMinerUpgrade.class);
        ConfigurationSerialization.registerClass(ReinforcedArmorUpgrade.class);
        ConfigurationSerialization.registerClass(SharpnessUpgrade.class);
        ConfigurationSerialization.registerClass(UpgradeShop.class);
        ConfigurationSerialization.registerClass(me.thevipershow.bedwars.config.objects.Enchantment.class);
        ConfigurationSerialization.registerClass(UpgradeItem.class);
        ConfigurationSerialization.registerClass(UpgradeLevel.class);
        ConfigurationSerialization.registerClass(SpawnerLevel.class);
        ConfigurationSerialization.registerClass(Spawner.class);
        ConfigurationSerialization.registerClass(ShopItem.class);
        ConfigurationSerialization.registerClass(Shop.class);
        ConfigurationSerialization.registerClass(Merchant.class);
        ConfigurationSerialization.registerClass(ShopItem.class);
        ConfigurationSerialization.registerClass(TeamSpawnPosition.class);
        ConfigurationSerialization.registerClass(MinerFatigueTrap.class);
        ConfigurationSerialization.registerClass(BlindnessAndPoisonTrap.class);
        ConfigurationSerialization.registerClass(CounterOffensiveTrap.class);
        ConfigurationSerialization.registerClass(AlarmTrap.class);
        ConfigurationSerialization.registerClass(TrapUpgrades.class);
    }
}
