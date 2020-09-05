package me.thevipershow.aussiebedwars.commands.arguments.impl;

import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.commands.arguments.CommandPerformer;
import me.thevipershow.aussiebedwars.commands.arguments.RequiredPermission;
import me.thevipershow.aussiebedwars.commands.arguments.RequiredSender;
import me.thevipershow.aussiebedwars.commands.tasks.VillagerQueueCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredPermission(requiredPerm = "abedwars.admin")
@RequiredSender()
public class SetCommandPerformer extends CommandPerformer {
    public SetCommandPerformer(CommandSender sender, String[] args) {
        super(sender, args);
    }

    @Override
    public void setExecutionLogic() {
        if (!(t instanceof Player)) return;
        if (getArgs().length < 2) return;
        if (!getArgs()[0].equalsIgnoreCase("set")) return;
        final String secondArg = getArgs()[1].toUpperCase();
        switch (secondArg) {
            case "SOLO":
            case "DUO":
            case "QUAD":
                executionLogic = (sender) -> new VillagerQueueCreator((Player) sender, Gamemode.valueOf(secondArg)).create();
                break;
            default:
                break;
        }
    }
}
