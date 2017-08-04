package me.kenzierocks.hardvox.net;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableMap;

import io.netty.buffer.ByteBuf;
import me.kenzierocks.hardvox.region.data.BoxRegionData;
import me.kenzierocks.hardvox.region.data.RegionData;
import me.kenzierocks.hardvox.region.data.RegionType;

public enum RegionDataCodec {
    INSTANCE;

    private interface CodecPart<RD extends RegionData> {

        RD decode(ByteBuf buffer);

        void encode(ByteBuf buffer, RD data);

    }

    private static Vector3i readVec(ByteBuf buf) {
        return new Vector3i(buf.readInt(), buf.readInt(), buf.readInt());
    }

    private static void writeVec(ByteBuf buf, Vector3i vector) {
        buf.writeInt(vector.getX());
        buf.writeInt(vector.getY());
        buf.writeInt(vector.getZ());
    }

    private final Map<RegionType, CodecPart<?>> codecParts = new ImmutableMap.Builder<RegionType, CodecPart<?>>()
            .put(RegionType.BOX, new CodecPart<BoxRegionData>() {

                @Override
                public void encode(ByteBuf buffer, BoxRegionData data) {
                    writeVec(buffer, data.pos1);
                    writeVec(buffer, data.pos2);
                }

                @Override
                public BoxRegionData decode(ByteBuf buffer) {
                    return new BoxRegionData(readVec(buffer), readVec(buffer));
                }
            })
            .build();

    public RegionData decode(ByteBuf buffer) {
        int type = buffer.readShort();
        checkArgument(type < RegionType.values().length, "type is too big!");
        return getPart(RegionType.values()[type]).decode(buffer);
    }

    public void encode(ByteBuf buffer, RegionData data) {
        RegionType regionType = data.getType();
        buffer.writeShort(regionType.ordinal());
        getPart(regionType).encode(buffer, data);
    }

    private CodecPart<RegionData> getPart(RegionType regionType) {
        @SuppressWarnings("unchecked")
        CodecPart<RegionData> part = (CodecPart<RegionData>) codecParts.get(regionType);
        checkArgument(part != null, "there's no registered CP for type %s", regionType);
        return part;
    }

}
