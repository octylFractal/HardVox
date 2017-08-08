package me.kenzierocks.hardvox.commands.region;

import java.util.Iterator;
import java.util.OptionalInt;
import java.util.stream.Stream;

import com.google.common.collect.Iterators;

import me.kenzierocks.hardvox.commands.HVCommand;
import me.kenzierocks.hardvox.commands.args.ArgumentContext;
import me.kenzierocks.hardvox.commands.args.BaseArg;
import me.kenzierocks.hardvox.commands.args.CommandArgSet;
import me.kenzierocks.hardvox.commands.args.CommandArgument;
import me.kenzierocks.hardvox.commands.args.CommandArguments;
import me.kenzierocks.hardvox.commands.args.CommandParser;
import me.kenzierocks.hardvox.region.selector.RegionSelector;
import me.kenzierocks.hardvox.session.HVSession;
import me.kenzierocks.hardvox.vector.VecBridge;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

public class ExpandCommand extends HVCommand {

    private static final class ExpandSpecialArg extends BaseArg<OptionalInt> {

        private final CommandArgument<Integer> delegate;

        protected ExpandSpecialArg(String name) {
            super(name);
            this.delegate = CommandArguments.integer(name);
        }

        @Override
        public OptionalInt convert(ArgumentContext context) {
            if (context.text.equals("vert")) {
                return OptionalInt.empty();
            }
            return OptionalInt.of(delegate.convert(context));
        }

        @Override
        public boolean validText(ArgumentContext context) {
            return "vert".startsWith(context.text) || delegate.validText(context);
        }

        @Override
        public Iterator<String> getCompletions(ArgumentContext context) {
            return Iterators.concat(delegate.getCompletions(context),
                    Stream.of("vert").filter(s -> s.startsWith(context.text)).iterator());
        }

    }

    private static final CommandArgument<OptionalInt> AMOUNT = new ExpandSpecialArg("vert|amount");
    private static final CommandArgument<EnumFacing> DIRECTION = CommandArguments.direction("direction");

    public ExpandCommand(String name) {
        super(name, CommandParser.init(AMOUNT, DIRECTION).markOptionalStartingWith(DIRECTION));
    }

    @Override
    protected String describeArguments(ICommandSender sender) {
        return "<vert>|<amount> [direction]";
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
        OptionalInt amt = args.value(AMOUNT);
        if (amt.isPresent()) {
            session.performFullRegionCommand(sel -> sel.expandRegion(facing, amt.getAsInt()));
        } else {
            session.performFullRegionCommand(RegionSelector::expandRegionVertically);
        }
    }

}
