package me.thevipershow.aussiebedwars.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandsManager {
    private static CommandsManager instance = null;
    private final JavaPlugin plugin;
    private CommandMap commandMap = null;

    private CommandsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setCommandMap();
    }

    public static CommandsManager getInstance(JavaPlugin plugin) {
        if (instance == null)
            instance = new CommandsManager(plugin);
        return instance;
    }

    public final void setCommandMap() {
        if (commandMap == null) {
            final Class<? extends Server> serverClass = Bukkit.getServer().getClass();
            try {
                final Field commandMapField = serverClass.getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public final void registerAll() {
        for (Commands commands : Commands.values()) {
            final Class<? extends Command> clazz = commands.clazz;
            try {
                Constructor<? extends Command> constructor = clazz.getConstructor();
                commandMap.register("abedwars", constructor.newInstance());
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private enum Commands {
        MAIN_COMMAND(ABedwarsMainCommand.class);

        private final Class<? extends Command> clazz;

        Commands(final Class<? extends Command> clazz) {
            this.clazz = clazz;
        }
    }

    public CommandMap getCommandMap() {
        return commandMap;
    }
}
