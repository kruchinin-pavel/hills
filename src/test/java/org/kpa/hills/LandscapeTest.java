package org.kpa.hills;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LandscapeTest {
    @Test
    public void testIterators() {
        Landscape landscape = new Landscape(0, 1, 2, 3, 4);
        assertEquals(3, landscape.rightIterator(3).next().getIndex());
        assertEquals(3, landscape.leftIterator(3).next().getIndex());
        assertEquals(4, landscape.leftIterator(4).next().getIndex());
    }

}