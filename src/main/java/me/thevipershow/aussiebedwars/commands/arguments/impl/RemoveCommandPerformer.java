package me.thevipershow.aussiebedwars.commands.arguments.impl;

import me.thevipershow.aussiebedwars.commands.arguments.CommandPerformer;
import me.thevipershow.aussiebedwars.commands.arguments.RequiredPermission;
import me.thevipershow.aussiebedwars.commands.arguments.RequiredSender;
import me.thevipershow.aussiebedwars.commands.tasks.VillageQueueRemover;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredPermission(requiredPerm = "abedwars.admin")
@RequiredSender()
public class RemoveCommandPerformer extends CommandPerformer {
    public RemoveCommandPerformer(CommandSender sender, String[] args) {
        super(sender, args);
    }

    @Override
    protected void setExecutionLogic() {
        if (!(t instanceof Player)) return;
        if (getArgs().length != 1) return;
        if (!getArgs()[0].equalsIgnoreCase("remove")) return;
        executionLogic = (t) -> new VillageQueueRemover((Player) t).perform();
    }
}
