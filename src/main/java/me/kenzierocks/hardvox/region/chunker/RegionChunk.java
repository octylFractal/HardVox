package me.kenzierocks.hardvox.region.chunker;

public interface RegionChunk {

    interface PositionConsumer {

        int accept(int x, int y, int z);

    }

    interface PositionIterator {

        boolean hasNext();

        int next(PositionConsumer nextConsumer);

        default int forEachRemaining(PositionConsumer consumer) {
            int ops = 0;
            while (hasNext()) {
                ops += next(consumer);
            }
            return ops;
        }

    }

    PositionIterator iterator();

    int getX();

    int getZ();

}
