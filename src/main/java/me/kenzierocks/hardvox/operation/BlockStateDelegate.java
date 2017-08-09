package me.kenzierocks.hardvox.operation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockStateDelegate implements IBlockState, IExtendedBlockState {

    protected final IBlockState delegate;

    public BlockStateDelegate(IBlockState delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param) {
        return delegate.onBlockEventReceived(worldIn, pos, id, param);
    }

    @Override
    public Collection<IProperty<?>> getPropertyKeys() {
        return delegate.getPropertyKeys();
    }

    @Override
    public <T extends Comparable<T>> T getValue(IProperty<T> property) {
        return delegate.getValue(property);
    }

    @Override
    public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
        return delegate.withProperty(property, value);
    }

    @Override
    public void neighborChanged(World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        delegate.neighborChanged(worldIn, pos, blockIn, fromPos);
    }

    @Override
    public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
        return delegate.cycleProperty(property);
    }

    @Override
    public Material getMaterial() {
        return delegate.getMaterial();
    }

    @Override
    public boolean isFullBlock() {
        return delegate.isFullBlock();
    }

    @Override
    public boolean canEntitySpawn(Entity entityIn) {
        return delegate.canEntitySpawn(entityIn);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightOpacity() {
        return delegate.getLightOpacity();
    }

    @Override
    public ImmutableMap<IProperty<?>, Comparable<?>> getProperties() {
        return delegate.getProperties();
    }

    @Override
    public Block getBlock() {
        return delegate.getBlock();
    }

    @Override
    public int getLightOpacity(IBlockAccess world, BlockPos pos) {
        return delegate.getLightOpacity(world, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightValue() {
        return delegate.getLightValue();
    }

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos) {
        return delegate.getLightValue(world, pos);
    }

    @Override
    public boolean isTranslucent() {
        return delegate.isTranslucent();
    }

    @Override
    public boolean useNeighborBrightness() {
        return delegate.useNeighborBrightness();
    }

    @Override
    public MapColor getMapColor(IBlockAccess p_185909_1_, BlockPos p_185909_2_) {
        return delegate.getMapColor(p_185909_1_, p_185909_2_);
    }

    @Override
    public IBlockState withRotation(Rotation rot) {
        return delegate.withRotation(rot);
    }

    @Override
    public IBlockState withMirror(Mirror mirrorIn) {
        return delegate.withMirror(mirrorIn);
    }

    @Override
    public boolean isFullCube() {
        return delegate.isFullCube();
    }

    @Override
    public boolean hasCustomBreakingProgress() {
        return delegate.hasCustomBreakingProgress();
    }

    @Override
    public EnumBlockRenderType getRenderType() {
        return delegate.getRenderType();
    }

    @Override
    public int getPackedLightmapCoords(IBlockAccess source, BlockPos pos) {
        return delegate.getPackedLightmapCoords(source, pos);
    }

    @Override
    public float getAmbientOcclusionLightValue() {
        return delegate.getAmbientOcclusionLightValue();
    }

    @Override
    public boolean isBlockNormalCube() {
        return delegate.isBlockNormalCube();
    }

    @Override
    public boolean isNormalCube() {
        return delegate.isNormalCube();
    }

    @Override
    public boolean canProvidePower() {
        return delegate.canProvidePower();
    }

    @Override
    public int getWeakPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return delegate.getWeakPower(blockAccess, pos, side);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return delegate.hasComparatorInputOverride();
    }

    @Override
    public int getComparatorInputOverride(World worldIn, BlockPos pos) {
        return delegate.getComparatorInputOverride(worldIn, pos);
    }

    @Override
    public float getBlockHardness(World worldIn, BlockPos pos) {
        return delegate.getBlockHardness(worldIn, pos);
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World worldIn, BlockPos pos) {
        return delegate.getPlayerRelativeBlockHardness(player, worldIn, pos);
    }

    @Override
    public int getStrongPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return delegate.getStrongPower(blockAccess, pos, side);
    }

    @Override
    public EnumPushReaction getMobilityFlag() {
        return delegate.getMobilityFlag();
    }

    @Override
    public IBlockState getActualState(IBlockAccess blockAccess, BlockPos pos) {
        return delegate.getActualState(blockAccess, pos);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        return delegate.getSelectedBoundingBox(worldIn, pos);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing) {
        return delegate.shouldSideBeRendered(blockAccess, pos, facing);
    }

    @Override
    public boolean isOpaqueCube() {
        return delegate.isOpaqueCube();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockAccess worldIn, BlockPos pos) {
        return delegate.getCollisionBoundingBox(worldIn, pos);
    }

    @Override
    public void addCollisionBoxToList(World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn,
            boolean p_185908_6_) {
        delegate.addCollisionBoxToList(worldIn, pos, entityBox, collidingBoxes, entityIn, p_185908_6_);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockAccess blockAccess, BlockPos pos) {
        return delegate.getBoundingBox(blockAccess, pos);
    }

    @Override
    public RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        return delegate.collisionRayTrace(worldIn, pos, start, end);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isTopSolid() {
        return delegate.isTopSolid();
    }

    @Override
    public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return delegate.doesSideBlockRendering(world, pos, side);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return delegate.isSideSolid(world, pos, side);
    }

    @Override
    public Vec3d getOffset(IBlockAccess access, BlockPos pos) {
        return delegate.getOffset(access, pos);
    }

    @Override
    public boolean causesSuffocation() {
        return delegate.causesSuffocation();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
        return delegate.getBlockFaceShape(worldIn, pos, facing);
    }

    // IExtendedBlockState interface

    private <T> T iebsValue(Function<IExtendedBlockState, T> func, T defaultValue) {
        if (delegate instanceof IExtendedBlockState) {
            return func.apply((IExtendedBlockState) delegate);
        }
        return defaultValue;
    }

    private <T> T iebsThrow(Function<IExtendedBlockState, T> func, Supplier<RuntimeException> ex) {
        if (delegate instanceof IExtendedBlockState) {
            return func.apply((IExtendedBlockState) delegate);
        }
        throw ex.get();
    }

    @Override
    public Collection<IUnlistedProperty<?>> getUnlistedNames() {
        return iebsValue(IExtendedBlockState::getUnlistedNames, Collections.emptyList());
    }

    @Override
    public <V> V getValue(IUnlistedProperty<V> property) {
        return iebsThrow(iebs -> iebs.getValue(property),
                () -> new IllegalArgumentException("Cannot get unlisted property " + property + " as it does not exist in " + getBlock().getBlockState()));
    }

    @Override
    public <V> IExtendedBlockState withProperty(IUnlistedProperty<V> property, V value) {
        return iebsThrow(iebs -> iebs.withProperty(property, value),
                () -> new IllegalArgumentException("Cannot set unlisted property " + property + " as it does not exist in " + getBlock().getBlockState()));
    }

    @Override
    public ImmutableMap<IUnlistedProperty<?>, Optional<?>> getUnlistedProperties() {
        return iebsValue(IExtendedBlockState::getUnlistedProperties, ImmutableMap.of());
    }

    @Override
    public IBlockState getClean() {
        return iebsValue(IExtendedBlockState::getClean, delegate);
    }

}
