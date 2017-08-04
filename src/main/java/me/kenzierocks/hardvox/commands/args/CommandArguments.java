package me.kenzierocks.hardvox.commands.args;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;

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

    private CommandArguments() {
        throw new AssertionError();
    }
}
