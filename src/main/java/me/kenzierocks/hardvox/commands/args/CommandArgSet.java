package me.kenzierocks.hardvox.commands.args;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

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
        checkNotNull(val, "No such argument %s", argument.getName());
        return val;
    }

}
