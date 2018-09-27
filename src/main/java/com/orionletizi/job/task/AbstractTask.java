package com.orionletizi.job.task;

import com.orionletizi.job.lifecycle.LifecycleContext;
import com.orionletizi.job.lifecycle.LifecycleListener;

import java.io.IOException;

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
    try {
      logger.close();
    } catch (IOException e) {
      ctxt.error(e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void error(Throwable t) {
    ctxt.error(t);
    try {
      logger.close();
    } catch (IOException e) {
      // XXX: Probably need to add support for multiple errors to the lifecycle stuff
      e.printStackTrace();
    }
  }
}
