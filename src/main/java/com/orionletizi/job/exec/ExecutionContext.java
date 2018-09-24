package com.orionletizi.job.exec;

import com.fasterxml.jackson.annotation.JsonProperty;
import logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ExecutionContext {
  private static final Logger logger = new LoggerFactory().getLoggerFor(ExecutionContext.class);
  private final Status status = new Status();
  private final LifecycleHandler lifecycleHandler = new LifecycleHandler(status);
  private final String id;
  private final String[] command;
  private final Task task;
  private String stdoutName;
  private String stderrName;
  private Collection<CompletionListener> listeners = new ArrayList<>();
  private ExecutionResult result;

  public ExecutionContext(final String id, final String[] command) {
    this.id = id;
    this.command = command;
    this.task = null;
  }

  public ExecutionContext(final String id, final Task task) {
    this.id = id;
    this.task = task;
    this.command = new String[0];
  }

  @JsonProperty
  public String getId() {
    return id;
  }

  @JsonProperty
  public List<String> getCommand() {
    return Arrays.asList(command);
  }

  @JsonProperty
  public String getStdoutName() {
    return stdoutName;
  }

  @JsonProperty
  public String getStderrName() {
    return stderrName;
  }

  @JsonProperty
  public String complete() {
    return lifecycleHandler.isComplete() ? "COMPLETE" : "IN-PROGRESS";
  }

  void setStdoutName(String stdoutName) {
    this.stdoutName = stdoutName;
  }

  void setStderrName(String stderrName) {
    this.stderrName = stderrName;
  }

  public synchronized void notifyComplete(final ExecutionResult result) {
    lifecycleHandler.notifyComplete(result);
  }


  public synchronized void onCompletion(CompletionListener listener) {
    lifecycleHandler.onCompletion(listener);
  }

  public Task getTask() {
    return task;
  }
}
