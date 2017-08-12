package me.kenzierocks.hardvox.vector;

public class Masker {

    public static final Masker CHUNK = new Masker(4, 8, 4);

    private final int size;
    private final int xShift;
    private final int zShift;
    private final int yShift;
    private final int xMask;
    private final int zMask;
    private final int yMask;

    public Masker(int xBits, int yBits, int zBits) {
        xShift = xBits + zBits + yBits;
        zShift = zBits + yBits;
        yShift = yBits;
        xMask = (1 << xShift) - 1;
        zMask = (1 << zShift) - 1;
        yMask = (1 << yShift) - 1;
        size = (1 << xBits) * (1 << yBits) * (1 << zBits);
    }

    public int size() {
        return size;
    }

    public int index(int x, int y, int z) {
        return ((y & yMask) << yShift)
                | ((z & zMask) << zShift)
                | ((x & xMask) << xShift);
    }

    public int xFromIndex(int index) {
        return (index >>> xShift) & xMask;
    }

    public int zFromIndex(int index) {
        return (index >>> zShift) & zMask;
    }

    public int yFromIndex(int index) {
        return (index >>> yShift) & yMask;
    }

}
