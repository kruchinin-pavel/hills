package org.kpa.hills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

public class Rain {

    public static List<Lake> drop(Landscape landscape) {
        return new ForkJoinPool().invoke(ForkUtils.fork(() -> fillLakes(landscape)));
    }

    private static List<Lake> fillLakes(Landscape landscape) {
        List<ForkJoinTask<Lake>> lakeTasks = new ArrayList<>();
        Lake lastLake = null;
        Iterator<LandscapeItem> iter = landscape.rightIterator(0);
        LandscapeItem previous = iter.next();
        while (iter.hasNext()) {
            if (Thread.currentThread().isInterrupted()) {
                return Collections.emptyList();
            }
            LandscapeItem current = iter.next();
            if (previous.getHeight() > current.getHeight()) {
                lastLake = null;
            }
            if (previous.getHeight() < current.getHeight() && lastLake == null) {
                lastLake = new Lake(previous);
                lakeTasks.add(lastLake.findBounds());
            }
            previous = current;
        }
        return lakeTasks.stream().map(ForkJoinTask::join).collect(Collectors.toList());
    }

}
