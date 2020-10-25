package me.thevipershow.bedwars.config.objects.upgradeshop;

import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.AlarmTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.BlindnessAndPoisonTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.CounterOffensiveTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.MinerFatigueTrap;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class TrapUpgrades implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    private final AlarmTrap alarmTrap;
    private final BlindnessAndPoisonTrap blindnessAndPoisonTrap;
    private final CounterOffensiveTrap counterOffensiveTrap;
    private final MinerFatigueTrap minerFatigueTrap;
    private final String itemName;
    private final List<String> lore;
    private final int slot;
    private final Material material;

    private ItemStack fancyItemStack = null;

    public TrapUpgrades(AlarmTrap alarmTrap, BlindnessAndPoisonTrap blindnessAndPoisonTrap, CounterOffensiveTrap counterOffensiveTrap, MinerFatigueTrap minerFatigueTrap, String itemName, List<String> lore, int slot, Material material) {
        this.alarmTrap = alarmTrap;
        this.blindnessAndPoisonTrap = blindnessAndPoisonTrap;
        this.counterOffensiveTrap = counterOffensiveTrap;
        this.minerFatigueTrap = minerFatigueTrap;
        this.itemName = itemName;
        this.lore = lore;
        this.slot = slot;
        this.material = material;
    }

    public static TrapUpgrades deserialize(final Map<String, Object> map) {
        final String itemName = (String) map.get(AllStrings.ITEM_NAME.get());
        final int slot = (int) map.get(AllStrings.SLOT.get());
        final Material material = Material.valueOf((String) map.get(AllStrings.MATERIAL.get()));
        final List<String> lore = (List<String>) map.get(AllStrings.LORE.get());

        final AlarmTrap alarmTrap = AlarmTrap.deserialize((Map<String, Object>) map.get(AllStrings.ALARM.get()));
        final BlindnessAndPoisonTrap blindnessAndPoisonTrap = BlindnessAndPoisonTrap.deserialize((Map<String, Object>) map.get(AllStrings.BLINDNESS_POISON.get()));
        final CounterOffensiveTrap counterOffensiveTrap = CounterOffensiveTrap.deserialize((Map<String, Object>) map.get(AllStrings.COUNTER_OFFENSIVE.get()));
        final MinerFatigueTrap minerFatigueTrap = MinerFatigueTrap.deserialize((Map<String, Object>) map.get(AllStrings.MINER_FATIGUE.get()));

        return new TrapUpgrades(alarmTrap, blindnessAndPoisonTrap, counterOffensiveTrap, minerFatigueTrap, itemName, lore, slot, material);
    }

    public AlarmTrap getAlarmTrap() {
        return alarmTrap;
    }

    public BlindnessAndPoisonTrap getBlindnessAndPoisonTrap() {
        return blindnessAndPoisonTrap;
    }

    public CounterOffensiveTrap getCounterOffensiveTrap() {
        return counterOffensiveTrap;
    }

    public MinerFatigueTrap getMinerFatigueTrap() {
        return minerFatigueTrap;
    }

    public String getItemName() {
        return itemName;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getSlot() {
        return slot;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack getFancyItemStack() {
        if (this.fancyItemStack == null) {
            final ItemStack i = new ItemStack(this.material);
            final ItemMeta m = i.getItemMeta();
            m.setDisplayName(this.itemName);
            m.setLore(this.lore);
            i.setItemMeta(m);
            this.fancyItemStack = i;
        }
        return fancyItemStack.clone();
    }
}
