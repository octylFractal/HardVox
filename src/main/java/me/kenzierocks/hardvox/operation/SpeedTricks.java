package me.kenzierocks.hardvox.operation;

import com.google.common.base.MoreObjects;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

class SpeedTricks {

    public static final class LightTrick extends BlockStateDelegate {

        private IBlockState old;

        private LightTrick(IBlockState delegate, IBlockState old) {
            super(delegate);
            this.old = old;
        }

        public void turnOff() {
            old = null;
        }

        private IBlockState state() {
            return MoreObjects.firstNonNull(old, delegate);
        }

        @Override
        public int getLightOpacity(IBlockAccess world, BlockPos pos) {
            return state().getLightOpacity(world, pos);
        }

        @Deprecated
        @Override
        public int getLightOpacity() {
            return state().getLightOpacity();
        }

        @Override
        public int getLightValue(IBlockAccess world, BlockPos pos) {
            return state().getLightValue(world, pos);
        }

        @Deprecated
        @Override
        public int getLightValue() {
            return state().getLightValue();
        }

    }

    /**
     * Tricks opacity checks in
     * {@link World#setBlockState(BlockPos, IBlockState, int)}, so we can delay
     * light updates until after everything is set.
     */
    static LightTrick lightTrick(IBlockState old, IBlockState newOriginal) {
        return new LightTrick(newOriginal, old);
    }

}
