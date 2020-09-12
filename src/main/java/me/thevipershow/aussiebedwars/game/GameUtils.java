package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.config.objects.Merchant;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class GameUtils {
    public final static String NO_AI_TAG = "NoAi";

    private GameUtils() {
        throw new UnsupportedOperationException("Instantiation of Utility class " + getClass().getName());
    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
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
        eTag.setInt(NO_AI_TAG, status ? 0x00 : 0x01);
        e.f(eTag);
    }

    public static AbstractActiveMerchant fromMerchant(final Merchant merchant, final ActiveGame activeGame) {
        switch (merchant.getMerchantType()) {
            case SHOP:
                return new ShopActiveMerchant(activeGame, merchant);
            case UPGRADE:
                return new UpgradeActiveMerchant(activeGame, merchant);
        }
        return null;
    }
}
