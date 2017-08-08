package me.kenzierocks.hardvox.commands.op;

import java.util.concurrent.CompletableFuture;

import me.kenzierocks.hardvox.Texts;
import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.commands.HVCommand;
import me.kenzierocks.hardvox.commands.UncheckedWUE;
import me.kenzierocks.hardvox.commands.args.CommandArgSet;
import me.kenzierocks.hardvox.commands.args.CommandArgument;
import me.kenzierocks.hardvox.commands.args.CommandArguments;
import me.kenzierocks.hardvox.commands.args.CommandParser;
import me.kenzierocks.hardvox.session.HVSession;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.command.CommandException;

public class SetBlockCommand extends HVCommand {

    private static final CommandArgument<Boolean> FLAG_TILE_ENTITY = CommandArguments.flag("t");
    private static final CommandArgument<VectorMap<BlockData>> BLOCK = CommandArguments.blockPattern("block");

    public SetBlockCommand() {
        super("set", CommandParser.init(FLAG_TILE_ENTITY, BLOCK));
    }

    @Override
    protected void execute(HVSession session, CommandArgSet args) throws CommandException {
        computeBlockData(args).thenAccept(vm -> {
            session.server.addScheduledTask(() -> {
                try {
                    setBlock(session, vm);
                } catch (Throwable e) {
                    Texts.error(session.owner, e);
                }
            });
        }).exceptionally(t -> {
            session.server.addScheduledTask(() -> {
                Texts.error(session.owner, t);
            });
            return null;
        });
    }

    private CompletableFuture<VectorMap<BlockData>> computeBlockData(CommandArgSet args) {
        if (args.value(FLAG_TILE_ENTITY)) {
            // TODO implement client side TE selection
            throw new UncheckedWUE("Client side TE selection not implemented!");
        }

        return CompletableFuture.completedFuture(args.value(BLOCK));
    }

    private void setBlock(HVSession session, VectorMap<BlockData> blockMap) throws CommandException {
        session.performOperationCommand(blockMap);
    }

}
