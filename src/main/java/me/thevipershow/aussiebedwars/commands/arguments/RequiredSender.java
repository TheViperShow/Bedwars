package me.thevipershow.aussiebedwars.commands.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequiredSender {
    Class<? extends CommandSender>[] allowedSenders() default {Player.class};
}