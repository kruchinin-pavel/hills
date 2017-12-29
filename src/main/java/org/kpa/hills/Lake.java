package org.kpa.hills;

import com.google.common.base.Preconditions;
import org.kpa.ForkUtils;

import java.util.Iterator;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Lake {
    private final LandscapeItem rootItem;
    private LandscapeItem leftBound;
    private LandscapeItem rightBound;

    public int height() {
        return Math.min(leftBound.getHeight(), rightBound.getHeight());
    }

    public Lake(LandscapeItem rootItem) {
        this.rootItem = rootItem;
    }

    public LandscapeItem getLeftBound() {
        return leftBound;
    }

    public LandscapeItem getRightBound() {
        return rightBound;
    }

    public ForkJoinTask<Lake> findBounds() {
        return ForkUtils.fork(() -> {
            ForkJoinTask<LandscapeItem> taskLeft = ForkUtils.fork(() -> lookupNearestBound(rootItem.leftIterator()));
            ForkJoinTask<LandscapeItem> taskRight = ForkUtils.fork(() -> lookupNearestBound(rootItem.rightIterator()));
            leftBound = taskLeft.join();
            rightBound = taskRight.join();
            return Lake.this;
        });
    }

    private LandscapeItem lookupNearestBound(Iterator<LandscapeItem> iter) {
        if (Thread.currentThread().isInterrupted()) {
            return null;
        }
        LandscapeItem lastItem = rootItem;
        while (iter.hasNext()) {
            LandscapeItem item = iter.next();
            if (lastItem.getHeight() < item.getHeight()) {
                lastItem = item;
            } else if (item.getHeight() > item.getHeight()) {
                break;
            }
        }
        Preconditions.checkArgument(lastItem != rootItem, "Didn't mange to find bound.");
        return lastItem;
    }
}

