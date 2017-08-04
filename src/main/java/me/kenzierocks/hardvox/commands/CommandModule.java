package me.kenzierocks.hardvox.commands;

import java.util.function.Consumer;

import net.minecraft.command.ICommand;

public interface CommandModule {
    void addCommands(Consumer<ICommand> registerCommand);
}
