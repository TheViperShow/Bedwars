package me.thevipershow.bedwars.game.spawners;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.spawners.SpawnerType;
import me.thevipershow.bedwars.bedwars.spawner.SpawnerLevel;
import me.thevipershow.bedwars.config.objects.Spawner;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.ImmutableTraversableList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class ActiveSpawner {

    private static long now() {
        return System.currentTimeMillis();
    }

    private final SpawnerType type;
    private final Spawner spawner;
    private final ActiveGame game;
    private final ImmutableTraversableList<Location> cachedAnimation;
    private final List<BukkitTask> levelUpTasks;
    private final int maxNearby;

    private long lastDrop = -1L;

    private SpawnerLevel currentLevel;
    private BukkitTask animationTask = null;
    private BukkitTask dropTask = null;
    private BukkitTask updateNameTask = null;
    private ArmorStand stand = null;
    private double dropSpeedRegulator = 0;
    private int dropAmountRegulator = 0;
    private long timePassedSinceCreation = 0L;

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
        this.cachedAnimation = generateAnimation(spawner.getSpawnPosition().toLocation(game.getCachedGameData().getGame()));
        final int gamemodeMultiplier = game.getBedwarsGame().getGamemode() == Gamemode.SOLO ? 1 : 2;
        switch (type) {
            case DIAMOND:
                this.maxNearby = 4 * gamemodeMultiplier;
                break;
            case EMERALD:
                this.maxNearby = 2 * gamemodeMultiplier;
                break;
            case IRON:
                this.maxNearby = 64;
                break;
            case GOLD:
                this.maxNearby = 32;
                break;
            default:
                this.maxNearby = -1;
                break;
        }

        this.levelUpTasks = new ArrayList<>();
    }

    private String generateStandName() {
        return String.format(AllStrings.SPAWNER_NAME.get(),
                currentLevel.getLevel(),
                (spawner.getDropDelay() - currentLevel.getDecreaseSpawnDelay()) - ((now() - lastDrop) / 1000));
    }

    private void drop() {
        final World w = game.getCachedGameData().getGame();
        final Location dropSpawnPos = spawner.getSpawnPosition().toLocation(w);
        final int nearbyEntities = w.getNearbyEntities(dropSpawnPos, 1.501, 3.00, 1.501)
                .stream()
                .filter(e -> e instanceof Item)
                .map(e -> (Item) e)
                .filter(i -> {
                    final ItemStack s = i.getItemStack();
                    return s.getType() == spawner.getSpawnerType().getDropItem();
                }).mapToInt(i -> i.getItemStack().getAmount()).sum();

        if (this.maxNearby == -1 || nearbyEntities < this.maxNearby) {
            final ItemStack toDrop = new ItemStack(type.getDropItem(), spawner.getDropAmount() + currentLevel.getDropIncrease());
            w.dropItem(dropSpawnPos, toDrop).setVelocity(new Vector());
        }
    }

    public void spawn() {
        if (active()) {
            return;
        }

        for (SpawnerLevel lvl : spawner.getSpawnerLevels()) { // assigning level up tasks.
            if (lvl.getLevel() == 1) continue;
            final BukkitTask task = new BukkitRunnable() {

                @Override
                public final void run() {
                    currentLevel = lvl;
                }

            }.runTaskLater(game.getPlugin(), lvl.getAfterSeconds() * 20L);
            this.levelUpTasks.add(task);
        }


        final Location spawnStandAt = spawner.getSpawnPosition().toLocation(game.getCachedGameData().getGame());
        if (!spawnStandAt.getWorld().isChunkLoaded(spawnStandAt.getChunk())) {
            spawnStandAt.getWorld().loadChunk(spawnStandAt.getChunk());
        }

        if (!spawner.isInvisible()) {
            this.stand = (ArmorStand) game.getCachedGameData().getGame().spawnEntity(spawnStandAt, EntityType.ARMOR_STAND);
            this.stand.setGravity(false);
            this.stand.setVisible(false);
            this.stand.setCanPickupItems(false);
            this.stand.setCustomNameVisible(true);
            this.stand.setHelmet(type.getHeadItem().clone());

            this.updateNameTask = game.getPlugin().getServer()
                    .getScheduler()
                    .runTaskTimer(game.getPlugin(), () -> this.stand.setCustomName(generateStandName()), 1L, 20L);

            this.animationTask = game.getPlugin().getServer()
                    .getScheduler()
                    .runTaskTimer(game.getPlugin(), () -> stand.teleport(cachedAnimation.move()), 1L, 1L);
        }

        this.dropTask = game.getPlugin().getServer()
                .getScheduler()
                .runTaskTimer(game.getPlugin(), () -> {
                    final long currentTime = now();
                    if (lastDrop == -1L ||
                            ((double) (currentTime - lastDrop) / 1000.0) > (spawner.getDropDelay() - currentLevel.getDecreaseSpawnDelay()) * Math.pow(0.5, (this.dropSpeedRegulator / 100))) {
                        lastDrop = currentTime;
                        drop();
                    }
                }, 1, 5L);

        game.getPlugin()
                .getServer()
                .getScheduler()
                .runTaskTimer(game.getPlugin(), () -> timePassedSinceCreation++, 1L, 20L);
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
            getLevelUpTasks().forEach(BukkitTask::cancel);
            getLevelUpTasks().clear();
            stand.remove();
        }
    }

    public long getTimeUntilNextLevel() {
        final SpawnerLevel currentLevel = this.currentLevel;
        final int nextLevel = currentLevel.getLevel();
        if (spawner.getSpawnerLevels().size() > nextLevel) {
            final SpawnerLevel nextSpawnerLevel = getSpawner().getSpawnerLevels().get(nextLevel);
            final long nextSpawnerLevelAfter = nextSpawnerLevel.getAfterSeconds();
            return nextSpawnerLevelAfter - this.timePassedSinceCreation;
        }
        return -1L;
    }

    /*--------------------------------------------------------------------------------------------------------------*/

    public final int getMaxNearby() {
        return maxNearby;
    }

    public final SpawnerType getType() {
        return type;
    }

    public final Spawner getSpawner() {
        return spawner;
    }

    public final ImmutableTraversableList<Location> getCachedAnimation() {
        return cachedAnimation;
    }

    public final BukkitTask getAnimationTask() {
        return animationTask;
    }

    public final BukkitTask getDropTask() {
        return dropTask;
    }

    public final BukkitTask getUpdateNameTask() {
        return updateNameTask;
    }

    public final ArmorStand getStand() {
        return stand;
    }

    public final ActiveGame getGame() {
        return game;
    }

    public final SpawnerLevel getCurrentLevel() {
        return currentLevel;
    }

    public final long getLastDrop() {
        return lastDrop;
    }

    public final List<BukkitTask> getLevelUpTasks() {
        return levelUpTasks;
    }

    public final void setDropSpeedRegulator(final double dropSpeedRegulator) {
        this.dropSpeedRegulator = dropSpeedRegulator;
    }

    public long getTimePassedSinceCreation() {
        return timePassedSinceCreation;
    }

    public final void setDropAmountRegulator(final int dropAmountRegulator) {
        this.dropAmountRegulator = dropAmountRegulator;
    }
}
