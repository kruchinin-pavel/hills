package org.kpa.hills;

import java.util.Iterator;
import java.util.Objects;

public class LandscapeItem {
    private int index;
    private int height;
    private final Landscape landscape;

    public LandscapeItem(Landscape landscape, int index, int height) {
        this.landscape = landscape;
        this.index = index;
        this.height = height;
    }

    public Iterator<LandscapeItem> rightIterator() {
        return landscape.rightIterator(index);
    }

    public Iterator<LandscapeItem> leftIterator() {
        return landscape.leftIterator(index);
    }

    public int getIndex() {
        return index;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandscapeItem)) return false;
        LandscapeItem that = (LandscapeItem) o;
        return getIndex() == that.getIndex();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIndex());
    }

    @Override
    public String toString() {
        return "LandscapeItem{" +
                "index=" + index +
                ", height=" + height +
                '}';
    }
}

