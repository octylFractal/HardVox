package me.kenzierocks.hardvox.commands.args;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.util.EnumFacing;

public final class CommandArguments {

    public static CommandArgument<Integer> integer(String name) {
        return new IntArg(name);
    }

    public static CommandArgument<Vector2i> position2(String name) {
        return new VecArg<>(name, 2, Vector2i::new);
    }

    public static CommandArgument<Vector3i> position3(String name) {
        return new VecArg<>(name, 3, Vector3i::new);
    }

    public static CommandArgument<EnumFacing> direction(String name) {
        return new DirectionArg(name);
    }

    public static CommandArgument<VectorMap<BlockData>> blockPattern(String name) {
        return new BlockPatternArg(name);
    }

    public static CommandArgument<AccessAction> accessAction() {
        return EnumArg.create(AccessAction.class);
    }

    public static CommandArgument<Boolean> flag(String name) {
        return new FlagArg(name);
    }

    public static CommandArgument<EBool> onOff(String name) {
        return EnumArg.create(EBool.class, name);
    }

    private CommandArguments() {
        throw new AssertionError();
    }
}
