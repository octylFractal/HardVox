package me.kenzierocks.hardvox.commands.args;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;

public class CommandParser {

    public static CommandParser init(CommandArgument<?>... arguments) {
        return new CommandParser(ImmutableList.copyOf(arguments));
    }

    private final List<CommandArgument<?>> arguments;
    private int optionalDivisor;

    public CommandParser(List<CommandArgument<?>> arguments) {
        this.arguments = ImmutableList.copyOf(arguments);
        optionalDivisor = -1;
    }

    public CommandParser markOptionalStartingWith(CommandArgument<?> arg) {
        int index = arguments.indexOf(arg);
        checkArgument(index >= 0, "argument not present in parser");
        optionalDivisor = index;
        return this;
    }

    public List<CommandArgument<?>> getArguments() {
        return arguments;
    }

    public boolean isArgumentOptional(CommandArgument<?> arg) {
        return arguments.indexOf(arg) >= optionalDivisor;
    }

    public Iterator<String> getCompletions(ParserContext context) {
        int argIndex = argIndex(context);
        CommandArgument<?> arg = arg(argIndex);
        if (arg == null) {
            return Collections.emptyIterator();
        }
        ArgumentContext argCtx = context.contextAt(argIndex);
        if (!arg.validText(argCtx)) {
            return Collections.emptyIterator();
        }
        return arg.getCompletions(argCtx);
    }

    private CommandArgument<?> arg(int argIndex) {
        return argIndex >= arguments.size() ? null : arguments.get(argIndex);
    }

    private int argIndex(ParserContext context) {
        return Math.max(0, context.text.length - 1);
    }

    @Nullable
    public CommandArgSet parse(String usage, ParserContext context) throws CommandException {
        ImmutableMap.Builder<CommandArgument<?>, Object> argMap = ImmutableMap.builder();
        if (context.text.length < optionalDivisor) {
            throw new WrongUsageException(usage);
        }
        for (int i = 0; i < context.text.length; i++) {
            CommandArgument<?> arg = arg(i);
            ArgumentContext argCtx = context.contextAt(i);
            if (arg.validText(argCtx)) {
                argMap.put(arg, arg.convert(argCtx));
            } else {
                throw new WrongUsageException("Hey, argument " + arg.getName() + " isn't formatted like that!");
            }
        }
        return new CommandArgSet(argMap.build());
    }

}
