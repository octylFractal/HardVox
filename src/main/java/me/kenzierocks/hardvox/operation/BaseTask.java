package me.kenzierocks.hardvox.operation;

import java.util.concurrent.CompletableFuture;

public abstract class BaseTask<V> implements Task<V> {

    private final CompletableFuture<V> result;
    private boolean firstTick = false;

    protected BaseTask() {
        this(new CompletableFuture<>());
    }

    protected BaseTask(CompletableFuture<V> result) {
        this.result = result;
    }

    protected void onFirstTick() {
    }

    protected abstract void onTick() throws Exception;

    @Override
    public boolean done() {
        return result.isDone();
    }

    @Override
    public void tick() {
        if (!firstTick) {
            onFirstTick();
            firstTick = true;
        }
        try {
            onTick();
        } catch (Exception e) {
            completeExceptionally(e);
        }
    }
    
    protected void completeExceptionally(Exception e) {
        result.completeExceptionally(e);
    }

    protected void complete(V value) {
        result.complete(value);
    }

    @Override
    public void cancel() {
        result.cancel(false);
    }

    @Override
    public CompletableFuture<V> result() {
        return result;
    }

}
