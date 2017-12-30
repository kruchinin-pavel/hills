package org.kpa.hills;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Rain {
    private static final Logger logger = LoggerFactory.getLogger(Rain.class);

    public static List<Lake> drop(Landscape landscape) {
        Rain rain = new Rain();
        return new ForkJoinPool().invoke(ForkUtils.create(() -> rain.fillLakes(landscape)));
    }

    private AtomicBoolean completed = new AtomicBoolean();

    private List<Lake> fillLakes(Landscape landscape) {
        Preconditions.checkArgument(completed.compareAndSet(false, true));
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
        logger.info("Lakes amount: " + lakeTasks.size());
        return lakeTasks.stream().map(ForkJoinTask::join).collect(Collectors.toList());
    }

}
