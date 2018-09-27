package com.orionletizi.job.task;

import com.orionletizi.job.lifecycle.LifecycleListener;

public interface Task extends Runnable, LifecycleListener {
  void onLifecycleEvent(final LifecycleListener listener);
  void setLogger(final TaskLogger logger);
  @SuppressWarnings("unused")
  String getName();
}
