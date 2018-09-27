package com.orionletizi.job.task;

import com.orionletizi.job.lifecycle.LifecycleContext;
import com.orionletizi.job.lifecycle.LifecycleListener;

public abstract class AbstractTask implements Task {

  private final LifecycleContext ctxt = new LifecycleContext();
  private TaskLogger logger;
  private String name;

  AbstractTask(final String name) {
    this.name = name;
  }

  @Override
  public void onLifecycleEvent(LifecycleListener listener) {
    ctxt.onLifecycleEvent(listener);
  }

  @Override
  public void setLogger(TaskLogger logger) {
    this.logger = logger;
  }

  TaskLogger getLogger() {
    return logger;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void started() {
    ctxt.started();
  }

  @Override
  public void completed() {
    ctxt.completed();
  }

  @Override
  public void error(Throwable t) {
    ctxt.error(t);
  }
}
