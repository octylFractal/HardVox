package me.kenzierocks.hardvox.session;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.flowpowered.math.vector.Vector3i;

import me.kenzierocks.hardvox.Texts;
import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.net.HardVoxPackets;
import me.kenzierocks.hardvox.net.SelectionMessage;
import me.kenzierocks.hardvox.operation.OperationManager;
import me.kenzierocks.hardvox.operation.TaskManager;
import me.kenzierocks.hardvox.region.Region;
import me.kenzierocks.hardvox.region.chunker.RegionChunker;
import me.kenzierocks.hardvox.region.chunker.RegionChunkers;
import me.kenzierocks.hardvox.region.selector.BoxRegionSelector;
import me.kenzierocks.hardvox.region.selector.RegionSelector;
import me.kenzierocks.hardvox.vector.VecBridge;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class HVSession {

    public final MinecraftServer server;
    public final ICommandSender owner;
    public final OperationManager operationManager;
    public final TaskManager taskManager;
    public final Set<SessionFlag> flags = EnumSet.allOf(SessionFlag.class);
    {
        flags.removeIf(sf -> !sf.onByDefault);
    }
    public World world;
    public RegionSelector<?, ?> regionSelector = new BoxRegionSelector();

    public HVSession(MinecraftServer server, ICommandSender owner) {
        this.server = server;
        this.owner = owner;
        world = owner.getEntityWorld();
        this.taskManager = new TaskManager();
        // op man must be last
        this.operationManager = new OperationManager(this);
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
            sendMessage(Texts.hardVoxMessage(msg));
            maybeSendUpdates();
        }
    }

    public void sendMessage(ITextComponent message) {
        if (owner.sendCommandFeedback()) {
            owner.sendMessage(message);
        }
    }

    public void maybeSendUpdates() {
        if (owner instanceof EntityPlayerMP) {
            SelectionMessage msg = new SelectionMessage().setRegionData(regionSelector.getSelectorInformation());
            HardVoxPackets.INSTANCE.sendTo(msg, (EntityPlayerMP) owner);
        }
    }

    public void performOperationCommand(VectorMap<BlockData> blockMap) throws CommandException {
        performFullRegionCommand(region -> {
            Region r = region.getRegion().get();
            runOperation(r, blockMap).thenAccept(totalOps -> {
                sendMessage(Texts.hardVoxMessage("Operation completed! " + totalOps + " operation(s) performed!"));
            }).exceptionally(e -> {
                Texts.error(this, e);
                return null;
            });
            return "Running operation, please wait...";
        });
    }

    public CompletableFuture<Integer> runOperation(Region region, VectorMap<BlockData> blockMap) {
        region = region.copy();
        RegionChunker.BoundRegionChunker<?> rc = RegionChunkers.forRegion(region).bind(region);
        Vector3i min = VecBridge.toChunk(region.getMinimum());
        Vector3i max = VecBridge.toChunk(region.getMaximum());
        Stream<Vector3i> vectors = IntStream.rangeClosed(min.getX(), max.getX())
                .mapToObj(x -> IntStream.rangeClosed(min.getZ(), max.getZ())
                        .mapToObj(z -> new Vector3i(x, 0, z)))
                .flatMap(Function.identity());
        return operationManager.performOperations(vectors, rc, world, blockMap);
    }

}
