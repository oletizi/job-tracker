package com.orionletizi.job.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orionletizi.job.lifecycle.LifecycleContext;
import com.orionletizi.job.lifecycle.LifecycleListener;

import java.io.IOException;

public abstract class AbstractTask implements Task {

  private TaskLogger logger;

  @JsonProperty
  private final LifecycleContext lifecycle = new LifecycleContext();

  @JsonProperty
  private String name;

  @SuppressWarnings("WeakerAccess")
  protected AbstractTask(final String name) {
    this.name = name;
  }

  @Override
  public void onLifecycleEvent(LifecycleListener listener) {
    lifecycle.onLifecycleEvent(listener);
  }

  @Override
  public void setLogger(TaskLogger logger) {
    this.logger = logger;
  }

  @SuppressWarnings("unused")
  protected TaskLogger getLogger() {
    return logger;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void started() {
    lifecycle.started();
  }

  @Override
  public void completed() {
    lifecycle.completed();
    try {
      logger.close();
    } catch (IOException e) {
      lifecycle.error(e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void error(Throwable t) {
    lifecycle.error(t);
    try {
      logger.close();
    } catch (IOException e) {
      // XXX: Probably need to add support for multiple errors to the lifecycle stuff
      e.printStackTrace();
    }
  }
}
