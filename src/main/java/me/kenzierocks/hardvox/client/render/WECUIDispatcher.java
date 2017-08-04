package me.kenzierocks.hardvox.client.render;

import com.flowpowered.math.vector.Vector3i;
import com.mumfrey.worldeditcui.render.RenderWrap;
import com.mumfrey.worldeditcui.render.region.CuboidRegion;
import com.mumfrey.worldeditcui.render.region.Region;
import com.mumfrey.worldeditcui.util.Vector3;

import me.kenzierocks.hardvox.Points;
import me.kenzierocks.hardvox.region.data.BoxRegionData;
import me.kenzierocks.hardvox.region.data.RegionData;
import net.minecraft.client.Minecraft;

/**
 * Takes region data and dispatches to the correct renderer.
 */
public class WECUIDispatcher {

    private final RenderWrap renderWrap = new RenderWrap();
    private Region renderRegion;

    public void render(float partialTicks) {
        if (renderRegion != null) {
            renderWrap.preRender();
            try {
                renderRegion.render(new Vector3(Minecraft.getMinecraft().getRenderViewEntity(), partialTicks), partialTicks);
            } finally {
                renderWrap.postRender();
            }
        }
    }

    public void pickRenderRegion(RegionData data) {
        switch (data.getType()) {
            case BOX:
                renderRegion = setCuboidRegion((BoxRegionData) data);
                break;
            default:
                throw new IllegalArgumentException("Unhandled data type " + data.getType());
        }
    }

    private Region setCuboidRegion(BoxRegionData data) {
        CuboidRegion region = new CuboidRegion();
        setCuboidPoint(region, 0, data.pos1);
        setCuboidPoint(region, 1, data.pos2);
        return region;
    }

    private void setCuboidPoint(Region region, int number, Vector3i vec) {
        if (!vec.equals(Points.INVALID_POINT)) {
            region.setCuboidPoint(number, vec.getX(), vec.getY(), vec.getZ());
        }
    }

}
