package org.kpa.hills;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class RainTest {
    private final int lakeCount;
    private final int waterVolume;
    private final Landscape landscape;
    private final Landscape landscapeRev;

    public RainTest(int lakeCount, int waterVolume, Integer[] heights) {
        landscape = new Landscape(heights);
        List<Integer> reverted = Arrays.asList(heights);
        Collections.reverse(reverted);
        landscapeRev = new Landscape(reverted.toArray(new Integer[heights.length]));
        this.lakeCount = lakeCount;
        this.waterVolume = waterVolume;
    }

    @Test
    public void test() {
        Collection<Lake> lakes = Rain.drop(landscape);
        assertEquals("Lake count", lakeCount, lakes.size());
        assertEquals("Water volume", waterVolume, lakes.stream().mapToInt(Lake::volume).sum());

        lakes = Rain.drop(landscapeRev);
        assertEquals("Lake count", lakeCount, lakes.size());
        assertEquals("Water volume", waterVolume, lakes.stream().mapToInt(Lake::volume).sum());
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(
                // Original test case from FxPro
                new Object[]{2, 8, new Integer[]{5, 2, 3, 4, 5, 4, 1, 3, 1}},
                // Rest test cases
                new Object[]{1, 2, new Integer[]{1, 2, 3, 4, 5, 4, 1, 3, 1}},
                new Object[]{0, 0, new Integer[]{1, 2, 3, 4, 5, 4, 3, 2, 1}},
                new Object[]{2, 3, new Integer[]{5, 3, 5, 4, 5}},
                new Object[]{1, 4, new Integer[]{5, 3, 3, 5, 5, 5}},
                new Object[]{0, 0, new Integer[]{5, 5, 5, 5, 3, 3}});
    }

}