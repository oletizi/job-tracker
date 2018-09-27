package com.orionletizi.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orionletizi.job.task.ExecutionContext;
import com.orionletizi.job.lifecycle.LifecycleContext;
import com.orionletizi.job.lifecycle.LifecycleListener;
import com.orionletizi.job.lifecycle.Status;
import logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Job implements LifecycleListener {
  private static final Logger logger = new LoggerFactory().getLoggerFor(Job.class);
  private String id;
  private String action;
  private boolean isOpen = true;
  private final Collection<ExecutionContext> executionContexts = new ArrayList<>();
  private final Status status = new Status();

  @JsonProperty
  private final LifecycleContext lifecycle = new LifecycleContext();

  @SuppressWarnings("unused")
  Job() {
    // noop for object mapper
  }

  Job(final String id, final String action) {
    this.id = id;
    this.action = action;
  }

  public void addExecutionContext(final ExecutionContext ctxt) {
    executionContexts.add(ctxt);
  }

  // Properties

  @JsonProperty
  public String getId() {
    return id;
  }

  @JsonProperty
  public String getAction() {
    return action;
  }

  public void onLifecycleEvent(final LifecycleListener listener) {
    lifecycle.onLifecycleEvent(listener);
  }

  @JsonProperty
  public synchronized List<ExecutionContext> getExecutions() {
    return new ArrayList<>(executionContexts);
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
}
