package me.kenzierocks.hardvox.commands.args;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import me.kenzierocks.hardvox.commands.UncheckedWUE;

public class CommandArgSet {

    private final Map<CommandArgument<?>, Object> parsedArgs;

    public CommandArgSet(Map<CommandArgument<?>, Object> parsedArgs) {
        this.parsedArgs = ImmutableMap.copyOf(parsedArgs);
    }

    public boolean hasValue(CommandArgument<?> argument) {
        return parsedArgs.containsKey(argument);
    }

    public <T> T value(CommandArgument<T> argument) {
        @SuppressWarnings("unchecked")
        T val = (T) parsedArgs.get(argument);
        if (argument instanceof FlagArg && val == null) {
            // Convenient return value for flags
            // unchecked OK because FlagArgs are always boolean
            @SuppressWarnings("unchecked")
            T t = (T) (Object) false;
            return t;
        }
        if (val == null) {
            throw new UncheckedWUE("Missing argument " + argument.getName());
        }
        return val;
    }

}
