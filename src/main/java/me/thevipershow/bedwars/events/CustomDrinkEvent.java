package me.thevipershow.bedwars.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

public final class CustomDrinkEvent extends PlayerEvent {

    private final Potion drankPotion;
    private final ItemStack drankPotionItem;
    public final static HandlerList handlersList = new HandlerList();

    public CustomDrinkEvent(final Player who, Potion drankPotion, ItemStack drankPotionItem) {
        super(who);
        this.drankPotion = drankPotion;
        this.drankPotionItem = drankPotionItem;
    }

    @Override
    public final HandlerList getHandlers() {
        return handlersList;
    }

    public static HandlerList getHandlerList() {
        return handlersList;
    }

    public final Potion getDrankPotion() {
        return drankPotion;
    }

    public final ItemStack getDrankPotionItem() {
        return drankPotionItem;
    }
}
