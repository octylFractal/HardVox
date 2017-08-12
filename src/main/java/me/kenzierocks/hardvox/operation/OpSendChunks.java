package me.kenzierocks.hardvox.operation;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.Region;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionIterator;
import me.kenzierocks.hardvox.vector.MutableVectorMap;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class OpSendChunks implements Operation {

    private static final int SPCD_SEND_ALL = 65535;

    @Override
    public String getName() {
        return "Send Chunks";
    }

    @Override
    public int performOperation(Region region, PositionIterator chunk, World world, Chunk c, VectorMap<BlockData> blockMap,
            MutableVectorMap<IBlockState> hitStore) {
        // send the whole chunk again
        PlayerChunkMapEntry entry = ((WorldServer) world).getPlayerChunkMap().getEntry(c.x, c.z);
        if (entry != null) {
            entry.sendPacket(new SPacketChunkData(c, SPCD_SEND_ALL));
        }
        return 1;
    }

}
