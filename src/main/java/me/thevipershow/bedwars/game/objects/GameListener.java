package me.thevipershow.bedwars.game.objects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import me.thevipershow.bedwars.listeners.game.BedBreakListener;
import me.thevipershow.bedwars.listeners.game.ChestInteractListener;
import me.thevipershow.bedwars.listeners.game.DragonTargetListener;
import me.thevipershow.bedwars.listeners.game.EntityDamageListener;
import me.thevipershow.bedwars.listeners.game.ExplosionListener;
import me.thevipershow.bedwars.listeners.game.GoodGameListener;
import me.thevipershow.bedwars.listeners.game.HungerLossListener;
import me.thevipershow.bedwars.listeners.game.ItemDegradeListener;
import me.thevipershow.bedwars.listeners.game.KillSoundListener;
import me.thevipershow.bedwars.listeners.game.LobbyCompassListener;
import me.thevipershow.bedwars.listeners.game.MapIllegalMovementsListener;
import me.thevipershow.bedwars.listeners.game.MapProtectionListener;
import me.thevipershow.bedwars.listeners.game.PlayerDeathListener2;
import me.thevipershow.bedwars.listeners.game.PlayerFireballInteractListener;
import me.thevipershow.bedwars.listeners.game.PlayerQuitDuringGameListener;
import me.thevipershow.bedwars.listeners.game.PlayerSpectatePlayerListener;
import me.thevipershow.bedwars.listeners.game.PotionModifyListener;
import me.thevipershow.bedwars.listeners.game.ShopInteractListener;
import me.thevipershow.bedwars.listeners.game.ShopMerchantListener;
import me.thevipershow.bedwars.listeners.game.SpawnersMultigiveListener;
import me.thevipershow.bedwars.listeners.game.SpectatorsInteractListener;
import me.thevipershow.bedwars.listeners.game.TNTPlaceListener;
import me.thevipershow.bedwars.listeners.unregisterable.BedwarsPlayerDeathUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.LobbyUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.MapProtectionUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.PlayerDeathUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.ShopInteractUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.ShopMerchantUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.UpgradeInteractUnregisterableListener;
import me.thevipershow.bedwars.listeners.unregisterable.UpgradeMerchantUnregisterableListener;

public enum GameListener {

    // The new listeners:,
    UPGRADE_INTERACT(UpgradeInteractUnregisterableListener.class, RegistrationStage.STARTUP),
    UPGRADE_MERCHANT(UpgradeMerchantUnregisterableListener.class, RegistrationStage.STARTUP),
    SHOP_INTERACT(ShopInteractUnregisterableListener.class, RegistrationStage.STARTUP),
    SHOP_MERCHANT(ShopMerchantUnregisterableListener.class, RegistrationStage.STARTUP),
    PLAYER_DEATH(PlayerDeathUnregisterableListener.class, RegistrationStage.STARTUP),
    MAP_PROTECTION(MapProtectionUnregisterableListener.class, RegistrationStage.STARTUP),
    BEDWARS_PLAYER_DEATH(BedwarsPlayerDeathUnregisterableListener.class, RegistrationStage.STARTUP),

    QUEUE(LobbyUnregisterableListener.class, RegistrationStage.INITIALIZATION);

    public enum RegistrationStage {
        INITIALIZATION, STARTUP
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
