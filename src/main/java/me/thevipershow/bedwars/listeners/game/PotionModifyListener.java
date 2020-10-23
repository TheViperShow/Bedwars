package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.config.objects.PotionItem;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

public final class PotionModifyListener extends UnregisterableListener {

    public static class CustomDrinkEvent extends PlayerEvent {

        private final Potion drankPotion;
        private final ItemStack drankPotionItem;
        public final static HandlerList handlersList = new HandlerList();

        public CustomDrinkEvent(final Player who, Potion drankPotion, ItemStack drankPotionItem) {
            super(who);
            this.drankPotion = drankPotion;
            this.drankPotionItem = drankPotionItem;
        }

        @Override
        public HandlerList getHandlers() {
            return handlersList;
        }

        public static HandlerList getHandlerList() {
            return handlersList;
        }

        public Potion getDrankPotion() {
            return drankPotion;
        }

        public ItemStack getDrankPotionItem() {
            return drankPotionItem;
        }
    }

    private final ActiveGame activeGame;

    public PotionModifyListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler()
    public void onPlayerInteract(final PlayerInteractEvent event) {

        final Player p = event.getPlayer();
        if (!p.getWorld().equals(activeGame.getAssociatedWorld())) {
            return;
        }

        if (p.getItemInHand() == null || p.getItemInHand().getType() != Material.POTION) {
            return;
        }

        final Action act = event.getAction();

        if (act != Action.RIGHT_CLICK_BLOCK && act != Action.RIGHT_CLICK_AIR) {
            return;
        }

        final ItemStack itemHand = p.getItemInHand().clone();
        final Potion pot = Potion.fromItemStack(itemHand);
        final int itemSlot = p.getInventory().getHeldItemSlot();

        activeGame.getPlugin().getServer().getScheduler()
                .runTaskLater(activeGame.getPlugin(), () -> {
                    if (p.getInventory().getHeldItemSlot() == itemSlot && p.getItemInHand().getType() == Material.GLASS_BOTTLE) {
                        final CustomDrinkEvent drinkEvent = new CustomDrinkEvent(p, pot, itemHand);
                        activeGame.getPlugin().getServer().getPluginManager().callEvent(drinkEvent);
                    }
                }, 33L);

    }

    @EventHandler(ignoreCancelled = true)
    public void onCustomDrink(final CustomDrinkEvent event) {
        if (!activeGame.isHasStarted()) {
            return;
        }
        final Player player = event.getPlayer();

        for (PotionItem potionItem : activeGame.getBedwarsGame().getShop().getPotionItem()) {
            if (potionItem.getType().equals(event.getDrankPotion().getType().getEffectType())) {
                player.addPotionEffect(new PotionEffect(potionItem.getType(), potionItem.getLength() * 20, potionItem.getLevel()), true);
                activeGame.hidePlayer(player);
                activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), () -> activeGame.showPlayer(player), potionItem.getLength() * 20L);
                break;
            }
        }
    }
}
