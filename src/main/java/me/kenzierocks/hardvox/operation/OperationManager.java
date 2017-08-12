package me.kenzierocks.hardvox.operation;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.chunker.RegionChunker.BoundRegionChunker;
import me.kenzierocks.hardvox.session.HVSession;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.world.World;

public class OperationManager {

    final HVSession parent;
    private final List<Operation> operations;

    public OperationManager(HVSession parent) {
        this.parent = parent;
        ImmutableList.Builder<Operation> ops = ImmutableList.builder();

        ops.add(new OpSetBasicBlock());
        ops.add(new OpBlockUpdates());
        ops.add(new OpLightUpdates(parent));
        ops.add(new OpSendChunks());

        operations = ops.build();
    }

    /*
     * TODO Optimize using chunk-dependencies
     * 
     * We know that each operation operates on a single chunk at a time. We also
     * know that each operation only depends on the nearby chunks. There should
     * be a way to optimize the operations to run on the closest chunks FIRST.
     * Instead of iterating over all chunks for every operation.
     * 
     * Imagine you have C1|C2|C3|C4, instead of running operations O1,O2,O3 like
     * this:
     * 
     * <pre> O1{C1,C2,C3,C4},O2{C1,C2,C3,C4},O3{C1,C2,C3,C4},O4{C1,C2,C3,C4}
     * </pre>
     * 
     * We can do it like this:
     * 
     * <pre>O1{C1,C2},O2{C1},O1{C3},O2{C2},O3{C1}...</pre>
     * 
     * Notice how we operate on the closest chunks first, ensuring that all
     * blocks that could touch are already placed.
     */

    public CompletableFuture<Integer> performOperations(Stream<Vector3i> chunkLocations, BoundRegionChunker<?> regionChunker, World world,
            VectorMap<BlockData> blockMap) {
        List<Operation> operations = this.operations.stream().filter(op -> op.isEnabled(parent)).collect(toImmutableList());
        return parent.taskManager.submit(new OMTask(parent, operations, chunkLocations, regionChunker, world, blockMap));
    }

}
