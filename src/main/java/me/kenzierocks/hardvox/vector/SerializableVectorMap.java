package me.kenzierocks.hardvox.vector;

public interface SerializableVectorMap<V> extends VectorMap<V> {

    interface EntryIterator<V> {

        boolean hasNext();

        void next();

        int getX();

        int getY();

        int getZ();

        V getValue();

    }

    EntryIterator<V> iterateEntries();

    int getSize();

}
