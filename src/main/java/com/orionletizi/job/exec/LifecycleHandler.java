package com.orionletizi.job.exec;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class LifecycleHandler implements CompletionListener {

  private final Set<CompletionListener> listeners = new HashSet<>();
  private boolean complete = false;
  private ExecutionResult result;
  private Status status;

  public LifecycleHandler(final Status status) {
    this.status = status;
  }

  public synchronized void onCompletion(final CompletionListener listener) {
    if (complete) {
      listener.notifyComplete(result);
    } else {
      listeners.add(listener);
    }
  }


  @Override
  public synchronized void notifyComplete(final ExecutionResult result) {
    status.stop();
    this.result = result;
    complete = true;
    for (CompletionListener listener : listeners) {
      listener.notifyComplete(result);
    }
  }

  public synchronized boolean isComplete() {
    return complete;
  }
}
