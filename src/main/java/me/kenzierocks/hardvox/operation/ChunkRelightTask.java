package me.kenzierocks.hardvox.operation;

import java.util.BitSet;

import me.kenzierocks.hardvox.HardVoxConfig;
import me.kenzierocks.hardvox.Texts;
import me.kenzierocks.hardvox.session.HVSession;
import me.kenzierocks.hardvox.vector.Masker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkRelightTask extends BaseTask<Void> {

    // 32x32x32
    private static final Masker ITERATION = new Masker(5, 5, 5);
    // 16x0x16
    private static final Masker RELIGHT = new Masker(4, 0, 4);
    // keeps track of all processed blocks, which is 16 on each side
    // we don't track blocks inside our chunk, as we are processing them already
    // so we ignore them by default
    // 32x256x32
    private static final Masker PROC = new Masker(5, 8, 5);

    private final HVSession session;
    private final World world;
    private final ChunkPos chunkPos;
    private final BitSet xzRelightSet = new BitSet(RELIGHT.size());
    private final BitSet alreadyProcessedSet = new BitSet(PROC.size());
    private int workingBlockIndex;
    private int iterationIndex;

    public ChunkRelightTask(HVSession session, World world, ChunkPos chunkPos) {
        this.session = session;
        this.world = world;
        this.chunkPos = chunkPos;
    }

    @Override
    protected void onFirstTick() {
        if (HardVoxConfig.messages.relightTask) {
            session.sendMessage(Texts.hardVoxMessage("Starting chunk relight task at (" + chunkPos.x + "," + chunkPos.z + ")."));
        }
    }

    @Override
    protected void onTick() {
        Chunk chunk = world.getChunkFromChunkCoords(chunkPos.x, chunkPos.z);
        int chunkLoX = chunkPos.getXStart();
        int chunkLoZ = chunkPos.getZStart();
        int chunkHiX = chunkPos.getXEnd();
        int chunkHiZ = chunkPos.getZEnd();
        for (int i = 0; i < HardVoxConfig.operations.delayedLightingBlocksPerTick; i++) {
            if (iterationIndex >= ITERATION.size()) {
                iterationIndex = 0;
                workingBlockIndex++;
                if (HardVoxConfig.messages.relightTask) {
                    session.sendMessage(Texts.hardVoxMessage("CRT: Relight " + workingBlockIndex + "/" + Masker.CHUNK.size()));
                }
            }
            if (workingBlockIndex >= Masker.CHUNK.size()) {
                complete(null);
                if (HardVoxConfig.messages.relightTask) {
                    session.sendMessage(Texts.hardVoxMessage("Finished chunk relight task at (" + chunkPos.x + "," + chunkPos.z + ")."));
                }
                return;
            }
            int wbX = Masker.CHUNK.xFromIndex(workingBlockIndex) + chunkLoX;
            int wbY = Masker.CHUNK.yFromIndex(workingBlockIndex);
            int wbZ = Masker.CHUNK.zFromIndex(workingBlockIndex) + chunkLoZ;
            session.sendMessage(Texts.hardVoxMessage(wbX + "," + wbY + "," + wbZ));
            int iX = ITERATION.xFromIndex(iterationIndex) + wbX - 16;
            int iY = ITERATION.yFromIndex(iterationIndex) + wbY - 16;
            int iZ = ITERATION.zFromIndex(iterationIndex) + wbZ - 16;

            // only need to check this once per WB
            if (iterationIndex == 0) {
                int xzRelightIndex = RELIGHT.index(wbX & 15, 0, wbZ & 15);
                if (wbY == 255 && !xzRelightSet.get(xzRelightIndex)) {
                    xzRelightSet.set(xzRelightIndex);
                    chunk.relightBlock(wbX & 15, wbY, wbZ & 15);
                    continue;
                }
            }

            boolean process = false;
            if (iY < 0 || iY > 255) {
                // don't process, it's out of range
            } else if (chunkLoX > iX || iX > chunkHiX || chunkLoZ > iZ || iZ > chunkHiZ) {
                // outside chunk, check process set
                int procIndex = PROC.index(iX - chunkLoX + 16, iY, iX - chunkLoZ + 16);
                if (!alreadyProcessedSet.get(procIndex)) {
                    alreadyProcessedSet.set(procIndex);
                    process = true;
                }
            } else if (wbX == iX && wbY == iY && wbZ == iZ) {
                // process our block
                process = true;
            }
            if (!process) {
                iterationIndex++;
                i--;
                continue;
            }
            world.checkLightFor(EnumSkyBlock.BLOCK, new BlockPos(iX, iY, iZ));
        }
    }

}
