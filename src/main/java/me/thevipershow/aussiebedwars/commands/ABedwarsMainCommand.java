package me.thevipershow.aussiebedwars.commands;

import me.thevipershow.aussiebedwars.commands.arguments.SelfRegisteringCommand;
import me.thevipershow.aussiebedwars.commands.arguments.impl.RemoveCommandPerformer;
import me.thevipershow.aussiebedwars.commands.arguments.impl.SetCommandPerformer;

public class ABedwarsMainCommand extends SelfRegisteringCommand {

    public ABedwarsMainCommand() {
        super("abedwars",
                "AussieBedwars main command.",
                "",
                SetCommandPerformer.class, RemoveCommandPerformer.class);
    }

}
