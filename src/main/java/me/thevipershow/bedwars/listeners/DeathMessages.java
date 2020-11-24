package me.thevipershow.bedwars.listeners;

import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

public enum DeathMessages {

    FALL(new DamageCause[]{DamageCause.FALL}, "&7has fallen to death", "&7believed the ground was soft", "&7broke his spine jumping"),
    VOID(new DamageCause[]{DamageCause.VOID}, "&7has &ointentionally &7voided"),
    WATER(new DamageCause[]{DamageCause.DROWNING}, "&7forgot he was not a fish", "&7hold his breath underwater for too long"),
    SUICIDE(new DamageCause[]{DamageCause.SUICIDE}, "&7left this sad world", "&7killed himself"),
    FIRE(new DamageCause[]{DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA}, "&7set himself on fire", "&7got turned into ash", "&7got microwaved perfectly"),
    SUFFOCATE(new DamageCause[]{DamageCause.SUFFOCATION}, "&7didn't remember how to breathe", "&7suffocated to death", "&7could not find enough air to breathe"),
    OTHER(new DamageCause[]{DamageCause.FALLING_BLOCK}, "&7got smashed by a falling block", "&7got crushed into a thin piece of paper"),
    STANDARD(new DamageCause[]{}, "&7has died."),
    SLAIN(new DamageCause[]{DamageCause.ENTITY_ATTACK}, "&7has been slain", "&7has been slashed", "&7got destroyed");

    private final DamageCause[] cause;
    private final String[] messages;
    private final static ThreadLocalRandom rand = ThreadLocalRandom.current();

    DeathMessages(final DamageCause[] cause, final String... messages) {
        this.cause = cause;
        this.messages = messages;
    }

    /**
     * Get the valid causes of this death message.
     * @return The DamageCauses that could've caused this message.
     */
    @NotNull
    public final DamageCause[] getCause() {
        return cause;
    }

    /**
     * All of the possible messages for this type of death.
     * @return The messages.
     */
    @NotNull
    public final String[] getMessages() {
        return messages;
    }

    @NotNull
    public String getRandom() {
        if (messages.length == 1) {
            return messages[0];
        } else {
            return messages[rand.nextInt(0, messages.length)];
        }
    }

    /**
     * Pick a random message from the available one
     * for the current DeathMessage.
     *
     * @param cause The cause to pick the message from.
     * @return A random death message if found, otherwise
     * the default one.
     */
    @NotNull
    public static String getRandomFrom(DamageCause cause) {
        DeathMessages valid = null;
        for (DeathMessages deathMessages : values()) {
            for (DamageCause damageCause : deathMessages.cause) {
                if (damageCause == cause) {
                    valid = deathMessages;
                    break;
                }
            }
        }

        if (valid == null) {
            return STANDARD.messages[0x00];
        }

        return valid.getRandom();
    }
}
