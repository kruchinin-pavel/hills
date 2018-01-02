package org.kpa.hills;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Lake {
    private final LandscapeItem rootItem;
    private LandscapeItem leftBound;
    private LandscapeItem rightBound;

    public Lake(LandscapeItem rootItem) {
        this.rootItem = rootItem;
    }

    public ForkJoinTask<Lake> findBounds() {
        return ForkUtils.fork(() -> {
            BoundLookupTask leftLookup = BoundLookupTask.create();
            Arrays.asList(leftLookup.start(rootItem.leftIterator()),
                    leftLookup.opposite.start(rootItem.rightIterator())).
                    forEach(ForkJoinTask::join);
            leftBound = leftLookup.lastItem;
            rightBound = leftLookup.opposite.lastItem;
            return Lake.this;
        });
    }

    public LandscapeItem getLeftBound() {
        return leftBound;
    }

    public LandscapeItem getRightBound() {
        return rightBound;
    }


    public int volume() {
        int volume = 0;
        int height = Math.min(leftBound.getHeight(), rightBound.getHeight());
        Iterator<LandscapeItem> iter = leftBound.rightIterator();
        while (iter.hasNext()) {
            LandscapeItem item = iter.next();
            if (item.getIndex() >= rightBound.getIndex()) {
                break;
            }
            volume += Math.max(height - item.getHeight(), 0);
        }
        return volume;
    }

    private static class BoundLookupTask {
        BoundLookupTask opposite;
        volatile LandscapeItem lastItem;
        final AtomicInteger taskCount;
        final NavigableMap<Integer, LandscapeItem> ladders = new TreeMap<>();

        BoundLookupTask(AtomicInteger taskCount) {
            this.taskCount = taskCount;
        }

        boolean oppositeLowerFound() {
            return taskCount.get() < 2 && opposite.lastItem.getHeight() < lastItem.getHeight();
        }

        ForkJoinTask<LandscapeItem> start(Iterator<LandscapeItem> iter) {
            return ForkUtils.fork(() -> {
                lastItem = iter.next();
                while (iter.hasNext()) {
                    LandscapeItem currentItem = iter.next();
                    if (Thread.currentThread().isInterrupted()) {
                        return null;
                    }
                    if (lastItem.getHeight() < currentItem.getHeight()) {
                        ladders.put(currentItem.getHeight(), currentItem);
                        lastItem = currentItem;
                        if (oppositeLowerFound()) break;
                    } else if (lastItem.getHeight() > currentItem.getHeight()) {
                        break;
                    }
                }
                if (taskCount.decrementAndGet() == 0) {
                    int minHeight = Math.min(lastItem.getHeight(), opposite.lastItem.getHeight());
                    lastItem = ladders.ceilingEntry(minHeight).getValue();
                    opposite.lastItem = opposite.ladders.ceilingEntry(minHeight).getValue();
                }
                return lastItem;
            });
        }

        static BoundLookupTask create() {
            AtomicInteger taskCount = new AtomicInteger(2);
            BoundLookupTask statA = new BoundLookupTask(taskCount);
            BoundLookupTask statB = new BoundLookupTask(taskCount);
            statB.opposite = statA;
            statA.opposite = statB;
            return statA;
        }
    }
}

