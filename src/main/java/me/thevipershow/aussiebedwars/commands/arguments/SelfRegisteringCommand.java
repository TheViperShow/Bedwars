package me.thevipershow.aussiebedwars.commands.arguments;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.thevipershow.aussiebedwars.AussieBedwars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class SelfRegisteringCommand extends Command {

    @SafeVarargs
    protected SelfRegisteringCommand(String name,
                                     String description,
                                     String usageMessage,
                                     Class<? extends CommandPerformer>... registeredArgs) {
        super(name, description, usageMessage, Collections.emptyList());
        for (final Class<? extends CommandPerformer> clazz : registeredArgs) {
            registerArgument(clazz);
        }
    }

    private static Constructor<? extends CommandPerformer> getConstructor(final Class<? extends CommandPerformer> clazz) {
        try {
            return clazz.getDeclaredConstructor(CommandSender.class, String[].class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean registerArgument(final Class<? extends CommandPerformer> clazz) {
        final Constructor<? extends CommandPerformer> constructor = getConstructor(clazz);
        if (constructor != null) {
            this.constructorMap.put(clazz, constructor);
            return true;
        }
        return false;
    }

    public boolean unregisterArgument(final Class<? extends CommandPerformer> clazz) {
        return this.constructorMap.remove(clazz) != null;
    }

    private static boolean buildAndPerform(final Constructor<? extends CommandPerformer> constructor,
                                           final CommandSender sender,
                                           final String[] args) {
        try {
            CommandPerformer commandPerformer = constructor.newInstance(sender, args);
            return commandPerformer.perform();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private final static Class<RequiredPermission> REQUIRED_PERMISSION_CLASS = RequiredPermission.class;
    private final static Class<RequiredSender> REQUIRED_SENDER_CLASS = RequiredSender.class;

    private final Map<Class<? extends CommandPerformer>, Constructor<? extends CommandPerformer>> constructorMap = new HashMap<>();

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        boolean missingPermission = false;
        boolean wrongSender = false;
        for (final Map.Entry<Class<? extends CommandPerformer>, Constructor<? extends CommandPerformer>> entry : constructorMap.entrySet()) {
            Class<? extends CommandPerformer> clazz = entry.getKey();
            if (clazz.isAnnotationPresent(REQUIRED_PERMISSION_CLASS)) {
                RequiredPermission permAnnotation = clazz.getAnnotation(REQUIRED_PERMISSION_CLASS);
                if (!sender.hasPermission(permAnnotation.requiredPerm())) {
                    missingPermission = true;
                    continue;
                }
            }
            if (clazz.isAnnotationPresent(REQUIRED_SENDER_CLASS)) {
                RequiredSender requiredSender = clazz.getAnnotation(REQUIRED_SENDER_CLASS);
                boolean found = false;
                for (final Class<? extends CommandSender> allowedSender : requiredSender.allowedSenders()) {
                    if (allowedSender.isInstance(sender)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    wrongSender = true;
                    continue;
                }
            }
            boolean successfullyPerformed = buildAndPerform(entry.getValue(), sender, args);
            if (successfullyPerformed) {
                return true;
            }
        }
        if (missingPermission) {
            sender.sendMessage(AussieBedwars.PREFIX + "§eYou are missing permissions to do this.");
        }
        if (wrongSender) {
            sender.sendMessage(AussieBedwars.PREFIX + "§eYou cannot use this command from here.");
        }
        return true;
    }
}
