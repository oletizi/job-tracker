package com.orionletizi.job.exec;

// XXX: This is not well named
public interface Task extends Runnable {
  void onCompletion(final CompletionListener listener);

  void setLogger(final ExecutableLogger logger);
}
