package me.thevipershow.aussiebedwars.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.Merchant;
import me.thevipershow.aussiebedwars.config.objects.TeamSpawnPosition;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public final class GameUtils {
    public final static String NO_AI_TAG = "NoAi";

    private GameUtils() {
        throw new UnsupportedOperationException("Instantiation of Utility class " + getClass().getName());
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

    /**
     * Unsafe check
     *
     * @param p Player p
     * @return the connection
     */
    public static PlayerConnection getPlayerConnection(final Player p) {
        return ((CraftPlayer) p).getHandle().playerConnection;
    }

    public static void setAI(final Entity entity, boolean status) {
        final net.minecraft.server.v1_8_R3.Entity e = ((CraftEntity) entity).getHandle();
        NBTTagCompound eTag = e.getNBTTag();
        if (eTag == null)
            eTag = new NBTTagCompound();
        e.c(eTag);
        eTag.setInt(NO_AI_TAG, status ? 0 : 1);
        e.f(eTag);
    }

    public static BedwarsTeam findMerchantTeam(final Merchant merchant, final ActiveGame game) {
        TeamSpawnPosition nearest = null;
        double sqdDistance = Double.MAX_VALUE;
        for (final TeamSpawnPosition teamSpawnPosition : game.bedwarsGame.getMapSpawns()) {
            final double tempSqdDistance = merchant.getMerchantPosition().squaredDistance(teamSpawnPosition);
            if (tempSqdDistance < sqdDistance) {
                nearest = teamSpawnPosition;
                sqdDistance = tempSqdDistance;
            }
        }
        return nearest.getBedwarsTeam();
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
            if (stack == null) continue;
            final int stackAmount = stack.getAmount();
            if (stack.getType() == itemStack.getType() && stack.getAmount() != 64) {
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

        //TODO : Maybe fix stacks overflowing
    }

    /**
     * Searches for the first empty slot if someone's inventory;
     *
     * @return any integer if found, -1 if no slots are found.
     */
    public static int findFirstEmptySlot(final ItemStack contents[]) {
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

    public static void clearAllEffects(final Player player) {
        for (final PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
    }

    public static void clearArmor(final Player player) {
        final PlayerInventory inv = player.getInventory();
        inv.setHelmet(null);
        inv.setChestplate(null);
        inv.setLeggings(null);
        inv.setBoots(null);
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

    public static void replaceIfPresent(final ItemStack toGive, final Material toReplace, final Player player) {
        final ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            final ItemStack stack = contents[i];
            if (stack == null) continue;
            if (stack.getType() == toReplace) {
                player.getInventory().setItem(i, toGive);
                break;
            }
        }

        //giveStackToPlayer(toGive, player, contents);
    }

    public static String getUpgradeString(final String oreName) {
        switch (oreName) {
            case "WOOD":
                return "IRON";
            case "IRON":
                return "DIAMOND";
            default:
                return null;
        }
    }

    public static void upgradeTool(final String toolType, final Player player) {
        final ItemStack[] contents = player.getInventory().getContents();
        ItemStack foundTool = null;
        int slot = -1;
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack == null) continue;
            if (stack.getType().name().endsWith(toolType)) {
                slot = i;
                foundTool = stack;
                break;
            }
        }

        final String materialName = foundTool.getType().name();
        final String[] strings = materialName.split("_");

        if (slot != -1) {
            final String nextToolLevel = getUpgradeString(strings[0]);
            if (nextToolLevel == null) return;
            final ItemStack newItem = new ItemStack(Material.valueOf(nextToolLevel + '_' + strings[1]), 1);
            player.getInventory().setItem(slot, newItem);
        } else {
            giveStackToPlayer(new ItemStack(Material.valueOf("WOOD" + toolType), 1), player, contents);
        }

    }

    public static boolean anyMatchMaterial(final Collection<ItemStack> stack, final Material material) {
        return stack.stream().anyMatch(s -> s.getType() == material);
    }
}
