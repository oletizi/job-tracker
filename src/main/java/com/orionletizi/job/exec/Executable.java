package com.orionletizi.job.exec;

// XXX: This is badly named
public interface Executable extends Runnable {
  void onCompletion(final CompletionListener listener);

  void setLogger(final ExecutableLogger logger);
}
