package com.orionletizi.job.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orionletizi.job.lifecycle.LifecycleContext;
import com.orionletizi.job.lifecycle.LifecycleListener;

import java.io.IOException;

public abstract class AbstractTask implements Task {

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


  abstract TaskLogger getLogger();

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
    closeLogger();
  }

  @Override
  public void error(Throwable t) {
    lifecycle.error(t);
    closeLogger();
  }

  private void closeLogger() {
    final TaskLogger logger = getLogger();
    if (logger != null) {
      try {
        logger.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
