package me.kenzierocks.hardvox.net;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SelectionMessageHandler implements IMessageHandler<SelectionMessage, IMessage> {

    @Override
    public IMessage onMessage(SelectionMessage message, MessageContext ctx) {
        return null;
    }

}
