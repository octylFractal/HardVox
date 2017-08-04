package me.kenzierocks.hardvox.commands.region;

import java.util.function.BiConsumer;

import com.flowpowered.math.vector.Vector3i;

import me.kenzierocks.hardvox.commands.HVCommand;
import me.kenzierocks.hardvox.commands.args.CommandArgSet;
import me.kenzierocks.hardvox.commands.args.CommandArgument;
import me.kenzierocks.hardvox.commands.args.CommandArguments;
import me.kenzierocks.hardvox.commands.args.CommandParser;
import me.kenzierocks.hardvox.session.HVSession;
import net.minecraft.command.CommandException;
import net.minecraft.util.math.BlockPos;

public class PositionCommand extends HVCommand {

    private static final CommandArgument<Vector3i> POSITION = CommandArguments.position3("pos");

    private final BiConsumer<HVSession, Vector3i> positionSetter;

    public PositionCommand(String name, BiConsumer<HVSession, Vector3i> positionSetter) {
        super(name, CommandParser.init(POSITION).markOptionalStartingWith(POSITION));
        this.positionSetter = positionSetter;
    }

    @Override
    protected void execute(HVSession session, CommandArgSet args) throws CommandException {
        Vector3i vec;
        if (args.hasValue(POSITION)) {
            vec = args.value(POSITION);
        } else {
            BlockPos position = session.owner.getPosition();
            vec = new Vector3i(position.getX(), position.getY(), position.getZ());
        }
        positionSetter.accept(session, vec);
    }

}
