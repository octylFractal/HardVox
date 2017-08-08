package me.kenzierocks.hardvox.commands.op;

import java.util.function.Consumer;

import me.kenzierocks.hardvox.commands.CommandModule;
import net.minecraft.command.ICommand;

public class OperationCommands implements CommandModule {

    @Override
    public void addCommands(Consumer<ICommand> registerCommand) {
        registerCommand.accept(new SetBlockCommand());
    }

}
