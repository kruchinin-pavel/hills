package org.kpa.hills;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Landscape {
    private final List<LandscapeItem> indexedDepths;

    public Iterator<LandscapeItem> rightIterator(int index) {
        return indexedDepths.listIterator(index);
    }

    public Iterator<LandscapeItem> leftIterator(int index) {
        return new Iterator<LandscapeItem>() {
            final ListIterator<LandscapeItem> iter = indexedDepths.listIterator(index + 1);

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


    public Landscape(Integer... heights) {
        AtomicInteger index = new AtomicInteger();
        indexedDepths = Arrays.stream(heights)
                .map(depth -> new LandscapeItem(Landscape.this, index.getAndIncrement(), depth))
                .collect(Collectors.toList());
    }
}
