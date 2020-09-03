package me.thevipershow.aussiebedwars.bedwars.objects.spawners.animations;


import java.util.List;
import java.util.ListIterator;
import me.thevipershow.aussiebedwars.bedwars.objects.spawners.SpawnerType;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

public final class ArmorStandRunnable implements Runnable {

    private final List<Location> list;
    private ArmorStand armorStand = null;
    private final SpawnerType spawnerType;
    private boolean armorStandExists = false;
    private final String armorStandName;
    private final ListIterator<Location> listIterator;


    public ArmorStandRunnable(List<Location> list, SpawnerType spawnerType, final String armorStandName, final Plugin plugin) {
        this.list = list;
        this.listIterator = list.listIterator();
        this.spawnerType = spawnerType;
        this.armorStand = null;
        this.armorStandName = armorStandName;
    }

    public final void setupArmorStand() {
        if (armorStandExists) return;
        final Location firstLoc = list.get(0);
        this.armorStand = (ArmorStand) firstLoc.getWorld().spawnEntity(firstLoc, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(armorStandName);
        armorStand.setGravity(false);
        armorStand.setHelmet(spawnerType.getHeadItem());
        armorStandExists = true;
    }

    private enum IterationStatus {
        ASCENDING(+1),
        DESCENDING(-1);

        private final int value;

        IterationStatus(final int value) {
            this.value = value;
        }
    }

    private IterationStatus currentIterationStatus = IterationStatus.ASCENDING;

    @Override
    public final void run() {
        setupArmorStand();
        Location toUse = null;
        switch (currentIterationStatus) {
            case ASCENDING: {
                if (listIterator.hasNext()) {
                    toUse = listIterator.next();
                } else {
                    toUse = listIterator.previous();
                    currentIterationStatus = IterationStatus.DESCENDING;
                }
            }
            break;
            case DESCENDING: {
                if (listIterator.hasPrevious()) {
                    toUse = listIterator.previous();
                } else {
                    toUse = listIterator.next();
                    currentIterationStatus = IterationStatus.ASCENDING;
                }
            }
            break;
        }
        armorStand.teleport(toUse);

    }


    public List<Location> getList() {
        return list;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public SpawnerType getSpawnerType() {
        return spawnerType;
    }

    public boolean isArmorStandExists() {
        return armorStandExists;
    }

    public void setArmorStand(ArmorStand armorStand) {
        this.armorStand = armorStand;
    }

    public void setArmorStandExists(boolean armorStandExists) {
        this.armorStandExists = armorStandExists;
    }

    public String getArmorStandName() {
        return armorStandName;
    }
}
