package me.thevipershow.aussiebedwars.bedwars.objects.spawners;

import me.thevipershow.aussiebedwars.bedwars.objects.spawners.animations.ArmorStandAnimation;
import me.thevipershow.aussiebedwars.bedwars.objects.spawners.animations.CachedSinusoidalWave;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

@SerializableAs("Spawner")
public class OreSpawner implements  {
    private final ArmorStandAnimation armorStandAnimation;
    private int dropAmount;

    public OreSpawner(Location startLocation, SpawnerType spawnerType, String armorStandName, Plugin plugin, int dropAmount) {

        this.armorStandAnimation = new ArmorStandAnimation(new CachedSinusoidalWave(startLocation),
                plugin, spawnerType, armorStandName);
        this.dropAmount = dropAmount;
    }

    public void create() {
        armorStandAnimation.startAnimation();
    }

    public void destroy() {
        armorStandAnimation.stopAnimation();
        armorStandAnimation.getTask().cancel();
        armorStandAnimation.getArmorStand().remove();
    }

    public void changeName(final String newName) {
        armorStandAnimation.getArmorStand().setCustomName(newName);
    }

    public void drop() {
        final Location dropLocation = armorStandAnimation.getCurrentArmorStandLocation();
        dropLocation.getWorld().dropItemNaturally(dropLocation, new ItemStack(armorStandAnimation.getRunnable().getSpawnerType().getDropItem(), dropAmount));
    }

    public void setDropAmount(int dropAmount) {
        this.dropAmount = dropAmount;
    }
}
