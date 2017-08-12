package me.kenzierocks.hardvox.operation;

import java.util.concurrent.CompletableFuture;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

public class TaskManager {

    private final ListMultimap<Class<?>, Task<?>> taskQueue = LinkedListMultimap.create();

    public <V> CompletableFuture<V> submit(Task<V> task) {
        taskQueue.get(task.getClass()).add(0, task);
        return task.result();
    }

    public void runTasks() {
        Multimaps.asMap(ImmutableListMultimap.copyOf(taskQueue)).values().forEach(tasks -> {
            int size = tasks.size();
            while (size > 0) {
                int last = size - 1;
                Task<?> current = tasks.get(last);
                if (current.done()) {
                    taskQueue.get(current.getClass()).remove(last);
                    size--;
                    continue;
                }
                current.tick();
                return;
            }
        });
    }

    public void cancelTasks() {
        taskQueue.values().forEach(Task::cancel);
        taskQueue.clear();
    }

}
