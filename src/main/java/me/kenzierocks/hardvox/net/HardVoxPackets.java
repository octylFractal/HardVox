package me.kenzierocks.hardvox.net;

import me.kenzierocks.hardvox.HardVox;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class HardVoxPackets {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(HardVox.MODID);

    static {
        initPackets();
    }

    private static void initPackets() {
        int disc = 0;
        INSTANCE.registerMessage(HardVox.getProxy().createSelectionMessageHandler(),
                SelectionMessage.class, disc++, Side.CLIENT);
    }

}
