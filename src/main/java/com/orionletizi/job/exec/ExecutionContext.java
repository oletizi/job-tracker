package com.orionletizi.job.exec;

import com.fasterxml.jackson.annotation.JsonProperty;
import logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public class ExecutionContext {
  private static final Logger logger = new LoggerFactory().getLoggerFor(ExecutionContext.class);
  private final String id;
  private final String[] command;
  private String stdoutName;
  private String stderrName;
  private Collection<CompletionListener> listeners = new ArrayList<>();
  private ExecutionResult result;

  public ExecutionContext(final String id, final String[] command) {
    this.id = id;
    this.command = command;
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

  ExecutionContext setStdoutName(String stdoutName) {
    this.stdoutName = stdoutName;
    return this;
  }

  ExecutionContext setStderrName(String stderrName) {
    this.stderrName = stderrName;
    return this;
  }

  public synchronized void notifyComplete(final ExecutionResult result) {
    logger.info("notifyComplete(result: " + result + ")");
    if (this.result != null) {
      throw new RuntimeException("Attempt to notify complete more than once!");
    }
    this.result = result;
    for (CompletionListener listener : listeners) {
      listener.notifyComplete(result);
    }
  }


  public synchronized void onCompletion(CompletionListener listener) {
    if (result != null) {
      // we're already complete
      listener.notifyComplete(result);
    } else {
      // still waiting for completion.
      listeners.add(listener);
    }
  }

}
