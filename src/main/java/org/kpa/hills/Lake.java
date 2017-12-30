package org.kpa.hills;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;

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

    public static class BoundLookupTask {
        BoundLookupTask opposite;
        LandscapeItem currentItem;
        volatile LandscapeItem lastItem;
        final CountDownLatch foundLatch = new CountDownLatch(1);
        final NavigableMap<Integer, LandscapeItem> ladders = new TreeMap<>();

        boolean oppositeLowerFound() {
            return opposite.found() && opposite.lastItem.getHeight() < lastItem.getHeight();
        }

        boolean found() {
            return foundLatch.getCount() == 0;
        }

        ForkJoinTask<LandscapeItem> start(Iterator<LandscapeItem> iter) {
            return ForkUtils.fork(() -> {
                lastItem = iter.next();
                while (iter.hasNext()) {
                    currentItem = iter.next();
                    if (Thread.currentThread().isInterrupted()) {
                        return null;
                    }
                    if (lastItem.getHeight() < currentItem.getHeight()) {
                        ladders.put(currentItem.getHeight(), currentItem);
                        lastItem = currentItem;
                        if (oppositeLowerFound()) break;
                    } else if (lastItem.getHeight() > currentItem.getHeight()) {
                        foundLatch.countDown();
                        break;
                    }
                }
                foundLatch.countDown();
                if (opposite.found()) {
                    int minHeight = Math.min(lastItem.getHeight(), opposite.lastItem.getHeight());
                    lastItem = ladders.ceilingEntry(minHeight).getValue();
                    opposite.lastItem = opposite.ladders.ceilingEntry(minHeight).getValue();
                }
                return lastItem;
            });
        }

        static BoundLookupTask create() {
            BoundLookupTask statA = new BoundLookupTask();
            BoundLookupTask statB = new BoundLookupTask();
            statB.opposite = statA;
            statA.opposite = statB;
            return statA;
        }
    }

}

