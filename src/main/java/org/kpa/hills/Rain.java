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
        Rain rain = new Rain();
        return new ForkJoinPool().invoke(ForkUtils.create(() -> rain.fillLakes(landscape)));
    }

    private List<Lake> fillLakes(Landscape landscape) {
        List<ForkJoinTask<Lake>> lakeTasks = new ArrayList<>();
        Iterator<LandscapeItem> iter = landscape.rightIterator(0);
        LandscapeItem previous = iter.next();
        LandscapeItem current = iter.next();
        while (iter.hasNext()) {
            if (Thread.currentThread().isInterrupted()) {
                return Collections.emptyList();
            }
            LandscapeItem next = iter.next();
            if (previous.getHeight() > current.getHeight() && current.getHeight() < next.getHeight()) {
                lakeTasks.add(new Lake(current).findBounds());
            }
            previous = current;
            current = next;
        }
        return lakeTasks.stream().map(ForkJoinTask::join).collect(Collectors.toList());
    }

}
