package com.orionletizi.job.exec;

import java.util.HashSet;
import java.util.Set;

public class CompletionHandler implements CompletionListener{

  private final Set<CompletionListener> listeners = new HashSet<>();
  private boolean complete = false;
  private ExecutionResult result;

  public synchronized void onCompletion(final CompletionListener listener) {
    if (complete) {
      listener.notifyComplete(result);
    } else {
      listeners.add(listener);
    }
  }


  @Override
  public synchronized void notifyComplete(final ExecutionResult result) {
    this.result = result;
    complete = true;
    for (CompletionListener listener : listeners) {
      listener.notifyComplete(result);
    }
  }
}
