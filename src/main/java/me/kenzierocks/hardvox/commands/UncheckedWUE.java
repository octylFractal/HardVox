package me.kenzierocks.hardvox.commands;

import net.minecraft.command.WrongUsageException;

/**
 * {@link WrongUsageException} unchecked.
 */
public class UncheckedWUE extends RuntimeException {

    private static final long serialVersionUID = -169292164464048454L;

    private final String extraErrorText;

    public UncheckedWUE() {
        this(null);
    }

    public UncheckedWUE(String extraErrorText) {
        super("");
        this.extraErrorText = extraErrorText;
    }

    public String getExtraErrorText() {
        return extraErrorText;
    }

}
