package me.kenzierocks.hardvox.commands.args;

import java.util.Collections;
import java.util.Iterator;

/**
 * Special-case argument, it's either there or not.
 */
class FlagArg extends BaseArg<Boolean> {

    FlagArg(String name) {
        super("-" + name);
    }

    @Override
    public Boolean convert(ArgumentContext context) {
        return true;
    }

    @Override
    public boolean validText(ArgumentContext context) {
        return getName().startsWith(context.text);
    }

    @Override
    public Iterator<String> getCompletions(ArgumentContext context) {
        return Collections.singleton(context.text).iterator();
    }

}
