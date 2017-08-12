package me.kenzierocks.hardvox.commands.args;

public enum EBool {
    ON, OFF;

    public boolean toBoolean() {
        return this == ON;
    }

}
