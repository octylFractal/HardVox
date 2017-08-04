package me.kenzierocks.hardvox.commands.args;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public final class ArgumentContext {

    public final MinecraftServer server;
    public final ICommandSender sender;
    @Nullable
    public final BlockPos targetPos;
    public final String text;

    public ArgumentContext(MinecraftServer server, ICommandSender sender, BlockPos targetPos, String text) {
        this.server = server;
        this.sender = sender;
        this.targetPos = targetPos;
        this.text = text;
    }

    public ArgumentContext withText(String text) {
        return new ArgumentContext(server, sender, targetPos, text);
    }

}
