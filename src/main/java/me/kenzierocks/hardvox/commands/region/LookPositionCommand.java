package me.kenzierocks.hardvox.commands.region;

import java.util.function.BiConsumer;

import com.flowpowered.math.vector.Vector3i;

import me.kenzierocks.hardvox.RayTraceHelper;
import me.kenzierocks.hardvox.commands.HVCommand;
import me.kenzierocks.hardvox.commands.args.CommandArgSet;
import me.kenzierocks.hardvox.commands.args.CommandParser;
import me.kenzierocks.hardvox.session.HVSession;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class LookPositionCommand extends HVCommand {

    private final BiConsumer<HVSession, Vector3i> positionSetter;

    public LookPositionCommand(String name, BiConsumer<HVSession, Vector3i> positionSetter) {
        super(name, CommandParser.init());
        this.positionSetter = positionSetter;
    }

    @Override
    protected void execute(HVSession session, CommandArgSet args) throws CommandException {
        Entity e = session.owner.getCommandSenderEntity();
        if (e == null) {
            throw new CommandException("There is no entity to look with!");
        }
        RayTraceResult result = RayTraceHelper.rayTraceEntity(e, 128);
        if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) {
            throw new CommandException("Not looking at a block!");
        }
        BlockPos position = result.getBlockPos();
        Vector3i vec = new Vector3i(position.getX(), position.getY(), position.getZ());
        positionSetter.accept(session, vec);
    }

}
