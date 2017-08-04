package me.kenzierocks.hardvox.commands.region;

import me.kenzierocks.hardvox.VecBridge;
import me.kenzierocks.hardvox.commands.HVCommand;
import me.kenzierocks.hardvox.commands.args.CommandArgSet;
import me.kenzierocks.hardvox.commands.args.CommandArgument;
import me.kenzierocks.hardvox.commands.args.CommandArguments;
import me.kenzierocks.hardvox.commands.args.CommandParser;
import me.kenzierocks.hardvox.session.HVSession;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

public class ShiftCommand extends HVCommand {

    private static final CommandArgument<Integer> AMOUNT = CommandArguments.integer("amount");
    private static final CommandArgument<EnumFacing> DIRECTION = CommandArguments.direction("direction");

    public ShiftCommand(String name) {
        super(name, CommandParser.init(AMOUNT, DIRECTION).markOptionalStartingWith(DIRECTION));
    }

    @Override
    protected void execute(HVSession session, CommandArgSet args) throws CommandException {
        EnumFacing facing;
        if (args.hasValue(DIRECTION)) {
            facing = args.value(DIRECTION);
        } else if (session.owner.getCommandSenderEntity() != null) {
            Entity e = session.owner.getCommandSenderEntity();
            facing = VecBridge.facingFromLook(e);
        } else {
            throw new CommandException("A direction must be provided, since the command was not excuted by an entity.");
        }
        int amt = args.value(AMOUNT);
        session.performFullRegionCommand(sel -> sel.shiftRegion(facing, amt));
    }

}
