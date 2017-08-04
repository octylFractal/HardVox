package me.kenzierocks.hardvox.commands.args;

import java.util.Iterator;

/**
 * An argument in a command. Build a sequence of these for your commands!
 */
public interface CommandArgument<T> {

    String getName();

    T convert(ArgumentContext context);

    boolean validText(ArgumentContext context);

    Iterator<String> getCompletions(ArgumentContext context);

}
