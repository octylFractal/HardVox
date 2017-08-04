package me.kenzierocks.hardvox.net;

import io.netty.buffer.ByteBuf;
import me.kenzierocks.hardvox.region.data.RegionData;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SelectionMessage implements IMessage {

    public RegionData regionData;

    public SelectionMessage setRegionData(RegionData regionData) {
        this.regionData = regionData;
        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        regionData = RegionDataCodec.INSTANCE.decode(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        RegionDataCodec.INSTANCE.encode(buf, regionData);
    }

}
