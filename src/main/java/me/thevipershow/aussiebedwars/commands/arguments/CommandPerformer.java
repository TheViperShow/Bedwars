package me.thevipershow.aussiebedwars.commands.arguments;

import org.bukkit.command.CommandSender;

public abstract class CommandPerformer extends AbstractPerformer<CommandSender> {
    private final String[] args;

    public CommandPerformer(CommandSender sender, String[] args) {
        super(sender);
        this.args = args;
        setExecutionLogic();
    }

    public String[] getArgs() {
        return args;
    }
}
