package me.kenzierocks.hardvox.region.selector;

import me.kenzierocks.hardvox.VecBridge;
import me.kenzierocks.hardvox.region.Region;
import me.kenzierocks.hardvox.region.data.RegionData;
import net.minecraft.util.EnumFacing;

abstract class BaseSelector<REGION extends Region, DATA extends RegionData> implements RegionSelector<REGION, DATA> {

    protected enum ExpandOp {
        CONTRACT("contract", "Contracted") {

            @Override
            public String directionName(EnumFacing direction) {
                return direction.getOpposite().getName2();
            }

            @Override
            public int amount(int amount) {
                return -amount;
            }
        },
        EXPAND("expand", "Expanded") {

            @Override
            public String directionName(EnumFacing direction) {
                return direction.getName2();
            }

            @Override
            public int amount(int amount) {
                return amount;
            }
        };

        public final String presentTense;
        public final String pastTense;

        private ExpandOp(String presentTense, String pastTense) {
            this.presentTense = presentTense;
            this.pastTense = pastTense;
        }

        public abstract String directionName(EnumFacing direction);

        public abstract int amount(int amount);

    }

    protected static ExpandOp expandOp(int amount) {
        return amount < 0 ? ExpandOp.CONTRACT : ExpandOp.EXPAND;
    }

    protected abstract Region getSelectingRegion();

    @Override
    public String shiftRegion(EnumFacing direction, int amount) {
        if (amount == 0) {
            return "";
        }
        getSelectingRegion().shift(VecBridge.toFlow(direction.getDirectionVec()).mul(amount));
        return "Shifted the selection " + direction.getName2() + " by " + amount + " block(s).";
    }

}
