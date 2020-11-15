package me.thevipershow.bedwars.game.objects;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.config.objects.UpgradeItem;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.bedwars.game.upgrades.ActiveHealPool;
import org.bukkit.entity.Player;

public abstract class TeamData<T> {

    public TeamData(Gamemode gamemode, PlayerMapper playerMapper) {
        this.gamemode = gamemode;
        this.playerMapper = playerMapper;
        final Map<UpgradeType, Integer> map = new EnumMap<>(UpgradeType.class);
        for (final UpgradeType value : UpgradeType.values()) {
            map.put(value, 0);
        }
        this.upgradesShopLevelsMap = map;
    }

    private T data;
    private TeamStatus status = TeamStatus.BED_EXISTS;
    private final Map<UpgradeType, Integer> upgradesShopLevelsMap;
    private ActiveHealPool activeHealPool;
    private final Gamemode gamemode;
    private final PlayerMapper playerMapper;

    public abstract void add(Player player);

    public final ActiveHealPool getActiveHealPool() {
        return activeHealPool;
    }

    public final void setActiveHealPool(ActiveHealPool activeHealPool) {
        this.activeHealPool = activeHealPool;
    }

    public abstract void perform(Consumer<? super BedwarsPlayer> consumer);



    public abstract Set<BedwarsPlayer> getAll();

    public final TeamStatus getStatus() {
        return status;
    }

    public final void setData(T data) {
        this.data = data;
    }

    public final void setStatus(TeamStatus status) {
        this.status = status;
    }

    public final void increaseLevel(UpgradeType upgradeType) {
        this.upgradesShopLevelsMap.computeIfPresent(upgradeType, ($, v) -> v++);
    }

    public final T getData() {
        return data;
    }

    public abstract String getStatusCharacter();

    public final Gamemode getGamemode() {
        return gamemode;
    }

    public final PlayerMapper getPlayerMapper() {
        return playerMapper;
    }

    public final int getUpgradeLevel(UpgradeType type) {
        return this.upgradesShopLevelsMap.get(type);
    }

    public final Map<UpgradeType, Integer> getUpgradesShopLevelsMap() {
        return upgradesShopLevelsMap;
    }
}
