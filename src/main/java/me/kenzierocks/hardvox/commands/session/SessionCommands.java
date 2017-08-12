package me.kenzierocks.hardvox.commands.session;

import java.util.function.Consumer;

import me.kenzierocks.hardvox.commands.CommandModule;
import net.minecraft.command.ICommand;

public class SessionCommands implements CommandModule {

    @Override
    public void addCommands(Consumer<ICommand> registerCommand) {
        registerCommand.accept(new SessionFlagCommand());
    }

}
