package org.kpa.hills;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class RainTest {
    private final int lakeCount;
    private final int waterVolume;
    private final Landscape landscape;

    public RainTest(int lakeCount, int waterVolume, Landscape landscape) {
        this.lakeCount = lakeCount;
        this.waterVolume = waterVolume;
        this.landscape = landscape;
    }

    @Test
    public void test() {
        List<Lake> lakes = Rain.drop(landscape);
        assertEquals("Lake count", lakeCount, lakes.size());
        assertEquals("Water volume", waterVolume, lakes.stream().mapToInt(Lake::volume).sum());
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(
                new Object[]{2, 8, new Landscape(5, 2, 3, 4, 5, 4, 1, 3, 1)},
                new Object[]{2, 3, new Landscape(5, 3, 5, 4, 5)});
    }


}