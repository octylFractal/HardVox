package me.kenzierocks.hardvox;

import me.kenzierocks.hardvox.net.SelectionMessageHandler;

public class HVSharedProxy {

    public SelectionMessageHandler createSelectionMessageHandler() {
        return new SelectionMessageHandler();
    }

}
