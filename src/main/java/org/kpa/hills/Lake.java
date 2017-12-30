package org.kpa.hills;

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
            ForkJoinTask<LandscapeItem> leftTask = leftLookup.start(rootItem.leftIterator());
            ForkJoinTask<LandscapeItem> rightTask = leftLookup.opposite.start(rootItem.rightIterator());
            leftBound = leftTask.join();
            rightBound = rightTask.join();
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

        boolean oppositeLowerFound() {
            return opposite.found() && opposite.lastItem.getHeight() < lastItem.getHeight();
        }

        boolean found() {
            return foundLatch.getCount() == 0;
        }

        ForkJoinTask<LandscapeItem> start(Iterator<LandscapeItem> iter) {
            return ForkUtils.fork(() -> {
                lastItem = iter.next();
                NavigableMap<Integer, LandscapeItem> ladders = new TreeMap<>();
                while (iter.hasNext()) {
                    currentItem = iter.next();
                    if (Thread.currentThread().isInterrupted()) {
                        return null;
                    }
                    if (oppositeLowerFound()) break;
                    if (lastItem.getHeight() < currentItem.getHeight()) {
                        lastItem = currentItem;
                        ladders.put(currentItem.getHeight(), currentItem);
                    } else if (lastItem.getHeight() > currentItem.getHeight()) {
                        foundLatch.countDown();
                        break;
                    }
                }
                try {
                    opposite.foundLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
                if (oppositeLowerFound()) {
                    this.lastItem = ladders.ceilingEntry(opposite.lastItem.getHeight()).getValue();
                }
                foundLatch.countDown();
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

