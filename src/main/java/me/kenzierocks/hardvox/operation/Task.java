package me.kenzierocks.hardvox.operation;

import java.util.concurrent.CompletableFuture;

public interface Task<V> {

    boolean done();

    void tick();
    
    void cancel();

    CompletableFuture<V> result();

}
