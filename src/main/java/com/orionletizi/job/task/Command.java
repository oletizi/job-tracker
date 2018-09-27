package com.orionletizi.job.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orionletizi.job.lifecycle.LifecycleContext;
import com.orionletizi.job.lifecycle.LifecycleListener;

public class Command implements LifecycleListener {

  private String[] command;

  @JsonProperty
  private LifecycleContext lifecycle = new LifecycleContext();

  public Command(final String[] command) {
    this.command = command;
  }

  @JsonProperty
  public String[] getCommand() {
    return command;
  }

  @Override
  public void started() {
    lifecycle.started();
  }

  @Override
  public void completed() {
    lifecycle.completed();
  }

  @Override
  public void error(Throwable t) {
    lifecycle.error(t);
  }

  public void onLifecycleEvent(final LifecycleListener listener) {
    lifecycle.onLifecycleEvent(listener);
  }
}
