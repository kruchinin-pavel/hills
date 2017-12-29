package org.kpa.hills;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class LakeBoundsTest {

    private final int index;
    private final int leftBound;
    private final int rightBound;
    private final Landscape landscape;

    public LakeBoundsTest(int index, int leftBound, int rightBound, Landscape landscape) {

        this.index = index;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.landscape = landscape;
    }

    @Test
    public void makeLakeTest() {
        Lake lake = new Lake(landscape.iterator(index).next()).findBounds().join();
        Assert.assertEquals(leftBound, lake.getLeftBound().getIndex());
        Assert.assertEquals(rightBound, lake.getRightBound().getIndex());
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(
                new Object[]{1, 0, 4, new Landscape(5, 2, 3, 4, 5, 4, 1, 3, 1)},
                new Object[]{1, 0, 2, new Landscape(2, 1, 2, 2, 2, 1, 1, 1, 1)},
                new Object[]{6, 5, 7, new Landscape(2, 1, 2, 2, 2, 1, 1, 1, 1)},
                new Object[]{1, 0, 5, new Landscape(2, 1, 2, 2, 2, 3, 1, 1, 1)},
                new Object[]{3, 2, 4, new Landscape(5, 3, 5, 3, 5)}
        );
    }


}