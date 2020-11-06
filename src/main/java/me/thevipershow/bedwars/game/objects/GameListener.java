package me.thevipershow.bedwars.game.objects;

import java.lang.reflect.Constructor;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import me.thevipershow.bedwars.listeners.game.UpgradeInteractListener;
import me.thevipershow.bedwars.listeners.game.UpgradeMerchantListener;
import me.thevipershow.bedwars.listeners.unregisterable.QueueUnregisterableListener;

@Getter
@RequiredArgsConstructor
public enum GameListener {
    BED_BREAK(BedBreakListener.class),
    CHEST_INTERACT(ChestInteractListener.class),
    DRAGON_TARGET(DragonTargetListener.class),
    ENTITY_DAMAGE(EntityDamageListener.class),
    EXPLOSION(ExplosionListener.class),
    GOOD_GAME(GoodGameListener.class),
    HUNGER_LOSS(HungerLossListener.class),
    ITEM_DEGRADE(ItemDegradeListener.class),
    KILL_SOUND(KillSoundListener.class),
    LOBBY_COMPASS(LobbyCompassListener.class),
    MAP_ILLEGAL_MOVEMENTS(MapIllegalMovementsListener.class),
    MAP_PROTECTION(MapProtectionListener.class),
    PLAYER_DEATH_LISTENER(PlayerDeathListener2.class),
    PLAYER_FIREBALL_INTERACT(PlayerFireballInteractListener.class),
    PLAYER_QUIT_DURING_GAME(PlayerQuitDuringGameListener.class),
    PLAYER_SPECTATE_PLAYER(PlayerSpectatePlayerListener.class),
    POTION_MODIFY(PotionModifyListener.class),
    SHOP_INTERACT(ShopInteractListener.class),
    SHOP_MERCHANT(ShopMerchantListener.class),
    SPAWNERS_MULTIGIVE(SpawnersMultigiveListener.class),
    SPECTATORS_INTERACT(SpectatorsInteractListener.class),
    TNT_PLACE(TNTPlaceListener.class),
    UPGRADE_INTERACT(UpgradeInteractListener.class),
    UPGRADE_MERCHANT(UpgradeMerchantListener.class),

    // The new listeners:;
    QUEUE(QueueUnregisterableListener.class);

    private final Class<? extends UnregisterableListener> ownClass;

    private static final Map<GameListener, Constructor<?>> constructorsCache = new EnumMap<>(GameListener.class);

    public final UnregisterableListener newInstance(ActiveGame activeGame) {
        Constructor<?> c = constructorsCache.get(this);
        try {
            if (c == null) {
                c = ownClass.getConstructor(ActiveGame.class);
                constructorsCache.put(this, c);
            }
            return (UnregisterableListener) c.newInstance(activeGame);
        } catch (Exception ignored) {
        }
        return null;
    }
}
