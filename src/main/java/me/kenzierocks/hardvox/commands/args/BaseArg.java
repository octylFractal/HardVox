package me.kenzierocks.hardvox.commands.args;

public abstract class BaseArg<T> implements CommandArgument<T> {

    private final String name;

    protected BaseArg(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
