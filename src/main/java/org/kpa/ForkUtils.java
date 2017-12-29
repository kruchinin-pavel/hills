package org.kpa;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Supplier;

public class ForkUtils {
    public static <T> ForkJoinTask<T> fork(Supplier<T> runnable) {
        return new RecursiveTask<T>() {
            @Override
            protected T compute() {
                return runnable.get();
            }
        }.fork();
    }
}
