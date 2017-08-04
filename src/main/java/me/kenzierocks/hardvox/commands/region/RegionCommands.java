package me.kenzierocks.hardvox.commands.region;

import java.util.function.Consumer;

import com.flowpowered.math.vector.Vector3i;

import me.kenzierocks.hardvox.commands.CommandModule;
import me.kenzierocks.hardvox.session.HVSession;
import net.minecraft.command.ICommand;

public class RegionCommands implements CommandModule {

    @Override
    public void addCommands(Consumer<ICommand> registerCommand) {
        registerCommand.accept(new PositionCommand("pos1", this::setPositionOne));
        registerCommand.accept(new PositionCommand("pos2", this::setPositionTwo));
        registerCommand.accept(new LookPositionCommand("hpos1", this::setPositionOne));
        registerCommand.accept(new LookPositionCommand("hpos2", this::setPositionTwo));
        registerCommand.accept(new ShiftCommand("shift"));
        registerCommand.accept(new ExpandCommand("expand"));
        registerCommand.accept(new ContractCommand("contract"));
    }

    private void setPositionOne(HVSession session, Vector3i pos) {
        session.performRegionCommand(sel -> sel.selectPrimary(pos));
    }

    private void setPositionTwo(HVSession session, Vector3i pos) {
        session.performRegionCommand(sel -> sel.selectSecondary(pos));
    }

}
