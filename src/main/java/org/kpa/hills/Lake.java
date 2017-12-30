package org.kpa.hills;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
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
        final CountDownLatch latch;
        final NavigableMap<Integer, LandscapeItem> ladders = new ConcurrentSkipListMap<>();

        public BoundLookupTask(CountDownLatch latch) {
            this.latch = latch;
        }

        boolean oppositeLowerFound() {
            return latch.getCount() < 2 && opposite.lastItem.getHeight() < lastItem.getHeight();
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
                        break;
                    }
                }
                latch.countDown();
                if (latch.getCount() == 0) {
                    int minHeight = Math.min(lastItem.getHeight(), opposite.lastItem.getHeight());
                    lastItem = ladders.ceilingEntry(minHeight).getValue();
                    opposite.lastItem = opposite.ladders.ceilingEntry(minHeight).getValue();
                }
                return lastItem;
            });
        }

        static BoundLookupTask create() {
            CountDownLatch latch = new CountDownLatch(2);
            BoundLookupTask statA = new BoundLookupTask(latch);
            BoundLookupTask statB = new BoundLookupTask(latch);
            statB.opposite = statA;
            statA.opposite = statB;
            return statA;
        }
    }

}

