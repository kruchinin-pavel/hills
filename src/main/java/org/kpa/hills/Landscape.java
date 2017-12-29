package org.kpa.hills;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Landscape {
    private final LinkedList<LandscapeItem> indexedDepths;

    public ListIterator<LandscapeItem> iterator(int index) {
        return indexedDepths.listIterator(index);
    }

    public Landscape(Integer... heights) {
        AtomicInteger index = new AtomicInteger();
        indexedDepths = Arrays.stream(heights)
                .map(depth -> new LandscapeItem(Landscape.this, index.getAndIncrement(), depth))
                .collect(Collectors.toCollection(LinkedList::new));
    }


}
