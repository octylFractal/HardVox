package me.kenzierocks.hardvox.region.chunker;

import me.kenzierocks.hardvox.region.BoxRegion;

class BoxRegionChunker extends BaseRegionChunker<BoxRegion> {

    @Override
    protected boolean isInRegion(BoxRegion region, int x, int y, int z) {
        // position is always inside the box!
        return true;
    }

}
