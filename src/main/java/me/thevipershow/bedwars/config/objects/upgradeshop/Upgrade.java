package me.thevipershow.bedwars.config.objects.upgradeshop;

public interface Upgrade {
    /**
     * Get the associated type with this upgrade.
     * @return The type.
     */
    UpgradeType getType();

    /**
     * Return true if this is staged.
     * @return Has stages?
     */
    boolean hasStages();

    /**
     * Get the slot this upgrade is spawned in.
     * @return The inventory slot.
     */
    int getSlot();
}
