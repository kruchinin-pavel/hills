package org.kpa.hills;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class LakeBoundsTest {

    private final int lowIndex;
    private final int leftIndex;
    private final int rightIndex;
    private final Landscape landscape;

    public LakeBoundsTest(int leftIndex, int lowIndex, int rightIndex, Landscape landscape) {
        this.lowIndex = lowIndex;
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
        this.landscape = landscape;
    }

    @Test
    public void makeLakeTest() {
        Lake lake = new Lake(landscape.rightIterator(lowIndex).next()).findBounds().join();
        Assert.assertEquals("Left bound", leftIndex, lake.getLeftBound().getIndex());
        Assert.assertEquals("Right bound", rightIndex, lake.getRightBound().getIndex());
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(
                //  Index:                                  0, 1, 2, 3, 4, 5, 6, 7, 8
                new Object[]{0, 1, 4, new Landscape(5, 2, 3, 4, 5, 4, 1, 3, 1)},
                new Object[]{0, 1, 2, new Landscape(2, 1, 2, 2, 2, 1, 1, 1, 1)},
                new Object[]{5, 6, 7, new Landscape(5, 2, 3, 4, 5, 4, 1, 3, 1)},
                new Object[]{0, 1, 2, new Landscape(2, 1, 2, 2, 2, 3, 1, 1, 1)},
                new Object[]{2, 3, 4, new Landscape(5, 3, 5, 3, 5)}
        );
    }


}