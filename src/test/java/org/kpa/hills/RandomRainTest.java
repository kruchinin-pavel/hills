package org.kpa.hills;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class RandomRainTest {
    private static final Logger logger = LoggerFactory.getLogger(RandomRainTest.class);
    private final Landscape landscape;

    public RandomRainTest(Integer[] data) {
        this.landscape = new Landscape(data);
    }

    @Test
    public void test() {
        long nanos = System.nanoTime();
        Collection<Lake> lakes = Rain.drop(landscape);
        logger.info("Completed at: {}sec", (System.nanoTime() - nanos) / 1e6);
        assertTrue("Lake count >0 ", lakes.size() > 0);
        assertTrue("Water volume >0", lakes.stream().mapToInt(Lake::volume).sum() > 0);
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        List<Object[]> parameters = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            parameters.add(new Object[]{HillsGenerator.generateHills(32000)});
        }
        return parameters;
    }
}