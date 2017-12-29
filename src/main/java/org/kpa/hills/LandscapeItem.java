package org.kpa.hills;

import com.google.common.base.Preconditions;

import java.util.*;

public class LandscapeItem {
    private int index;
    private int height;
    private final Landscape landscape;
    private ListIterator<LandscapeItem> iter;
    private List<Lake> belongstoLakes = new ArrayList<>();

    public LandscapeItem(Landscape landscape, int index, int height) {
        this.landscape = landscape;
        this.index = index;
        this.height = height;
    }

    public Lake boundToLake(Lake lake) {
        Preconditions.checkNotNull(lake, "Lake is null");
        belongstoLakes.add(lake);
        return lake;
    }

    public Iterator<LandscapeItem> rightIterator() {
        return landscape.iterator(index);
    }

    public Iterator<LandscapeItem> leftIterator() {
        return new Iterator<>() {
            private final ListIterator<LandscapeItem> iter = landscape.iterator(index);

            @Override
            public boolean hasNext() {
                return iter.hasPrevious();
            }

            @Override
            public LandscapeItem next() {
                return iter.previous();
            }
        };
    }

    public Landscape getLandscape() {
        return landscape;
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

