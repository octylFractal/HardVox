package me.kenzierocks.hardvox.client;

import me.kenzierocks.hardvox.HVSharedProxy;
import me.kenzierocks.hardvox.client.net.CSelectionMessageHandler;
import me.kenzierocks.hardvox.client.render.WECUIDispatcher;
import me.kenzierocks.hardvox.net.SelectionMessageHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HVClientProxy extends HVSharedProxy {
    
    private final WECUIDispatcher dispatch = new WECUIDispatcher();

    @Override
    public SelectionMessageHandler createSelectionMessageHandler() {
        return new CSelectionMessageHandler(dispatch);
    }
    
    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        dispatch.render(event.getPartialTicks());
    }

}
