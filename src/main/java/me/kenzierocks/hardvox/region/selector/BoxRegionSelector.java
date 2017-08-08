package me.kenzierocks.hardvox.region.selector;

import java.util.Optional;

import com.flowpowered.math.vector.Vector3i;

import me.kenzierocks.hardvox.Points;
import me.kenzierocks.hardvox.region.BoxRegion;
import me.kenzierocks.hardvox.region.Region;
import me.kenzierocks.hardvox.region.data.BoxRegionData;
import me.kenzierocks.hardvox.vector.VecBridge;
import net.minecraft.util.EnumFacing;

public class BoxRegionSelector extends BaseSelector<BoxRegion, BoxRegionData> {

    private final BoxRegion region = new BoxRegion(Points.INVALID_POINT, Points.INVALID_POINT);

    @Override
    public String selectPrimary(Vector3i selected) {
        if (region.getPos1().equals(selected)) {
            return "";
        }
        region.setPos1(selected);
        return "Set position #1 to " + selected;
    }

    @Override
    public String selectSecondary(Vector3i selected) {
        if (region.getPos2().equals(selected)) {
            return "";
        }
        region.setPos2(selected);
        return "Set position #2 to " + selected;
    }

    @Override
    public String expandRegion(EnumFacing direction, int amount) {
        if (amount == 0) {
            return "";
        }
        ExpandOp op = expandOp(amount);
        final String partialDesc = " the selection " + op.directionName(direction) + " by " + op.amount(amount) + " block(s)";
        if (op == ExpandOp.CONTRACT) {
            // verify this won't move too far
            int pos1Axis = VecBridge.getAxisValue(direction.getAxis(), region.getPos1());
            int pos2Axis = VecBridge.getAxisValue(direction.getAxis(), region.getPos2());
            if (Math.abs(pos1Axis - pos2Axis) < Math.abs(amount)) {
                // this will move one past the other, not allowed!
                throw new IllegalArgumentException("Cannot " + op.presentTense + partialDesc + ", it is too small in that direction.");
            }
        }
        Vector3i moveVec = VecBridge.toFlow(direction.getDirectionVec()).mul(amount);
        if (VecBridge.vecForDirection(direction, region.getPos1(), region.getPos2()) == region.getPos1()) {
            // move pos1
            region.setPos1(region.getPos1().add(moveVec));
        } else {
            // move pos1
            region.setPos2(region.getPos2().add(moveVec));
        }
        return op.pastTense + partialDesc + ".";
    }

    @Override
    public String expandRegionVertically() {
        Vector3i p1 = region.getPos1();
        Vector3i p2 = region.getPos2();
        if (p1.getY() >= p2.getY()) {
            region.setPos1(new Vector3i(p1.getX(), 255, p1.getZ()));
            region.setPos2(new Vector3i(p2.getX(), 0, p2.getZ()));
        } else {
            region.setPos1(new Vector3i(p1.getX(), 0, p1.getZ()));
            region.setPos2(new Vector3i(p2.getX(), 255, p2.getZ()));
        }
        return "Expanded the selection vertically.";
    }

    @Override
    public boolean isRegionDefined() {
        return region.getPos1() != Points.INVALID_POINT && region.getPos2() != Points.INVALID_POINT;
    }

    @Override
    public Optional<BoxRegion> getRegion() {
        return isRegionDefined() ? Optional.of(region) : Optional.empty();
    }

    @Override
    protected Region getSelectingRegion() {
        return region;
    }

    @Override
    public BoxRegionData getSelectorInformation() {
        return new BoxRegionData(region.getPos1(), region.getPos2());
    }

}
