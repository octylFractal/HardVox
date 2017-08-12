package me.kenzierocks.hardvox.session;

public enum SessionFlag {
    LIGHT_UPDATES("lightUpdates", true), BLOCK_UPDATES("blockUpdates", true);

    public final String id;
    public final boolean onByDefault;

    SessionFlag(String id, boolean onByDefault) {
        this.id = id;
        this.onByDefault = onByDefault;
    }

}
