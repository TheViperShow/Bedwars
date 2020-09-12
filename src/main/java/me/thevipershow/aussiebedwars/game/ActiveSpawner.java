package me.thevipershow.aussiebedwars.game;

import java.util.LinkedList;
import me.thevipershow.aussiebedwars.bedwars.objects.spawners.SpawnerType;
import me.thevipershow.aussiebedwars.bedwars.spawner.SpawnerLevel;
import me.thevipershow.aussiebedwars.config.objects.Spawner;
import me.thevipershow.aussiebedwars.game.data.ImmutableTraversableList;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class ActiveSpawner {

    private static long now() {
        return System.currentTimeMillis();
    }

    private final SpawnerType type;
    private final Spawner spawner;
    private final ActiveGame game;
    private final SpawnerLevel currentLevel;
    private final ImmutableTraversableList<Location> cachedAnimation;

    private long lastDrop = -1L;
    private BukkitTask animationTask = null;
    private BukkitTask dropTask = null;
    private BukkitTask updateNameTask = null;
    private ArmorStand stand = null;

    private final static double FOURTH_OF_PI = Math.PI / 4.000;
    private final static double START = -4.000;
    private final static double END = +4.000;
    private final static double LOOP_INCREASE = 0.100;
    private final static double YAW_INCREASE = 4.5;
    private final static double MAX_HEIGHT_MOV = 0.850;

    private static ImmutableTraversableList<Location> generateAnimation(final Location absolutePos) {
        double start = START;
        final LinkedList<Location> animationValues = new LinkedList<>();
        float lastYaw = 0.000f;
        while (start <= END) {
            final Location clonedLoc = absolutePos.clone();
            clonedLoc.add(
                    0.000,
                    MAX_HEIGHT_MOV * Math.sin(start * FOURTH_OF_PI),
                    0.000);
            clonedLoc.setYaw(lastYaw);
            animationValues.offer(clonedLoc);
            start += LOOP_INCREASE;
            lastYaw += YAW_INCREASE;
        }
        return new ImmutableTraversableList<>(animationValues);
    }

    public ActiveSpawner(Spawner spawner, ActiveGame game) {
        this.spawner = spawner;
        this.type = spawner.getSpawnerType();
        this.game = game;
        this.currentLevel = spawner.getSpawnerLevels().get(0);
        this.cachedAnimation = generateAnimation(spawner.getSpawnPosition().toLocation(game.getAssociatedWorld()));
    }

    private String generateStandName() {
        return String.format("§7Level: §7[§e%s§7] Next in: (§e%d§7)s",
                currentLevel.getLevel(),
                spawner.getDropDelay() - ((now() - lastDrop) / 1000));
    }

    private void drop() {
        game.associatedWorld.dropItem(spawner.getSpawnPosition().toLocation(game.associatedWorld).add(0, 1, 0),
                new ItemStack(type.getDropItem(), spawner.getDropAmount() + currentLevel.getDropIncrease()));
    }

    public void spawn() {
        if (active()) {
            return;
        }

        final Location spawnStandAt = spawner.getSpawnPosition().toLocation(game.associatedWorld);
        if (!spawnStandAt.getWorld().isChunkLoaded(spawnStandAt.getChunk()))
            spawnStandAt.getWorld().loadChunk(spawnStandAt.getChunk());

        if (!spawner.isInvisible()) {
            this.stand = (ArmorStand) game.associatedWorld.spawnEntity(spawnStandAt, EntityType.ARMOR_STAND);
            this.stand.setGravity(false);
            this.stand.setVisible(false);
            this.stand.setCanPickupItems(false);
            this.stand.setCustomNameVisible(true);
            this.stand.setHelmet(type.getHeadItem().clone());

            this.updateNameTask = game.plugin.getServer()
                    .getScheduler()
                    .runTaskTimer(game.plugin, () -> this.stand.setCustomName(generateStandName()), 1L, 20L);

            this.animationTask = game.plugin.getServer()
                    .getScheduler()
                    .runTaskTimer(game.plugin, () -> stand.teleport(cachedAnimation.move()), 1L, 1L);
        }

        // game.plugin.getLogger().info("Created spawner " + spawner.toString() + '\n');

        this.dropTask = game.plugin.getServer()
                .getScheduler()
                .runTaskTimer(game.plugin, () -> {
                    final long currentTime = now();
                    if (lastDrop == -1L || ((currentTime - lastDrop) / 1000 >= spawner.getDropDelay())) {
                        lastDrop = currentTime;
                        drop();
                    }
                }, 1, 20L);
    }

    public boolean active() {
        return this.animationTask != null
                && this.updateNameTask != null
                && this.dropTask != null;
    }

    public void despawn() {
        if (active()) {
            animationTask.cancel();
            updateNameTask.cancel();
            dropTask.cancel();
            getCachedAnimation().clear();
            stand.remove();
        }
    }

    /*--------------------------------------------------------------------------------------------------------------*/

    public SpawnerType getType() {
        return type;
    }

    public Spawner getSpawner() {
        return spawner;
    }

    public ImmutableTraversableList<Location> getCachedAnimation() {
        return cachedAnimation;
    }

    public BukkitTask getAnimationTask() {
        return animationTask;
    }

    public BukkitTask getDropTask() {
        return dropTask;
    }

    public BukkitTask getUpdateNameTask() {
        return updateNameTask;
    }

    public ArmorStand getStand() {
        return stand;
    }

    public ActiveGame getGame() {
        return game;
    }

    public SpawnerLevel getCurrentLevel() {
        return currentLevel;
    }

    public long getLastDrop() {
        return lastDrop;
    }
}
