package me.kenzierocks.hardvox.commands.args;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public final class ParserContext {

    public final MinecraftServer server;
    public final ICommandSender sender;
    @Nullable
    public final BlockPos targetPos;
    public final String[] text;

    public ParserContext(MinecraftServer server, ICommandSender sender, BlockPos targetPos, String[] text) {
        this.server = server;
        this.sender = sender;
        this.targetPos = targetPos;
        this.text = text;
    }

    public ArgumentContext contextAt(int index) {
        return new ArgumentContext(server, sender, targetPos, index >= text.length ? "" : text[index]);
    }

}
