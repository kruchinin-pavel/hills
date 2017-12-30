package org.kpa.hills;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Supplier;

public class ForkUtils {

    public static <T> ForkJoinTask<T> create(Supplier<T> runnable) {
        return new RecursiveTask<>() {
            @Override
            protected T compute() {
                return runnable.get();
            }
        };
    }

    public static <T> ForkJoinTask<T> fork(Supplier<T> runnable) {
        return create(runnable).fork();
    }
}
