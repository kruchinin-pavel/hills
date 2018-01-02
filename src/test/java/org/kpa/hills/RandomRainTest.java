package org.kpa.hills;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class RandomRainTest {
    private final Landscape landscape;

    public RandomRainTest(Integer[] data) {
        this.landscape = new Landscape(data);
    }

    @Test
    public void test() {
        Collection<Lake> lakes = Rain.drop(landscape);
        assertTrue("Lake count >0 ", lakes.size() > 0);
        assertTrue("Water volume >0", lakes.stream().mapToInt(Lake::volume).sum() > 0);
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        List<Object[]> parameters = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            parameters.add(new Object[]{HillsGenerator.generateHills(32000)});
        }
        return parameters;
    }
}