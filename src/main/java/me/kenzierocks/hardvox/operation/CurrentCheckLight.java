package me.kenzierocks.hardvox.operation;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * Re-implementation of
 * {@link World#checkLight(net.minecraft.util.math.BlockPos)}, to improve speed.
 */
final class CurrentCheckLight {

    // see World#lightUpdateBlockList
    private static final ThreadLocal<int[]> localLUBL = ThreadLocal.withInitial(() -> new int[32768]);

    static void checkLight(Chunk c, BlockPos pos) {
        new CurrentCheckLight(c, pos).checkLight();
    }

    private final Chunk c;
    private final BlockPos pos;
    private final IBlockState state;
    private final int[] lightUpdateBlockList = localLUBL.get();

    private CurrentCheckLight(Chunk c, BlockPos pos) {
        this.c = c;
        this.pos = pos;
        this.state = c.getBlockState(pos);
    }

    private void checkLight() {
        if (c.getWorld().provider.hasSkyLight()) {
            checkLightFor(EnumSkyBlock.SKY);
        }
        checkLightFor(EnumSkyBlock.BLOCK);
    }

    private void checkLightFor(EnumSkyBlock lightType) {
        int i = 0;
        int j = 0;
        int k = getLightFor(lightType, pos);
        int l = getRawLight(pos, lightType);
        int i1 = pos.getX();
        int j1 = pos.getY();
        int k1 = pos.getZ();

        if (l > k) {
            lightUpdateBlockList[j++] = 133152;
        } else if (l < k) {
            lightUpdateBlockList[j++] = 133152 | k << 18;

            while (i < j) {
                int l1 = lightUpdateBlockList[i++];
                int i2 = (l1 & 63) - 32 + i1;
                int j2 = (l1 >> 6 & 63) - 32 + j1;
                int k2 = (l1 >> 12 & 63) - 32 + k1;
                int l2 = l1 >> 18 & 15;
                BlockPos blockpos = new BlockPos(i2, j2, k2);
                int i3 = getLightFor(lightType, blockpos);

                if (i3 == l2) {
                    if (blockpos.getY() >= 0 && blockpos.getY() < 255) {
                        c.setLightFor(lightType, blockpos, 0);
                    }

                    if (l2 > 0) {
                        int j3 = MathHelper.abs(i2 - i1);
                        int k3 = MathHelper.abs(j2 - j1);
                        int l3 = MathHelper.abs(k2 - k1);

                        if (j3 + k3 + l3 < 17) {
                            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

                            for (EnumFacing enumfacing : EnumFacing.values()) {
                                int i4 = i2 + enumfacing.getFrontOffsetX();
                                int j4 = j2 + enumfacing.getFrontOffsetY();
                                int k4 = k2 + enumfacing.getFrontOffsetZ();
                                blockpos$pooledmutableblockpos.setPos(i4, j4, k4);
                                int l4 = Math.max(1, c.getBlockState(blockpos$pooledmutableblockpos).getBlock().getLightOpacity(
                                        c.getWorld().getBlockState(blockpos$pooledmutableblockpos), c.getWorld(), blockpos$pooledmutableblockpos));
                                i3 = getLightFor(lightType, blockpos$pooledmutableblockpos);

                                if (i3 == l2 - l4 && j < lightUpdateBlockList.length) {
                                    lightUpdateBlockList[j++] = i4 - i1 + 32 | j4 - j1 + 32 << 6 | k4 - k1 + 32 << 12 | l2 - l4 << 18;
                                }
                            }

                            blockpos$pooledmutableblockpos.release();
                        }
                    }
                }
            }

            i = 0;
        }

        while (i < j) {
            int i5 = lightUpdateBlockList[i++];
            int j5 = (i5 & 63) - 32 + i1;
            int k5 = (i5 >> 6 & 63) - 32 + j1;
            int l5 = (i5 >> 12 & 63) - 32 + k1;
            BlockPos blockpos1 = new BlockPos(j5, k5, l5);
            int i6 = getLightFor(lightType, blockpos1);
            int j6 = getRawLight(blockpos1, lightType);

            if (j6 != i6) {
                if (blockpos1.getY() >= 0 && blockpos1.getY() < 255) {
                    c.setLightFor(lightType, blockpos1, j6);
                }

                if (j6 > i6) {
                    int k6 = Math.abs(j5 - i1);
                    int l6 = Math.abs(k5 - j1);
                    int i7 = Math.abs(l5 - k1);
                    boolean flag = j < lightUpdateBlockList.length - 6;

                    if (k6 + l6 + i7 < 17 && flag) {
                        if (getLightFor(lightType, blockpos1.west()) < j6) {
                            lightUpdateBlockList[j++] = j5 - 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                        }

                        if (getLightFor(lightType, blockpos1.east()) < j6) {
                            lightUpdateBlockList[j++] = j5 + 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                        }

                        if (getLightFor(lightType, blockpos1.down()) < j6) {
                            lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                        }

                        if (getLightFor(lightType, blockpos1.up()) < j6) {
                            lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 + 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                        }

                        if (getLightFor(lightType, blockpos1.north()) < j6) {
                            lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - 1 - k1 + 32 << 12);
                        }

                        if (getLightFor(lightType, blockpos1.south()) < j6) {
                            lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 + 1 - k1 + 32 << 12);
                        }
                    }
                }
            }
        }

    }

    private int getRawLight(BlockPos pos, EnumSkyBlock lightType) {
        if (lightType == EnumSkyBlock.SKY && c.canSeeSky(pos)) {
            return 15;
        } else {
            IBlockState iblockstate = pos.equals(this.pos) ? state : c.getBlockState(pos);
            int blockLight = iblockstate.getBlock().getLightValue(iblockstate, c.getWorld(), pos);
            int i = lightType == EnumSkyBlock.SKY ? 0 : blockLight;
            int j = iblockstate.getBlock().getLightOpacity(iblockstate, c.getWorld(), pos);

            if (j >= 15 && blockLight > 0) {
                j = 1;
            }

            if (j < 1) {
                j = 1;
            }

            if (j >= 15) {
                return 0;
            } else if (i >= 14) {
                return i;
            } else {
                BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

                try {
                    for (EnumFacing enumfacing : EnumFacing.values()) {
                        blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing);
                        int k = getLightFor(lightType, blockpos$pooledmutableblockpos) - j;

                        if (k > i) {
                            i = k;
                        }

                        if (i >= 14) {
                            int l = i;
                            return l;
                        }
                    }

                    return i;
                } finally {
                    blockpos$pooledmutableblockpos.release();
                }
            }
        }
    }

    private int getLightFor(EnumSkyBlock lightType, MutableBlockPos mutablePos) {
        if (mutablePos.getY() < 0) {
            mutablePos.setY(0);
        } else if (mutablePos.getY() > 255) {
            mutablePos.setY(255);
        }
        return c.getLightFor(lightType, mutablePos);
    }

    private int getLightFor(EnumSkyBlock lightType, BlockPos pos) {
        int yDiff0 = 0 - pos.getY();
        if (yDiff0 > 0) {
            pos = pos.add(0, yDiff0, 0);
        } else {
            int yDiff255 = 255 - pos.getY();
            if (yDiff255 < 0) {
                pos = pos.add(0, yDiff255, 0);
            }
        }
        return c.getLightFor(lightType, pos);
    }

}
