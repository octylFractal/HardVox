package me.kenzierocks.hardvox.region;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class BoxRegion implements Region {

    private Vector3i pos1 = Vector3i.ZERO;
    private Vector3i pos2 = Vector3i.ZERO;

    public BoxRegion(Vector3i pos1, Vector3i pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public Vector3i getPos1() {
        return pos1;
    }

    public void setPos1(Vector3i pos1) {
        this.pos1 = pos1;
    }

    public Vector3i getPos2() {
        return pos2;
    }

    public void setPos2(Vector3i pos2) {
        this.pos2 = pos2;
    }

    @Override
    public Vector3i getMinimum() {
        return pos1.min(pos2);
    }

    @Override
    public Vector3i getMaximum() {
        return pos1.max(pos2);
    }

    @Override
    public Vector3d getCenter() {
        return getMinimum().add(getMaximum()).toDouble().div(2);
    }

    @Override
    public int getArea() {
        Vector3i dim = getDimensions();
        return dim.getX() * dim.getY() * dim.getZ();
    }

    @Override
    public Vector3i getDimensions() {
        // add one to max because the box (0,0,0)->(0,0,0) should be 1x1x1
        return getMaximum().add(Vector3i.ONE).sub(getMinimum());
    }

    @Override
    public void ensureContains(Vector3i vector) {
        Vector3i max = getMaximum();
        Vector3i min = getMinimum();
        pos1 = max.max(vector);
        pos2 = min.min(vector);
    }

    @Override
    public void ensureExcludes(Vector3i vector) {
        Vector3i max = getMaximum();
        Vector3i min = getMinimum();
        pos1 = max.min(vector);
        pos2 = min.max(vector);
    }

    @Override
    public boolean contains(Vector3i vector) {
        return vector.compareTo(getMaximum()) <= 0 && vector.compareTo(getMinimum()) >= 0;
    }

    @Override
    public void shift(Vector3i vector) {
        pos1 = pos1.add(vector);
        pos2 = pos2.add(vector);
    }

}
