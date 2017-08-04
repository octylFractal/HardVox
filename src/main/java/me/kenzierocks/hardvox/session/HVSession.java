package me.kenzierocks.hardvox.session;

import java.util.function.Function;

import me.kenzierocks.hardvox.Texts;
import me.kenzierocks.hardvox.net.HardVoxPackets;
import me.kenzierocks.hardvox.net.SelectionMessage;
import me.kenzierocks.hardvox.region.selector.BoxRegionSelector;
import me.kenzierocks.hardvox.region.selector.RegionSelector;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class HVSession {

    public final MinecraftServer server;
    public final ICommandSender owner;
    public RegionSelector<?, ?> regionSelector = new BoxRegionSelector();

    public HVSession(MinecraftServer server, ICommandSender owner) {
        this.server = server;
        this.owner = owner;
    }

    public void performFullRegionCommand(Function<RegionSelector<?, ?>, String> commandAction) throws CommandException {
        if (!regionSelector.isRegionDefined()) {
            throw new CommandException("This command can only be used on defined selections.");
        }
        performRegionCommand(commandAction);
    }

    public void performRegionCommand(Function<RegionSelector<?, ?>, String> commandAction) {
        String msg = commandAction.apply(regionSelector);
        if (!msg.isEmpty()) {
            owner.sendMessage(Texts.hardVoxMessage(msg));
            maybeSendUpdates();
        }
    }

    public void maybeSendUpdates() {
        if (owner instanceof EntityPlayerMP) {
            SelectionMessage msg = new SelectionMessage().setRegionData(regionSelector.getSelectorInformation());
            HardVoxPackets.INSTANCE.sendTo(msg, (EntityPlayerMP) owner);
        }
    }

}
