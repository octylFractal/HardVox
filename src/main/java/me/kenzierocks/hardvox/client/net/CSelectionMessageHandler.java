package me.kenzierocks.hardvox.client.net;

import me.kenzierocks.hardvox.client.render.WECUIDispatcher;
import me.kenzierocks.hardvox.net.SelectionMessage;
import me.kenzierocks.hardvox.net.SelectionMessageHandler;
import me.kenzierocks.hardvox.region.data.RegionData;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CSelectionMessageHandler extends SelectionMessageHandler {

    private final WECUIDispatcher dispatch;

    public CSelectionMessageHandler(WECUIDispatcher dispatch) {
        this.dispatch = dispatch;
    }

    @Override
    public IMessage onMessage(SelectionMessage message, MessageContext ctx) {
        RegionData data = message.regionData;
        Minecraft.getMinecraft().addScheduledTask(() -> {
            dispatch.pickRenderRegion(data);
        });
        return super.onMessage(message, ctx);
    }

}
