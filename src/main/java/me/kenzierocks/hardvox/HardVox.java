package me.kenzierocks.hardvox;

import me.kenzierocks.hardvox.commands.region.RegionCommands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = HardVox.MODID, version = HardVox.VERSION)
public class HardVox {

    public static final String MODID = "hardvox";
    public static final String VERSION = "1.0.0";
    @Instance
    private static HardVox instance;

    public static HardVox getInstance() {
        return instance;
    }

    @SidedProxy(clientSide = "me.kenzierocks.hardvox.client.HVClientProxy",
            serverSide = "me.kenzierocks.hardvox.HVSharedProxy")
    private static HVSharedProxy proxy;

    public static HVSharedProxy getProxy() {
        return proxy;
    }
    
    @EventHandler
    public void construct(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(proxy);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        new RegionCommands().addCommands(event::registerServerCommand);
    }
}
