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
        distrib = new NormalDistribution(0., sigma);
    }

    private int nextHill() {
        lastHill *= (1 + drift + distrib.sample());
        lastHill = Math.min(Math.max((int) Precision.round(lastHill, 0), 0), 32000);
        return (int) lastHill;
    }

    public static Integer[] generateHills(int count) {
        HillsGenerator generator = new HillsGenerator(16000, .0, .01);
        Integer[] ret = new Integer[count];
        for (int i = 0; i < count; i++) {
            ret[i] = generator.nextHill();
        }
        return ret;
    }
}
