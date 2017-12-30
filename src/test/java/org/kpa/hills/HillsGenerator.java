package org.kpa.hills;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.Precision;

public class HillsGenerator {
    private final NormalDistribution distrib;
    private final double drift;
    private double lastHill;

    private HillsGenerator(double startHill, double drift, double sigma) {
        this.lastHill = startHill;
        this.drift = drift;
        distrib = new NormalDistribution(0, sigma);
    }

    private int nextHill() {
        lastHill *= (1 + drift + distrib.sample());
        return Math.max((int) Precision.round(lastHill, 0), 0);
    }

    public static Integer[] generateHills(int count) {
        HillsGenerator generator = new HillsGenerator(20, .02, .1);
        Integer[] ret = new Integer[count];
        for (int i = 0; i < count; i++) {
            ret[i] = generator.nextHill();
        }
        return ret;
    }
}
