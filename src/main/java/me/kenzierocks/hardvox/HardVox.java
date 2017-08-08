package me.kenzierocks.hardvox;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import me.kenzierocks.hardvox.commands.CommandModule;
import me.kenzierocks.hardvox.commands.op.OperationCommands;
import me.kenzierocks.hardvox.commands.region.RegionCommands;
import me.kenzierocks.hardvox.region.SelectionListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;

@Mod(modid = HardVox.MODID, version = HardVox.VERSION, acceptedMinecraftVersions = "1.12", useMetadata = true,
        guiFactory = "me.kenzierocks.hardvox.client.gui.HVGuiFactory")
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
        EventBus bus = MinecraftForge.EVENT_BUS;
        bus.register(proxy);
        bus.register(new SelectionListener());
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        Iterator<CommandModule> modules = Iterators.forArray(
                new RegionCommands(),
                new OperationCommands());
        modules.forEachRemaining(m -> m.addCommands(event::registerServerCommand));
    }
}
