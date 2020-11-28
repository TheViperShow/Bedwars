package me.thevipershow.bedwars.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.unregisterable.CompassUtilizeUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.ExplosionsUnregisterableListeners;
import me.thevipershow.bedwars.listeners.unregisterable.GoodGamePointUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.NaturalMobSpawnPreventUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.UnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.HungerLossListener;
import me.thevipershow.bedwars.listeners.unregisterable.ItemDegradeListener;
import me.thevipershow.bedwars.listeners.unregisterable.SpawnersMultigiveUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.SpectatorsInteractUnregisterableListeners;
import me.thevipershow.bedwars.listeners.unregisterable.BedDestroyUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.BedwarsPlayerDeathUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.BedwarsPlayerQuitUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.DragonRedirectorListener;
import me.thevipershow.bedwars.listeners.unregisterable.FireballShootUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.GameEntitiesProtectionUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.LobbyUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.MapProtectionUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.PlayerDeathUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.ShopInteractUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.ShopMerchantUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.TeamEliminationUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.TeamWinUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.UpgradeInteractUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.UpgradeMerchantUnregisterableListener;

public enum GameListener {
    // The new listeners:,
    BED_DESTROY(BedDestroyUnregisterableListener.class, RegistrationStage.STARTUP),
    BEDWARS_PLAYER_DEATH(BedwarsPlayerDeathUnregisterableListener.class, RegistrationStage.STARTUP),
    DRAGON_REDIRECTOR(DragonRedirectorListener.class, RegistrationStage.STARTUP),
    FIREBALL_SHOOT(FireballShootUnregisterableListener.class, RegistrationStage.STARTUP),
    MAP_PROTECTION(MapProtectionUnregisterableListener.class, RegistrationStage.STARTUP),
    PLAYER_DEATH(PlayerDeathUnregisterableListener.class, RegistrationStage.STARTUP),
    SHOP_INTERACT(ShopInteractUnregisterableListener.class, RegistrationStage.STARTUP),
    SHOP_MERCHANT(ShopMerchantUnregisterableListener.class, RegistrationStage.STARTUP),
    TEAM_WIN(TeamWinUnregisterableListener.class, RegistrationStage.STARTUP),
    UPGRADE_INTERACT(UpgradeInteractUnregisterableListener.class, RegistrationStage.STARTUP),
    UPGRADE_MERCHANT(UpgradeMerchantUnregisterableListener.class, RegistrationStage.STARTUP),
    TEAM_LOSE(TeamEliminationUnregisterableListener.class, RegistrationStage.STARTUP),
    BEDWARS_PLAYER_QUIT(BedwarsPlayerQuitUnregisterableListener.class, RegistrationStage.STARTUP),
    GAME_ENTITIES_PROTECTION(GameEntitiesProtectionUnregisterableListener.class, RegistrationStage.STARTUP),
    ITEM_DEGRADE(ItemDegradeListener.class, RegistrationStage.STARTUP),
    SPAWNERS_MULTIGIVE(SpawnersMultigiveUnregisterableListener.class, RegistrationStage.STARTUP),
    SPECTATORS_INTERACT(SpectatorsInteractUnregisterableListeners.class, RegistrationStage.STARTUP),
    EXPLOSIONS(ExplosionsUnregisterableListeners.class, RegistrationStage.STARTUP),
    COMPASS(CompassUtilizeUnregisterableListener.class, RegistrationStage.STARTUP),

    NATURAL_MOB_SPAWN_PREVENTION(NaturalMobSpawnPreventUnregisterableListener.class, RegistrationStage.INITIALIZATION),
    HUNGER_LOSS(HungerLossListener.class, RegistrationStage.INITIALIZATION),
    QUEUE(LobbyUnregisterableListener.class, RegistrationStage.INITIALIZATION),

    GOOD_GAME(GoodGamePointUnregisterableListener.class, RegistrationStage.END_GAME);

    public enum RegistrationStage {
        INITIALIZATION, STARTUP, END_GAME;
    }

    private final Class<? extends UnregisterableListener> ownClass;

    private final RegistrationStage registrationStage;

    GameListener(Class<? extends UnregisterableListener> ownClass, RegistrationStage registrationStage) {
        this.ownClass = ownClass;
        this.registrationStage = registrationStage;
    }

    public final RegistrationStage getRegistrationStage() {
        return registrationStage;
    }

    public Class<? extends UnregisterableListener> getOwnClass() {
        return ownClass;
    }

    private static final Map<GameListener, Constructor<?>> constructorsCache = new EnumMap<>(GameListener.class);

    public final UnregisterableListener newInstance(ActiveGame activeGame) {
        Constructor<?> c = constructorsCache.get(this);
        try {
            if (c == null) {
                c = ownClass.getConstructor(ActiveGame.class);
                constructorsCache.put(this, c);
            }
            return (UnregisterableListener) c.newInstance(activeGame);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
